package tech.mazunki.gtnh.sundial.common.dimension;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public final class ModOwnership {

    private static final List<String> BOOTSTRAP_MOD_IDS = Arrays.asList("forge", "fml", "mcp");

    private ModOwnership() {}

    public static String forClass(Class<?> clazz) {
        File source = sourceOf(clazz);
        if (source == null) {
            return null;
        }
        for (ModContainer mod : Loader.instance()
            .getModList()) {
            if (source.equals(mod.getSource())) {
                if (BOOTSTRAP_MOD_IDS.contains(
                    mod.getModId()
                        .toLowerCase(Locale.ROOT))) {
                    return null;
                }
                return mod.getName();
            }
        }
        return null;
    }

    private static File sourceOf(Class<?> clazz) {
        CodeSource codeSource = clazz.getProtectionDomain()
            .getCodeSource();
        if (codeSource == null) {
            return null;
        }
        URL location = codeSource.getLocation();
        if (location == null) {
            return null;
        }
        try {
            // a class loaded from inside a jar reports its location as jar:file:/path.jar!/entry,
            // which java.io.File(URI) rejects; unwrap to the plain jar file instead.
            if ("jar".equals(location.getProtocol())) {
                URLConnection connection = location.openConnection();
                if (connection instanceof JarURLConnection) {
                    location = ((JarURLConnection) connection).getJarFileURL();
                }
            }
            return new File(location.toURI());
        } catch (URISyntaxException | IOException | IllegalArgumentException e) {
            return null;
        }
    }
}
