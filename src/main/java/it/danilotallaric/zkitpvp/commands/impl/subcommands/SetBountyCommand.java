package it.danilotallaric.zkitpvp.commands.impl.subcommands;

import it.danilotallaric.zkitpvp.KitPvP;
import it.danilotallaric.zkitpvp.commands.api.Subcommand;
import it.danilotallaric.zkitpvp.data.PlayerData;
import it.danilotallaric.zkitpvp.utils.ChatUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SetBountyCommand extends Subcommand {

    public SetBountyCommand() {
        super("kitpvp", "setbounty", "zkitpvp.commands.setbounty", false);
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (args.size() != 3) {
            sender.sendMessage(ChatUtils.getFormattedText("admin.malformed-command"));
            return;
        }

        String username = args.get(1);
        if (Bukkit.getPlayer(username) == null) {
            sender.sendMessage(ChatUtils.getFormattedText("admin.player-not-found"));
            return;
        }

        String value = args.get(2);
        if (!NumberUtils.isNumber(value)) {
            sender.sendMessage(ChatUtils.getFormattedText("admin.malformed-command"));
            return;
        }

        Player target = Bukkit.getPlayer(username);
        PlayerData data = KitPvP.getDataManager().getPlayerData(target.getUniqueId());

        long bounty = Long.parseLong(value);
        data.setBounty(bounty);

        KitPvP.getDataManager().updateData(data);
        sender.sendMessage(ChatUtils.getFormattedText("admin.bounty-edited")
                .replaceAll("%bounty%", String.valueOf(bounty)).replaceAll("%player%", target.getName()));
    }
}
