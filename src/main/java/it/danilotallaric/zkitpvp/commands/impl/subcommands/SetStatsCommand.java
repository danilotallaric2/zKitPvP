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

public class SetStatsCommand extends Subcommand {

    public SetStatsCommand() {
        super("kitpvp", "setstats", "kitpvp.commands.setstats", false);
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (args.size() != 4) {
            sender.sendMessage(ChatUtils.getFormattedText("admin.malformed-command"));
            return;
        }

        String command = args.get(1);
        if ((!command.equals("kills") && !command.equals("deaths") && !command.equals("streak"))) {
            sender.sendMessage(ChatUtils.getFormattedText("admin.malformed-command"));
            return;
        }

        String username = args.get(2);
        if (Bukkit.getPlayer(username) == null) {
            sender.sendMessage(ChatUtils.getFormattedText("admin.player-not-found"));
            return;
        }

        String value = args.get(3);
        if (!NumberUtils.isNumber(value)) {
            sender.sendMessage(ChatUtils.getFormattedText("admin.malformed-command"));
            return;
        }

        Player player = Bukkit.getPlayer(username);
        PlayerData data = KitPvP.getDataManager().getPlayerData(player.getUniqueId());

        switch (command) {
            case "kills":
                data.kills = Integer.parseInt(value);
                break;
            case "deaths":
                data.deaths = Integer.parseInt(value);
                break;
            case "streak":
                data.streak = Integer.parseInt(value);
                break;
        }

        sender.sendMessage(ChatUtils.getFormattedText("admin.stats-edited")
                .replaceAll("%player%", player.getName()));

        KitPvP.getDataManager().updateData(data);
    }
}
