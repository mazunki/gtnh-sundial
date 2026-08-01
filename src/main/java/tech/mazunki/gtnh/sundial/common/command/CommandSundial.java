package tech.mazunki.gtnh.sundial.common.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.util.ChatComponentText;

import tech.mazunki.gtnh.sundial.common.dimension.DimensionReading;
import tech.mazunki.gtnh.sundial.common.dimension.DimensionReadingResolver;
import tech.mazunki.gtnh.sundial.common.dimension.Phase;

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
        String subcommand = (args.length > 0) ? args[0].toLowerCase(Locale.ROOT) : "help";
        String[] rest = (args.length > 0) ? Arrays.copyOfRange(args, 1, args.length) : new String[0];
        TerminalColors c = TerminalColors.forSender(sender);
        List<String> lines = new ArrayList<>();

        switch (subcommand) {
            case "info":
                info(sender, lines, rest, c);
                break;
            case "format":
                format(lines, c);
                break;
            case "help":
            default:
                help(lines, c);
                break;
        }

        flush(sender, lines);
    }

    // RConConsoleSource#addChatMessage concatenates every call with no separator at all, so a
    // multi-line reply needs to go over as one call joined by '\n' there instead.
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

    private static void commandHelp(List<String> lines, TerminalColors c, String command, String description) {
        send(lines, "  " + c.label(command) + c.gray(" - " + description));
    }

    private void help(List<String> lines, TerminalColors c) {
        heading(lines, c, "Sundial commands:");
        commandHelp(lines, c, "/cal [dimension] [+format]", "quick day/night check for a dimension");
        commandHelp(lines, c, "/sundial info [dimension]", "detailed info about a dimension's clock");
        commandHelp(lines, c, "/sundial format", "list every +format field /cal understands");
    }

    private void info(ICommandSender sender, List<String> lines, String[] dimArgs, TerminalColors c) {
        if (dimArgs.length > 1) {
            throw new WrongUsageException("/sundial info [dimension]");
        }

        DimensionReading r = DimensionReadingResolver.resolve(sender, dimArgs);

        heading(lines, c, r.label());
        // ownerModName is null for vanilla Minecraft
        detail(lines, c, "Mod", (r.ownerModName != null) ? r.ownerModName : "Minecraft (vanilla)");
        detail(lines, c, "Can sleep", sleepStatus(r));

        if (r.phase == null) {
            send(lines, "  " + c.gray("No day/night cycle."));
            return;
        }

        detail(
            lines,
            c,
            "Day length",
            r.dayLength + " ticks ("
                + ClockFormatter.formatDuration(r.cycleLengthSeconds)
                + ", "
                + ClockFormatter.formatRatio(r.dayLengthRatio)
                + "x Overworld)");
        detail(
            lines,
            c,
            "Day/Night split",
            "day " + ClockFormatter.formatDuration(r.dayLengthSeconds)
                + ", night "
                + ClockFormatter.formatDuration(r.nightLengthSeconds));
        detail(
            lines,
            c,
            "Now",
            ClockFormatter.capitalize(r.phase.label) + ", day "
                + r.dayNumber
                + ", "
                + String.format(Locale.ROOT, "%02d:%02d %s", r.hour12, r.minute, r.ampmUpper));
        detail(
            lines,
            c,
            "Next",
            "sunrise in " + ClockFormatter.formatDuration(r.sunriseSeconds)
                + ", sunset in "
                + ClockFormatter.formatDuration(r.sunsetSeconds));
    }

    private static String sleepStatus(DimensionReading r) {
        if (!r.canSleepHere) {
            return "No";
        }
        if (r.phase == null) {
            return "Yes (no day/night cycle)";
        }
        if (r.dayFraction >= Phase.SLEEP_ELIGIBLE_START) {
            return "Yes";
        }
        return "Not yet, but in " + ClockFormatter.formatDuration(r.sunsetSeconds);
    }

    private void format(List<String> lines, TerminalColors c) {
        heading(lines, c, "Format fields for /cal +FORMAT:");
        for (FormatField field : FormatField.values()) {
            send(
                lines,
                "  " + c.cyan("%" + field.shortCode)
                    + c.gray(" / ")
                    + c.cyan("{" + field.longName + "}")
                    + c.gray(": " + field.description));
        }
    }
}
