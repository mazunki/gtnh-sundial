package tech.mazunki.gtnh.sundial.common.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TerminalColorsTest {

    @Test
    void ansiCodesWrapTextInEscBracketSequences() {
        assertEquals("[1;36mtext[0m", TerminalColors.ANSI.label("text"));
        assertEquals("[1;93mtext[0m", TerminalColors.ANSI.yellow("text"));
        assertEquals("[37mtext[0m", TerminalColors.ANSI.gray("text"));
        assertEquals("[1;96mtext[0m", TerminalColors.ANSI.cyan("text"));
    }

    @Test
    void minecraftCodesAreDistinctFromAnsiAndCarryNoEscByte() {
        String label = TerminalColors.MINECRAFT.label("text");
        assertTrue(label.contains("text"));
        assertTrue(label.indexOf('') == -1);
    }

    @Test
    void forSenderFallsBackToAnsiForNonPlayerSenders() {
        assertEquals(TerminalColors.ANSI, TerminalColors.forSender(null));
    }
}
