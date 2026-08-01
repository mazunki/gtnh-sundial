package tech.mazunki.gtnh.sundial.common.dimension;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PhaseTest {

    @Test
    void fromFractionPicksTheLastBreakpointAtOrBeforeTheFraction() {
        assertEquals(Phase.DAY, Phase.fromFraction(0.0));
        assertEquals(Phase.DAY, Phase.fromFraction(0.49));
        assertEquals(Phase.DUSK, Phase.fromFraction(0.5));
        assertEquals(Phase.DUSK, Phase.fromFraction(0.574));
        assertEquals(Phase.NIGHT, Phase.fromFraction(0.575));
        assertEquals(Phase.NIGHT, Phase.fromFraction(0.924));
        assertEquals(Phase.DAWN, Phase.fromFraction(0.925));
        assertEquals(Phase.DAWN, Phase.fromFraction(0.999));
    }

    @Test
    void ticksUntilNextMeasuresToTheFollowingBreakpoint() {
        assertEquals(6000L, Phase.DAY.ticksUntilNext(0.25, 24000L));
        assertEquals(1200L, Phase.DAWN.ticksUntilNext(0.95, 24000L));
    }

    @Test
    void ticksUntilFractionWrapsAroundWhenTheTargetAlreadyPassed() {
        assertEquals(6000.0, Phase.ticksUntilFraction(0.5, 0.25, 24000L));
        assertEquals(18000.0, Phase.ticksUntilFraction(0.25, 0.5, 24000L), 1e-9);
    }

    @Test
    void timeOfDayZeroFractionIsSixAm() {
        Phase.TimeOfDay time = Phase.timeOfDay(0.0);
        assertEquals(6, time.hour24);
        assertEquals(6, time.hour12);
        assertEquals(0, time.minute);
        assertEquals(0, time.second);
        assertEquals("AM", time.ampmUpper);
    }

    @Test
    void timeOfDayHalfFractionWrapsPastMidnight() {
        Phase.TimeOfDay time = Phase.timeOfDay(0.75);
        assertEquals(0, time.hour24);
        assertEquals(12, time.hour12);
        assertEquals("AM", time.ampmUpper);
    }

    @Test
    void timeOfDayEveningFractionIsPm() {
        Phase.TimeOfDay time = Phase.timeOfDay(0.5);
        assertEquals(18, time.hour24);
        assertEquals(6, time.hour12);
        assertEquals("PM", time.ampmUpper);
    }

    @Test
    void cycleAndDayAndNightLengthSumForVanillaDayLength() {
        long dayLength = 24000L;
        assertEquals(1200L, Phase.cycleLengthSeconds(dayLength));
        assertEquals(627L, Phase.dayLengthSeconds(dayLength));
        assertEquals(573L, Phase.nightLengthSeconds(dayLength));
        assertEquals(
            Phase.cycleLengthSeconds(dayLength),
            Phase.dayLengthSeconds(dayLength) + Phase.nightLengthSeconds(dayLength));
    }

    @Test
    void cycleAndDayAndNightLengthSumForMoonDayLength() {
        long dayLength = 192000L; // the Moon, 8x an Overworld day
        assertEquals(9600L, Phase.cycleLengthSeconds(dayLength));
        assertEquals(
            Phase.cycleLengthSeconds(dayLength),
            Phase.dayLengthSeconds(dayLength) + Phase.nightLengthSeconds(dayLength));
    }

    @Test
    void sunriseSecondsCountsDownToTheStartOfTheDay() {
        assertEquals(600L, Phase.sunriseSeconds(0.5, 24000L));
        // already exactly at sunrise: "next" sunrise wraps to a full cycle away, not zero
        assertEquals(1200L, Phase.sunriseSeconds(0.0, 24000L));
    }

    @Test
    void sunsetSecondsCountsDownToSleepBecomingPossible() {
        // already exactly at the sleep-eligible boundary: wraps to a full cycle away, not zero
        assertEquals(1200L, Phase.sunsetSeconds(Phase.SLEEP_ELIGIBLE_START, 24000L));
        assertEquals(627L, Phase.sunsetSeconds(0.0, 24000L));
    }
}
