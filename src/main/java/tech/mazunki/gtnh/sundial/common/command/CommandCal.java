package tech.mazunki.gtnh.sundial.common.command;

import java.util.Arrays;
import java.util.Locale;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;

import tech.mazunki.gtnh.sundial.common.dimension.DimensionReading;
import tech.mazunki.gtnh.sundial.common.dimension.DimensionReadingResolver;
import tech.mazunki.gtnh.sundial.common.dimension.Phase;

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
}
