package tech.mazunki.gtnh.sundial.common.dimension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import tech.mazunki.gtnh.sundial.common.command.ClockFormatter;

// exercises DimensionReadingResolver#computeReading end to end (Phase's day/night math feeding
// DimensionReading feeding ClockFormatter's rendering), the same pipeline resolve() drives, without
// needing a live World/WorldProvider.
class DimensionReadingResolverIntegrationTest {

    @Test
    void vanillaOverworldAtSunriseReadsAsDayOne() {
        DimensionReading r = DimensionReadingResolver
            .computeReading(0, "DIM0", "Overworld", null, null, true, 24000L, true, 0L);

        assertEquals(1L, r.dayNumber);
        assertEquals(Phase.DAY, r.phase);
        assertEquals("06:00", ClockFormatter.render("%R", r));
        assertEquals(1.0, r.dayLengthRatio);
        assertEquals("1x", ClockFormatter.render("%r", r));
    }

    @Test
    void moonDayLengthProducesAnEightXRatioAndScaledClock() {
        // 96000 ticks into a 192000-tick day is exactly halfway: 18:00, dusk
        DimensionReading r = DimensionReadingResolver
            .computeReading(28, "DIM28", "Moon", "Galacticraft", "Moon", true, 192000L, true, 96000L);

        assertEquals(Phase.DUSK, r.phase);
        assertEquals("18:00", ClockFormatter.render("%R", r));
        assertEquals("8x", ClockFormatter.render("%r", r));
        assertEquals("Moon (DIM28)", r.label());
    }

    @Test
    void marsDayNumberAdvancesAcrossMultipleCycles() {
        long marsDayLength = 24660L;
        long localTime = marsDayLength * 5 + 1000; // deep into day 6
        DimensionReading r = DimensionReadingResolver
            .computeReading(29, "DIM29", "Mars", "Galacticraft", "Mars", true, marsDayLength, true, localTime);

        assertEquals(6L, r.dayNumber);
    }

    @Test
    void zeroDayLengthDimensionHasNoPhaseAndFormatsAsNone() {
        // the Asteroid Belt: GC reports dayLength <= 0, guarded before any division happens
        DimensionReading r = DimensionReadingResolver
            .computeReading(30, "DIM30", "Asteroids", "Galacticraft", "Asteroids", false, 0L, true, 12345L);

        assertNull(r.phase);
        assertEquals("none", ClockFormatter.render("%K", r));
        assertFalse(r.canSleepHere);
    }

    @Test
    void aConstantCelestialAngleIsTreatedAsHavingNoRealCycle() {
        // Twilight Forest: reports a normal-looking dayLength but calculateCelestialAngle never moves
        DimensionReading r = DimensionReadingResolver
            .computeReading(31, "DIM31", "The Twilight Forest", null, null, true, 24000L, false, 5000L);

        assertNull(r.phase);
        assertTrue(r.canSleepHere);
    }
}
