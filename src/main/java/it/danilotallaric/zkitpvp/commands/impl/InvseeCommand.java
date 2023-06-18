package it.danilotallaric.zkitpvp.commands.impl;

import it.danilotallaric.zkitpvp.KitPvP;
import it.danilotallaric.zkitpvp.commands.api.KitPvPCommand;
import it.danilotallaric.zkitpvp.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class InvseeCommand extends KitPvPCommand {
    public InvseeCommand() {
        super(KitPvP.getInstance(), "zinvsee", "kitpvp.commands.zinvsee", true);
    }

    public boolean execute(CommandSender sender, List<String> args) {
        Player player = (Player)sender;
        if (!player.hasPermission("zkitpvp.commands.zinvsee")) {
            player.sendMessage(ChatUtils.getFormattedText("messages.unknowcommand"));
            return false;
        }
        if (args.size() < 1) {
            player.sendMessage(ChatUtils.getFormattedText("invsee.usage"));
            return false;
        }

        Player targetPlayer = Bukkit.getPlayer(args.get(0));
        if (targetPlayer == null) {
            player.sendMessage(ChatUtils.getFormattedText("invsee.player-not-found"));
            return false;
        }

        Player viewer = (Player) sender;
        Inventory targetInventory = targetPlayer.getInventory();

        viewer.openInventory(targetInventory);
        viewer.sendMessage(ChatUtils.getFormattedText("invsee.sent"));

        return true;
    }
}
