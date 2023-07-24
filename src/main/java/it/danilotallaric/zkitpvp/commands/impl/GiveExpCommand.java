package it.danilotallaric.zkitpvp.commands.impl;

import it.danilotallaric.zkitpvp.KitPvP;
import it.danilotallaric.zkitpvp.commands.api.KitPvPCommand;
import it.danilotallaric.zkitpvp.utils.ChatUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class GiveExpCommand extends KitPvPCommand {

    public GiveExpCommand() {
        super(KitPvP.getInstance(), "giveexp", "zkitpvp.commands.giveexp", true);

        setNoPermissionMessage(ChatUtils.getFormattedText("giveexp.no-permission"));
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        Player player = (Player) sender;
        if (args.size() != 2) {
            player.sendMessage(ChatUtils.getFormattedText("giveexp.usage"));
            return false;
        }

        String name = args.get(0);
        if (Bukkit.getPlayer(name) == null) {
            player.sendMessage(ChatUtils.getFormattedText("giveexp.player-not-found"));
            return false;
        }

        Player target = Bukkit.getPlayer(name);
        if (!NumberUtils.isNumber(args.get(1))) {
            player.sendMessage(ChatUtils.getFormattedText("giveexp.usage"));
            return false;
        }

        int levels = player.getLevel();
        int amount = Integer.parseInt(args.get(1));

        if (levels < amount) {
            player.sendMessage(ChatUtils.getFormattedText("giveexp.not-enough-levels"));
            return false;
        }

        player.setLevel(levels - amount);
        target.setLevel(target.getLevel() + amount);

        player.sendMessage(ChatUtils.getFormattedText("giveexp.sent")
                .replaceAll("%levels%", String.valueOf(amount))
                .replaceAll("%name%", target.getName()));
        return false;
    }

}
