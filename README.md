# Sundial

Check a dimension's day/night cycle without traveling there.

Galacticraft dimensions each run their own day length (the Moon's day is 8x an Overworld day,
for example), so there's normally no way to know what time it is without actually flying there first. This mod lets you know if monsters will be spawning at any given time.

## Commands

### `/cal [dimension] [+format]`

A quick day/night check.

```
/cal moon
☀ Moon (DIM-28) Day 1 Time: 06:22 AM (Day)
```

Add a `+format` string (à la `date(1)`) to render specific fields instead:

```
/cal moon +Day {day}, {hour12}:{minute} {ampm} ({phase})
Day 1, 06:22 AM (Day)
```

### `/sundial <help|info|format>`

- `/sundial help` - lists the available commands
- `/sundial info [dimension]` - a full breakdown of a dimension's clock: which mod owns it,
  whether sleeping is currently possible, day length, and time until the next sunrise/sunset
- `/sundial format` - every `+format` field `/cal` understands, with both its short code (`%d`)
  and long name (`{day}`)

Both commands tab-complete dimension names and, inside a `+format` string, field names.

## Format specifiers

Every field is available two ways: a `date(1)`-style short code (`%H`), or a self-describing long
name (`{hour24}`). Run `/sundial format` in-game for the full, current list.

| Short | Long | Meaning |
|---|---|---|
| `%D` | `{codename}` | Dimension codename, e.g. `DIM-28` |
| `%N` | `{name}` | Display name, e.g. `Moon` |
| `%d` | `{day}` | Days since this dimension's clock started |
| `%H` / `%I` | `{hour24}` / `{hour12}` | Hour, 24- or 12-hour |
| `%M` / `%S` | `{minute}` / `{second}` | Minute / second |
| `%p` / `%P` | `{ampm}` / `{ampm_lower}` | `AM`/`PM`, upper or lower case |
| `%R` / `%T` | `{time24}` / `{time}` | `HH:MM` / `HH:MM:SS` |
| `%F` | `{fulldate}` | `Day N HH:MM` in one token |
| `%K` | `{phase}` | `Day`, `Dusk`, `Night`, or `Dawn` |
| `%e` | `{eta}` | Time until the next phase transition |
| `%r` | `{ratio}` | How many Overworld days this dimension's day takes |
| `%c` | `{cyclelength}` | Length of one full day+night cycle |
| `%y` / `%n` | `{daylength}` / `{nightlength}` | Time until/while sleep is possible |
| `%u` / `%x` | `{sunrise}` / `{sunset}` | Time until the next sunrise/sunset |

`%%` escapes a literal `%`. An unrecognized specifier is a hard error, not silently ignored.

## Requirements, building

Intended to be used with GTNH.

Built against and requires the [GTNH fork of Galacticraft](https://github.com/GTNewHorizons/Galacticraft)
for per-dimension day lengths; vanilla dimensions and other mods' dimensions (resolved generically
via `WorldProvider`, no hardcoded id tables) work too, but Galacticraft itself must be present.

```
./gradlew build       # compile and package the mod jar (build/libs)
./gradlew runServer   # launch a dev dedicated server with the mod loaded
./gradlew runClient   # launch a dev client with the mod loaded
./gradlew test        # run the unit tests
```

`flake.nix` wraps the same commands, plus a couple of maintainer-only stuff:

```
nix run .#build       # ./gradlew build
nix run .#runServer   # ./gradlew runServer
nix run .#runClient   # ./gradlew runClient
nix run .#test        # ./gradlew test
nix run .#upload      # build and scp the jar to a personal dev server (hardcoded host)
nix run .#release -- <version>   # tag and push a release; CI builds it and publishes the GitHub release
```
