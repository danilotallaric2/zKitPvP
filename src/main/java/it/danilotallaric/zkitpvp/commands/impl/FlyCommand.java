
package it.danilotallaric.zkitpvp.commands.impl;

import it.danilotallaric.zkitpvp.KitPvP;
import it.danilotallaric.zkitpvp.commands.api.KitPvPCommand;
import it.danilotallaric.zkitpvp.utils.ChatUtils;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand
        extends KitPvPCommand {
    public FlyCommand() {
        super(KitPvP.getInstance(), "fly", "zkitpvp.commands.fly", true);
    }

    public boolean execute(CommandSender sender, List<String> args) {

        Player player = (Player) sender;

        if (!player.hasPermission("zkitpvp.commands.fly")) {
            player.sendMessage(ChatUtils.getFormattedText("messages.unknowcommand"));
            return false;
        }

        if (player.getAllowFlight()) {
            player.setAllowFlight(false);
            player.setFlying(false);
            player.sendMessage(ChatUtils.getFormattedText("fly.disabled"));
        } else {
            player.setAllowFlight(true);
            player.sendMessage(ChatUtils.getFormattedText("fly.enabled"));
        }
        return false;
    }

}
