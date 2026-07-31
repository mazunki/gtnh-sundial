package tech.mazunki.gtnh.sundial.common.dimension;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

public final class DimensionReadingResolver {

    private DimensionReadingResolver() {}

    public static int resolveDimensionId(ICommandSender sender, String[] dimArgs) {
        if (dimArgs.length == 0) { // overworld for server console
            return sender.getEntityWorld().provider.dimensionId;
        }

        Integer live = GalacticraftBodies.resolve(dimArgs[0]);
        if (live != null) {
            return live;
        }

        Integer resolved = RegisteredDimensions.resolve(dimArgs[0]);
        if (resolved == null) {
            throw new WrongUsageException(
                "Unknown dimension '" + escape(dimArgs[0])
                    + "'. Use a valid name, DIM<id>, or <id>. Tab-completion may be supported for names.");
        }
        return resolved;
    }

    private static String escape(String s) {
        return s.replace("%", "%%");
    }
}
