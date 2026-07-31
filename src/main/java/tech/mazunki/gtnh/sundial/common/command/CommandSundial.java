package tech.mazunki.gtnh.sundial.common.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.util.ChatComponentText;

// help/info/format subcommands: usage help, a diagnostic dump for a dimension, and the +FORMAT
// field reference
public class CommandSundial extends CommandBase {

    @Override
    public String getCommandName() {
        return "sundial";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/sundial <help|info|format> ...";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        TerminalColors c = TerminalColors.forSender(sender);
        List<String> lines = new ArrayList<>();
        heading(lines, c, "Sundial commands:");
        detail(lines, c, "TODO", "not implemented yet");
        flush(sender, lines);
    }

    // RConConsoleSource#addChatMessage concatenates every call with no separator at all, so a
    // multi-line reply needs to go over as one call joined by '\n' instead.
    private static void flush(ICommandSender sender, List<String> lines) {
        if (sender instanceof RConConsoleSource) {
            sender.addChatMessage(new ChatComponentText(String.join("\n", lines)));
            return;
        }
        for (String line : lines) {
            sender.addChatMessage(new ChatComponentText(line));
        }
    }

    private static void send(List<String> lines, String text) {
        lines.add(text);
    }

    private static void heading(List<String> lines, TerminalColors c, String text) {
        send(lines, c.yellow(text));
    }

    private static void detail(List<String> lines, TerminalColors c, String label, Object value) {
        send(lines, "  " + c.label(label + ": ") + c.gray(String.valueOf(value)));
    }
}
