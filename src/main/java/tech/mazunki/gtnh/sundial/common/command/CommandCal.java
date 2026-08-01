package tech.mazunki.gtnh.sundial.common.command;

import java.util.Locale;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
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
        return "/cal [dimension]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        DimensionReading reading = DimensionReadingResolver.resolve(sender, args);

        if (reading.phase == null) {
            sender.addChatMessage(new ChatComponentText(reading.label() + " has no day/night cycle."));
            return;
        }

        sender.addChatMessage(new ChatComponentText(defaultLine(reading, TerminalColors.forSender(sender))));
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
