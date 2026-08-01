package tech.mazunki.gtnh.sundial.common.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import tech.mazunki.gtnh.sundial.common.dimension.DimensionReading;
import tech.mazunki.gtnh.sundial.common.dimension.Phase;

class ClockFormatterTest {

    // Day 3, 06:22:07 AM on the Moon (dayLength 192000), phase DAY
    private static DimensionReading moonReading() {
        return new DimensionReading(
            28,
            "DIM28",
            "Moon",
            "Galacticraft",
            "Moon",
            true,
            192000L,
            0.001,
            Phase.DAY,
            3L,
            6,
            6,
            22,
            7,
            "AM",
            95820L,
            8.0,
            9600L,
            5015L,
            4585L,
            0L,
            5015L);
    }

    @Test
    void shortCodesRenderTheSameFieldsAsLongNames() {
        DimensionReading r = moonReading();
        assertEquals(ClockFormatter.render("%D", r), ClockFormatter.render("{codename}", r));
        assertEquals(ClockFormatter.render("%N", r), ClockFormatter.render("{name}", r));
        assertEquals(ClockFormatter.render("%d", r), ClockFormatter.render("{day}", r));
        assertEquals(ClockFormatter.render("%H", r), ClockFormatter.render("{hour24}", r));
        assertEquals(ClockFormatter.render("%I", r), ClockFormatter.render("{hour12}", r));
        assertEquals(ClockFormatter.render("%M", r), ClockFormatter.render("{minute}", r));
        assertEquals(ClockFormatter.render("%S", r), ClockFormatter.render("{second}", r));
        assertEquals(ClockFormatter.render("%p", r), ClockFormatter.render("{ampm}", r));
        assertEquals(ClockFormatter.render("%P", r), ClockFormatter.render("{ampm_lower}", r));
        assertEquals(ClockFormatter.render("%R", r), ClockFormatter.render("{time24}", r));
        assertEquals(ClockFormatter.render("%T", r), ClockFormatter.render("{time}", r));
        assertEquals(ClockFormatter.render("%F", r), ClockFormatter.render("{fulldate}", r));
        assertEquals(ClockFormatter.render("%K", r), ClockFormatter.render("{phase}", r));
        assertEquals(ClockFormatter.render("%e", r), ClockFormatter.render("{eta}", r));
        assertEquals(ClockFormatter.render("%r", r), ClockFormatter.render("{ratio}", r));
        assertEquals(ClockFormatter.render("%c", r), ClockFormatter.render("{cyclelength}", r));
        assertEquals(ClockFormatter.render("%y", r), ClockFormatter.render("{daylength}", r));
        assertEquals(ClockFormatter.render("%n", r), ClockFormatter.render("{nightlength}", r));
        assertEquals(ClockFormatter.render("%u", r), ClockFormatter.render("{sunrise}", r));
        assertEquals(ClockFormatter.render("%x", r), ClockFormatter.render("{sunset}", r));
    }

    @Test
    void rendersKnownFieldValues() {
        DimensionReading r = moonReading();
        assertEquals("DIM28", ClockFormatter.render("%D", r));
        assertEquals("Moon", ClockFormatter.render("%N", r));
        assertEquals("3", ClockFormatter.render("%d", r));
        assertEquals("06:22", ClockFormatter.render("%R", r));
        assertEquals("06:22:07", ClockFormatter.render("%T", r));
        assertEquals("AM", ClockFormatter.render("%p", r));
        assertEquals("am", ClockFormatter.render("%P", r));
        assertEquals("Day", ClockFormatter.render("%K", r));
        assertEquals("8x", ClockFormatter.render("%r", r));
    }

    @Test
    void mixedFormatStringRendersEveryPieceInOrder() {
        DimensionReading r = moonReading();
        assertEquals(
            "Day 3, 06:22 AM (Day)",
            ClockFormatter.render("Day {day}, {hour12}:{minute} {ampm} ({phase})", r));
    }

    @Test
    void doublePercentEscapesALiteralPercent() {
        assertEquals("100%", ClockFormatter.render("100%%", moonReading()));
    }

    @Test
    void unclosedBracePassesThroughLiterally() {
        assertEquals("{oops", ClockFormatter.render("{oops", moonReading()));
    }

    @Test
    void unknownShortCodeThrows() {
        assertThrows(IllegalArgumentException.class, () -> ClockFormatter.render("%q", moonReading()));
    }

    @Test
    void unknownLongNameThrows() {
        assertThrows(IllegalArgumentException.class, () -> ClockFormatter.render("{nonsense}", moonReading()));
    }

    @Test
    void formatDurationOmitsZeroComponentsButKeepsAtLeastOne() {
        assertEquals("0s", ClockFormatter.formatDuration(0));
        assertEquals("45s", ClockFormatter.formatDuration(45));
        assertEquals("56min", ClockFormatter.formatDuration(3360));
        assertEquals("2h 40min", ClockFormatter.formatDuration(9600));
        assertEquals("1h 00min 05s", ClockFormatter.formatDuration(3605));
    }

    @Test
    void formatRatioDropsTheDecimalOnWholeNumbers() {
        assertEquals("8", ClockFormatter.formatRatio(8.0));
        assertEquals("1.03", ClockFormatter.formatRatio(1.0275));
    }

    @Test
    void capitalizeUppercasesOnlyTheFirstLetter() {
        assertEquals("Day", ClockFormatter.capitalize("day"));
    }
}
