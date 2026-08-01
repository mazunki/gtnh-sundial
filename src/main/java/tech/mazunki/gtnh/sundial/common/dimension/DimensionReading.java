package tech.mazunki.gtnh.sundial.common.dimension;

import javax.annotation.Nullable;

// a fully-resolved snapshot of a dimension's identity and day/night clock.
// build me through DimensionReadingResolver.
public final class DimensionReading {

    public final int dimensionId;
    public final String codename;
    @Nullable
    public final String displayName;
    @Nullable
    public final String ownerModName;
    @Nullable
    public final String galacticraftBodyName;
    public final boolean canSleepHere;
    public final long dayLength;
    public final double dayFraction;
    @Nullable
    public final Phase phase;
    public final long dayNumber;
    public final int hour24;
    public final int hour12;
    public final int minute;
    public final int second;
    @Nullable
    public final String ampmUpper;
    public final long secondsUntilNextPhase;
    public final double dayLengthRatio;
    public final long cycleLengthSeconds;
    public final long dayLengthSeconds;
    public final long nightLengthSeconds;
    public final long sunriseSeconds;
    public final long sunsetSeconds;

    public DimensionReading(int dimensionId, String codename, @Nullable String displayName,
        @Nullable String ownerModName, @Nullable String galacticraftBodyName, boolean canSleepHere, long dayLength,
        double dayFraction, @Nullable Phase phase, long dayNumber, int hour24, int hour12, int minute, int second,
        @Nullable String ampmUpper, long secondsUntilNextPhase, double dayLengthRatio, long cycleLengthSeconds,
        long dayLengthSeconds, long nightLengthSeconds, long sunriseSeconds, long sunsetSeconds) {
        this.dimensionId = dimensionId;
        this.codename = codename;
        this.displayName = displayName;
        this.ownerModName = ownerModName;
        this.galacticraftBodyName = galacticraftBodyName;
        this.canSleepHere = canSleepHere;
        this.dayLength = dayLength;
        this.dayFraction = dayFraction;
        this.phase = phase;
        this.dayNumber = dayNumber;
        this.hour24 = hour24;
        this.hour12 = hour12;
        this.minute = minute;
        this.second = second;
        this.ampmUpper = ampmUpper;
        this.secondsUntilNextPhase = secondsUntilNextPhase;
        this.dayLengthRatio = dayLengthRatio;
        this.cycleLengthSeconds = cycleLengthSeconds;
        this.dayLengthSeconds = dayLengthSeconds;
        this.nightLengthSeconds = nightLengthSeconds;
        this.sunriseSeconds = sunriseSeconds;
        this.sunsetSeconds = sunsetSeconds;
    }

    // for a dimension with no day/night cycle (dayLength <= 0): everything past dayLength is
    // meaningless (phase null, the rest zeroed)
    public DimensionReading(int dimensionId, String codename, @Nullable String displayName,
        @Nullable String ownerModName, @Nullable String galacticraftBodyName, boolean canSleepHere, long dayLength) {
        this(
            dimensionId,
            codename,
            displayName,
            ownerModName,
            galacticraftBodyName,
            canSleepHere,
            dayLength,
            0.0,
            null,
            0,
            0,
            0,
            0,
            0,
            null,
            0,
            0.0,
            0,
            0,
            0,
            0,
            0);
    }

    public String label() {
        return (displayName != null) ? displayName + " (" + codename + ")" : codename;
    }
}
