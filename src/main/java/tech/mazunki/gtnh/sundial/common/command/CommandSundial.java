package tech.mazunki.gtnh.sundial.common.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
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
        sender.addChatMessage(new ChatComponentText("TODO"));
    }
}
