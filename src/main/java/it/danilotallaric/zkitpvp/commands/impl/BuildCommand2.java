package it.danilotallaric.zkitpvp.commands.impl;

import it.danilotallaric.zkitpvp.KitPvP;
import it.danilotallaric.zkitpvp.commands.api.KitPvPCommand;
import it.danilotallaric.zkitpvp.data.PlayerData;
import it.danilotallaric.zkitpvp.utils.ChatUtils;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuildCommand2
        extends KitPvPCommand {
    public BuildCommand2() {
        super(KitPvP.getInstance(), "build", "zkitpvp.commands.build", true);
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        Player player = (Player) sender;
        if (!player.hasPermission("zkitpvp.commands.build")) {
            player.sendMessage(ChatUtils.getFormattedText("messages.unknowcommand"));
            return false;
        }
        PlayerData data = KitPvP.getDataManager().getPlayerData(player.getUniqueId());
        data.isBuilder = !data.isBuilder;

        sender.sendMessage(ChatUtils.getFormattedText("anti-build.build-mode-" + (data.isBuilder ? "on" : "off")));

        KitPvP.getDataManager().updateData(data);
        return false;
    }
}
