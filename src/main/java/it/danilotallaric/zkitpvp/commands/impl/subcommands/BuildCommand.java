package it.danilotallaric.zkitpvp.commands.impl.subcommands;

import it.danilotallaric.zkitpvp.KitPvP;
import it.danilotallaric.zkitpvp.commands.api.Subcommand;
import it.danilotallaric.zkitpvp.data.PlayerData;
import it.danilotallaric.zkitpvp.utils.ChatUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class BuildCommand extends Subcommand {

    public BuildCommand() {
        super("kitpvp", "build", "zkitpvp.commands.build", true);
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        Player player = (Player) sender;

        PlayerData data = KitPvP.getDataManager().getPlayerData(player.getUniqueId());
        data.isBuilder = !data.isBuilder;

        sender.sendMessage(ChatUtils.getFormattedText("anti-build.build-mode-" + (data.isBuilder ? "on" : "off")));

        KitPvP.getDataManager().updateData(data);
    }
}
