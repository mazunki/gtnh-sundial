package tech.mazunki.gtnh.sundial.common.dimension;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;

// dimension IDs are config-driven per server (ConfigManagerCore.idDimensionMoon etc), so we
// resolve at runtime via CelestialBody#getDimensionID() rather than a hardcoded table
public final class GalacticraftBodies {

    private GalacticraftBodies() {}

    public static Integer resolve(String name) {
        CelestialBody body = allLive().get(name.toLowerCase(Locale.ROOT));
        return (body != null) ? body.getDimensionID() : null;
    }

    // human-formatted display name (e.g "Alpha Centauri Bb")
    public static String displayNameFor(int dimensionId) {
        for (CelestialBody body : allLive().values()) {
            if (body.getDimensionID() == dimensionId) {
                return body.getLocalizedName();
            }
        }
        return null;
    }

    public static String[] allNames() {
        return allLive().keySet()
            .toArray(new String[0]);
    }

    private static Map<String, CelestialBody> allLive() {
        Map<String, CelestialBody> merged = new LinkedHashMap<>();
        merged.putAll(GalaxyRegistry.getRegisteredMoons());
        merged.putAll(GalaxyRegistry.getRegisteredPlanets());

        // unset bodies default to dimension id 0, colliding with overworld
        merged.values()
            .removeIf(body -> !body.getReachable());

        return merged;
    }
}
