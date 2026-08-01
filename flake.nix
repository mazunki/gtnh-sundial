{
  description = "Dev wrappers for the Sundial GTNH mod";

  inputs.nixpkgs.url = "github:NixOS/nixpkgs/25.05";

  outputs = { self, nixpkgs }:
  let
    system = "x86_64-linux";
    pkgs = import nixpkgs { inherit system; };

    remoteHost = "unicorn";
    remoteModsDir = "mc/gtnh2/mods";

    gradlew = task: pkgs.writeShellApplication {
      name = task;
      runtimeInputs = [ pkgs.git ];
      text = ''
        cd "$(git rev-parse --show-toplevel)"
        exec ./gradlew ${task} "$@"
      '';
    };

    upload = pkgs.writeShellApplication {
      name = "upload";
      runtimeInputs = [ pkgs.openssh pkgs.git pkgs.findutils ];
      text = ''
        repoRoot=$(git rev-parse --show-toplevel)
        cd "$repoRoot"

        rm -rf build/libs
        ./gradlew build

        jar=$(find build/libs -maxdepth 1 -name 'sundial-*.jar' \
          ! -name '*-dev.jar' ! -name '*-sources.jar' | head -n1)

        if [ -z "$jar" ]; then
          echo "No deployable jar found in build/libs after building" >&2
          exit 1
        fi

        echo "Uploading $jar to ${remoteHost}:${remoteModsDir}"
        ssh ${remoteHost} 'rm -f ${remoteModsDir}/sundial-*.jar'
        scp "$jar" ${remoteHost}:${remoteModsDir}/
        echo "Done."
      '';
    };

    release = pkgs.writeShellApplication {
      name = "release";
      runtimeInputs = [ pkgs.git pkgs.gh ];
      text = ''
        if [ $# -ne 1 ]; then
          echo "Usage: release <version>  (e.g. release 0.1.0)" >&2
          exit 1
        fi
        version="$1"

        repoRoot=$(git rev-parse --show-toplevel)
        cd "$repoRoot"

        if [ -n "$(git status --porcelain)" ]; then
          echo "Working tree is dirty, refusing to tag. Commit or stash first." >&2
          exit 1
        fi

        if git rev-parse "$version" >/dev/null 2>&1; then
          echo "Tag $version already exists." >&2
          exit 1
        fi

        git tag -a "$version" -m "$version"
        git push origin "$version"

        repo=$(gh repo view --json nameWithOwner -q .nameWithOwner)
        echo "Pushed tag $version. GTNH-Actions-Workflows will build the jar and publish the GitHub release:"
        echo "  https://github.com/$repo/actions"
      '';
    };

    tasks = {
      build = gradlew "build";
      runServer = gradlew "runServer";
      runClient = gradlew "runClient";
      test = gradlew "test";
      inherit upload release;
    };
  in
  {
    packages.${system} = tasks;
    apps.${system} = builtins.mapAttrs (name: pkg: {
      type = "app";
      program = "${pkg}/bin/${name}";
    }) tasks;
  };
}
