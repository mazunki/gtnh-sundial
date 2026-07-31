package tech.mazunki.gtnh.sundial.common.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

// reports a dimension's current day/night phase without requiring the player to travel there.
public class CommandCal extends CommandBase {

    @Override
    public String getCommandName() {
        return "cal";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/cal [dimension] [+format]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        sender.addChatMessage(new ChatComponentText("TODO"));
    }
}
