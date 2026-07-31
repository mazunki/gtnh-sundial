package tech.mazunki.gtnh.sundial.common.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

// minecraft's §-codes only render in the in-game chat GUI; console/RCON get real ANSI escapes
// instead. the ANSI strings contain embedded ESC bytes (0x1B) before each "[", invisible in a
// normal read. confirm with `cat -A` when editing
public enum TerminalColors {

    MINECRAFT(EnumChatFormatting.BOLD.toString() + EnumChatFormatting.DARK_AQUA.toString(),
        EnumChatFormatting.BOLD.toString() + EnumChatFormatting.YELLOW.toString(), EnumChatFormatting.GRAY.toString(),
        EnumChatFormatting.BOLD.toString() + EnumChatFormatting.AQUA.toString(), EnumChatFormatting.RESET.toString()),
    ANSI("[1;36m", "[1;93m", "[37m", "[1;96m", "[0m");

    private final String label;
    private final String yellow;
    private final String gray;
    private final String cyan;
    private final String reset;

    TerminalColors(String label, String yellow, String gray, String cyan, String reset) {
        this.label = label;
        this.yellow = yellow;
        this.gray = gray;
        this.cyan = cyan;
        this.reset = reset;
    }

    private String fmt(String code, String text) {
        return code + text + reset;
    }

    public String label(String text) {
        return fmt(label, text);
    }

    public String yellow(String text) {
        return fmt(yellow, text);
    }

    public String gray(String text) {
        return fmt(gray, text);
    }

    public String cyan(String text) {
        return fmt(cyan, text);
    }

    public static TerminalColors forSender(ICommandSender sender) {
        return (sender instanceof EntityPlayer) ? MINECRAFT : ANSI;
    }
}
