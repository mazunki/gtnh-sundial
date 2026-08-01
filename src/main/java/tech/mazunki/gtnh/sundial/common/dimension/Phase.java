package tech.mazunki.gtnh.sundial.common.dimension;

// Breakpoints as fractions of a full day (matches vanilla/Galacticraft's calculateCelestialAngle
// proportions), so the same split applies regardless of dayLength.
public enum Phase {

    DAY(0.0, "day"),
    DUSK(0.5, "dusk"),
    NIGHT(0.575, "night"),
    DAWN(0.925, "dawn");

    public final double start;
    public final String label;

    Phase(double start, String label) {
        this.start = start;
        this.label = label;
    }

    public static Phase fromFraction(double dayFraction) {
        Phase result = DAY;
        for (Phase phase : values()) {
            if (dayFraction >= phase.start) {
                result = phase;
            }
        }
        return result;
    }

    public long ticksUntilNext(double dayFraction, long dayLength) {
        Phase[] phases = values();
        int index = ordinal();
        double nextStart = (index + 1 < phases.length) ? phases[index + 1].start : 1.0;
        return Math.round((nextStart - dayFraction) * dayLength);
    }

    public static double ticksUntilFraction(double targetFraction, double dayFraction, long dayLength) {
        double delta = targetFraction - dayFraction;
        if (delta <= 0) {
            delta += 1.0;
        }
        return delta * dayLength;
    }

    // vanilla: tick 0 of a day is 06:00 (sunrise), scaled proportionally so it
    // lines up for the Moon's 192000-tick day or Mars's 24660-tick
    public static TimeOfDay timeOfDay(double dayFraction) {
        double hourFraction = (dayFraction * 24.0 + 6.0) % 24.0;
        int hour24 = (int) hourFraction;
        double minuteFraction = (hourFraction - hour24) * 60.0;
        int minute = (int) minuteFraction;
        int second = (int) Math.round((minuteFraction - minute) * 60.0);
        if (second == 60) {
            second = 0;
            minute++;
        }
        if (minute == 60) {
            minute = 0;
            hour24 = (hour24 + 1) % 24;
        }
        int hour12 = hour24 % 12;
        if (hour12 == 0) {
            hour12 = 12;
        }
        String ampmUpper = (hour24 < 12) ? "AM" : "PM";
        return new TimeOfDay(hour24, hour12, minute, second, ampmUpper);
    }

    // vanilla's EntityPlayer#sleepInBedAt blocks while World#isDaytime() (skylightSubtracted < 4) is
    // true. this matches tick 12541/24000.
    // the same is true for Galacticraft planets since their celestial-angle formula is the same shape.
    // distinct from dusk/night/dawn (visible darkness/mob risk, not sleep)
    // a completed sleep jumps straight to the next day boundary, so night-for-sleep just runs from
    // here to the wrap.
    public static final double SLEEP_ELIGIBLE_START = 12541.0 / 24000.0;

    // real-world seconds for a full day+night cycle at this dayLength.
    public static long cycleLengthSeconds(long dayLength) {
        return Math.round(dayLength / 20.0);
    }

    // sums exactly to cycleLengthSeconds with nightLengthSeconds below.
    public static long dayLengthSeconds(long dayLength) {
        return Math.round(SLEEP_ELIGIBLE_START * dayLength / 20.0);
    }

    // real-world seconds sleep remains possible for, until the next day boundary.
    public static long nightLengthSeconds(long dayLength) {
        return Math.round((1.0 - SLEEP_ELIGIBLE_START) * dayLength / 20.0);
    }

    public static long sunriseSeconds(double dayFraction, long dayLength) {
        return Math.round(ticksUntilFraction(DAY.start, dayFraction, dayLength) / 20.0);
    }

    // real-world seconds until sleep becomes possible, not until DUSK's visual boundary; see
    // SLEEP_ELIGIBLE_START.
    public static long sunsetSeconds(double dayFraction, long dayLength) {
        return Math.round(ticksUntilFraction(SLEEP_ELIGIBLE_START, dayFraction, dayLength) / 20.0);
    }

    public static final class TimeOfDay {

        public final int hour24;
        public final int hour12;
        public final int minute;
        public final int second;
        public final String ampmUpper;

        private TimeOfDay(int hour24, int hour12, int minute, int second, String ampmUpper) {
            this.hour24 = hour24;
            this.hour12 = hour12;
            this.minute = minute;
            this.second = second;
            this.ampmUpper = ampmUpper;
        }
    }
}
