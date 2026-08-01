package tech.mazunki.gtnh.sundial.common.dimension;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

// resolves whatever dimension isn't covered by GalacticraftBodies (vanilla, and any other mod's
// dimension) purely from WorldProvider#getDimensionName(), not a hardcoded id -> name table.
// Covers every *registered* dimension, not just loaded ones. ref DimensionManager#createProviderFor()
public final class RegisteredDimensions {

    private RegisteredDimensions() {}

    // accepts a slug derived from the live name ("twilightforest"), a DIM<id> codename in any case,
    // or a bare integer id. Returns null if the argument matches none of these forms.
    @Nullable
    public static Integer resolve(String arg) {
        Integer bySlug = bySlug().get(arg.toLowerCase(Locale.ROOT));
        if (bySlug != null) {
            return bySlug;
        }
        if (arg.length() > 3 && arg.substring(0, 3)
            .equalsIgnoreCase("DIM")) {
            try {
                return Integer.parseInt(arg.substring(3));
            } catch (NumberFormatException ignored) {
                // fall through to the bare-integer check below
            }
        }
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Nullable
    public static String displayNameFor(int dimensionId) {
        WorldServer world = DimensionManager.getWorld(dimensionId);
        if (world != null) {
            return world.provider.getDimensionName();
        }
        if (!DimensionManager.isDimensionRegistered(dimensionId)) {
            return null;
        }
        try {
            WorldProvider provider = DimensionManager.createProviderFor(dimensionId);
            return provider.getDimensionName();
        } catch (RuntimeException e) {
            return null;
        }
    }

    public static String[] allNames() {
        return bySlug().keySet()
            .toArray(new String[0]);
    }

    private static Map<String, Integer> bySlug() {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (int id : DimensionManager.getStaticDimensionIDs()) {
            String name = displayNameFor(id);
            if (name != null) {
                map.put(slugify(name), id);
            }
        }
        return map;
    }

    private static String slugify(String name) {
        return name.toLowerCase(Locale.ROOT)
            .replace(" ", "");
    }
}
