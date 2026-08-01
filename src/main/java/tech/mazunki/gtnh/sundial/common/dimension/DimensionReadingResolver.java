package tech.mazunki.gtnh.sundial.common.dimension;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace;

public final class DimensionReadingResolver {

    private static final long VANILLA_DAY_LENGTH = 24000L;

    private DimensionReadingResolver() {}

    public static DimensionReading resolve(ICommandSender sender, String[] dimArgs) throws CommandException {
        int dimensionId = resolveDimensionId(sender, dimArgs);
        String codename = "DIM" + dimensionId;

        // Galacticraft's own bodies have localized display names, everything else only has a name after they're loaded
        String galacticraftName = GalacticraftBodies.displayNameFor(dimensionId);
        String displayName = (galacticraftName != null) ? galacticraftName
            : RegisteredDimensions.displayNameFor(dimensionId);
        String label = (displayName != null) ? displayName + " (" + codename + ")" : codename;
        String safeDisplayName = (displayName != null) ? displayName : codename;

        WorldServer target = DimensionManager.getWorld(dimensionId);
        if (target == null) {
            // getWorld() only finds already-loaded dimensions; initDimension() force-loads it
            if (!DimensionManager.isDimensionRegistered(dimensionId)) {
                throw new CommandException(escape(label) + " is not a registered dimension.");
            }
            DimensionManager.initDimension(dimensionId);
            target = DimensionManager.getWorld(dimensionId);
            if (target == null) {
                throw new CommandException(escape(label) + " could not be loaded.");
            }
        }

        WorldProvider provider = target.provider;
        String ownerModName = ModOwnership.forClass(provider.getClass());
        long dayLength = (provider instanceof WorldProviderSpace) ? ((WorldProviderSpace) provider).getDayLength()
            : VANILLA_DAY_LENGTH;

        boolean canSleepHere = provider.isSurfaceWorld(); // maybe there's a more correct way of doing this

        // some dimensions report a normal dayLength but never actually cycle (e.g. Twilight Forest's
        // calculateCelestialAngle ignores its arguments and returns a constant).
        // varying the partial-tick argument distinguishes this without false-positiving on GC, whose
        // override ignores the raw tick argument but still uses partial-ticks
        boolean hasRealCycle = provider.calculateCelestialAngle(0L, 0.0F)
            != provider.calculateCelestialAngle(0L, 0.99F);
        long localTime = provider.getWorldTime();

        return computeReading(
            dimensionId,
            codename,
            safeDisplayName,
            ownerModName,
            galacticraftName,
            canSleepHere,
            dayLength,
            hasRealCycle,
            localTime);
    }

    // pure day/night math, split out from resolve() so it's testable without a live World
    static DimensionReading computeReading(int dimensionId, String codename, String displayName,
        String ownerModName, String galacticraftName, boolean canSleepHere, long dayLength, boolean hasRealCycle,
        long localTime) {
        if (dayLength <= 0 || !hasRealCycle) {
            return new DimensionReading(
                dimensionId,
                codename,
                displayName,
                ownerModName,
                galacticraftName,
                canSleepHere,
                dayLength);
        }

        long ticksIntoDay = ((localTime % dayLength) + dayLength) % dayLength;
        double dayFraction = ticksIntoDay / (double) dayLength;
        long dayNumber = localTime / dayLength + 1;

        Phase phase = Phase.fromFraction(dayFraction);
        long ticksUntilNext = phase.ticksUntilNext(dayFraction, dayLength);
        long secondsUntilNext = Math.round(ticksUntilNext / 20.0);
        Phase.TimeOfDay time = Phase.timeOfDay(dayFraction);

        double dayLengthRatio = dayLength / (double) VANILLA_DAY_LENGTH;

        return new DimensionReading(
            dimensionId,
            codename,
            displayName,
            ownerModName,
            galacticraftName,
            canSleepHere,
            dayLength,
            dayFraction,
            phase,
            dayNumber,
            time.hour24,
            time.hour12,
            time.minute,
            time.second,
            time.ampmUpper,
            secondsUntilNext,
            dayLengthRatio,
            Phase.cycleLengthSeconds(dayLength),
            Phase.dayLengthSeconds(dayLength),
            Phase.nightLengthSeconds(dayLength),
            Phase.sunriseSeconds(dayFraction, dayLength),
            Phase.sunsetSeconds(dayFraction, dayLength));
    }

    private static int resolveDimensionId(ICommandSender sender, String[] dimArgs) {
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
