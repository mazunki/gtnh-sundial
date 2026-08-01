package tech.mazunki.gtnh.sundial.common.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class FormatFieldTest {

    @Test
    void everyFieldRoundTripsThroughItsShortCodeAndLongName() {
        for (FormatField field : FormatField.values()) {
            assertEquals(field, FormatField.byShortCode(field.shortCode));
            assertEquals(field, FormatField.byLongName(field.longName));
        }
    }

    @Test
    void shortCodesAreUnique() {
        FormatField[] fields = FormatField.values();
        for (int i = 0; i < fields.length; i++) {
            for (int j = i + 1; j < fields.length; j++) {
                assertNotEqualsShortCode(fields[i], fields[j]);
            }
        }
    }

    private static void assertNotEqualsShortCode(FormatField a, FormatField b) {
        if (a.shortCode.equals(b.shortCode)) {
            throw new AssertionError(a + " and " + b + " share the short code '" + a.shortCode + "'");
        }
    }

    @Test
    void unknownShortCodeReturnsNull() {
        assertNull(FormatField.byShortCode("q"));
    }

    @Test
    void unknownLongNameReturnsNull() {
        assertNull(FormatField.byLongName("nonsense"));
    }
}
