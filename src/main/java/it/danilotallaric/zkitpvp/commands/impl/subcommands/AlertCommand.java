package it.danilotallaric.zkitpvp.commands.impl.subcommands;

import it.danilotallaric.zkitpvp.commands.api.Subcommand;
import it.danilotallaric.zkitpvp.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;

public class AlertCommand extends Subcommand {

    public AlertCommand() {
        super("kitpvp", "alert", "kitpvp.commands.alert", false);
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (args.size() == 1) {
            sender.sendMessage(ChatUtils.getFormattedText("malformed-command"));
            return;
        }

        String message = String.join(" ", args.subList(1, args.size()));
        Bukkit.broadcastMessage(ChatUtils.getColoredText(message));
    }
}
