package it.danilotallaric.zkitpvp.commands.impl;

import it.danilotallaric.zkitpvp.KitPvP;
import it.danilotallaric.zkitpvp.commands.api.CommandHandler;
import it.danilotallaric.zkitpvp.commands.api.KitPvPCommand;
import it.danilotallaric.zkitpvp.commands.impl.subcommands.*;
import it.danilotallaric.zkitpvp.utils.ChatUtils;
import it.danilotallaric.zkitpvp.commands.impl.subcommands.*;
import org.bukkit.command.CommandSender;

import java.util.List;

public class MainCommand extends KitPvPCommand {

    public MainCommand() {
        super(KitPvP.getInstance(), "kitpvp", "kitpvp.commands.admin", false);

        setNoSubCommandFoundMessage(ChatUtils.getFormattedText("admin.no-sub-command-found"));
        setNoPermissionMessage(ChatUtils.getColoredText("&7Running &bzKitPvP &7version &a2.0.0 &7by &cdanilotallaric#9017"));

        CommandHandler.addSubCommand(this, new SetStatsCommand());
        CommandHandler.addSubCommand(this, new SetSpawnCommand());
        CommandHandler.addSubCommand(this, new SetBountyCommand());
        CommandHandler.addSubCommand(this, new BuildCommand());
        CommandHandler.addSubCommand(this, new AlertCommand());
        CommandHandler.addSubCommand(this, new ReloadCommand());
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        if (args.size() != 0) return false;

        KitPvP.getFileManager().getMessages().getStringList("admin.help-command")
                .forEach(msg -> sender.sendMessage(ChatUtils.getColoredText(msg)));
        return false;
    }

}
