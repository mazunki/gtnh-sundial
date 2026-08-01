package tech.mazunki.gtnh.sundial.common.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;

import tech.mazunki.gtnh.sundial.common.dimension.DimensionReading;
import tech.mazunki.gtnh.sundial.common.dimension.DimensionReadingResolver;
import tech.mazunki.gtnh.sundial.common.dimension.GalacticraftBodies;
import tech.mazunki.gtnh.sundial.common.dimension.Phase;
import tech.mazunki.gtnh.sundial.common.dimension.RegisteredDimensions;

// reports a dimension's current day/night phase without requiring the player to travel there.
public class CommandCal extends CommandBase {

    @Override
    public String getCommandName() {
        return "cal";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/cal [dimension] [+format]\n" + "  e.g. /cal moon\n"
            + "  e.g. /cal moon +{day} {hour12}:{minute} {ampm} ({phase})";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        int formatStart = formatArgStart(args);
        String[] dimArgs = Arrays.copyOfRange(args, 0, formatStart);
        String format = (formatStart < args.length)
            ? String.join(" ", Arrays.copyOfRange(args, formatStart, args.length))
                .substring(1)
            : null;

        if (dimArgs.length > 1) {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        DimensionReading reading = DimensionReadingResolver.resolve(sender, dimArgs);

        if (reading.phase == null) {
            sender.addChatMessage(new ChatComponentText(reading.label() + " has no day/night cycle."));
            return;
        }

        TerminalColors colors = TerminalColors.forSender(sender);
        String output;
        if (format != null) {
            try {
                output = ClockFormatter.render(format, reading);
            } catch (IllegalArgumentException e) {
                // escape here too: the message contains the player's own bad specifier (e.g. "%F").
                throw new CommandException(
                    e.getMessage()
                        .replace("%", "%%"));
            }
        } else {
            output = defaultLine(reading, colors);
        }
        sender.addChatMessage(new ChatComponentText(output));
    }

    // first '+'-prefixed token, a la `date +FORMAT`; everything before it is the dimension argument.
    private static int formatArgStart(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("+")) {
                return i;
            }
        }
        return args.length;
    }

    private String defaultLine(DimensionReading r, TerminalColors c) {
        String icon = (r.phase == Phase.NIGHT) ? "☾" : "☀";
        return icon + " "
            + c.label(r.label() + " Day ")
            + c.yellow(String.valueOf(r.dayNumber))
            + c.label(" Time: ")
            + c.yellow(String.format(Locale.ROOT, "%02d:%02d %s", r.hour12, r.minute, r.ampmUpper))
            + c.label(" (")
            + c.gray(ClockFormatter.capitalize(r.phase.label))
            + c.label(")");
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            return null;
        }

        boolean inFormat = Arrays.stream(args)
            .anyMatch(arg -> arg.startsWith("+"));
        if (inFormat) {
            return completeFormatToken(args[args.length - 1]);
        }

        if (args.length == 1) {
            // galacticraft registers some names (Overworld, Ross128a/b) through GalaxyRegistry too,
            // so dedupe against RegisteredDimensions's same names
            Set<String> names = new LinkedHashSet<>(Arrays.asList(GalacticraftBodies.allNames()));
            names.addAll(Arrays.asList(RegisteredDimensions.allNames()));
            return getListOfStringsMatchingLastWord(args, names.toArray(new String[0]));
        }
        return null;
    }

    // 1.7.10 tab completion replaces the whole last token, so completions include everything typed
    // so far plus the chosen field.
    private List<String> completeFormatToken(String lastArg) {
        int braceIndex = lastArg.lastIndexOf('{');
        if (braceIndex != -1 && braceIndex >= lastArg.lastIndexOf('}')) {
            String prefix = lastArg.substring(braceIndex + 1);
            String beforeBrace = lastArg.substring(0, braceIndex + 1);
            List<String> completions = new ArrayList<>();
            for (FormatField field : FormatField.values()) {
                if (field.longName.startsWith(prefix)) {
                    completions.add(beforeBrace + field.longName + "}");
                }
            }
            return completions;
        }

        if (lastArg.endsWith("%")) {
            List<String> completions = new ArrayList<>();
            for (FormatField field : FormatField.values()) {
                completions.add(lastArg + field.shortCode);
            }
            completions.add(lastArg + "%");
            return completions;
        }
        return null;
    }
}
