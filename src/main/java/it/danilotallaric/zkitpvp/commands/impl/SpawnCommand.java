package it.danilotallaric.zkitpvp.commands.impl;

import it.danilotallaric.zkitpvp.KitPvP;
import it.danilotallaric.zkitpvp.commands.api.KitPvPCommand;
import it.danilotallaric.zkitpvp.data.PlayerData;
import it.danilotallaric.zkitpvp.listeners.PlayerListener;
import it.danilotallaric.zkitpvp.utils.ChatUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SpawnCommand extends KitPvPCommand {
    public SpawnCommand() {
        super(KitPvP.getInstance(), "spawn", "zkitpvp.commands.spawn", true);

        setNoPermissionMessage(ChatUtils.getFormattedText("spawn.no-permission"));
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        Player player = (Player) sender;
        Location spawn = (Location) KitPvP.getFileManager().getConfig().get("spawn-location");

        if (spawn == null) {
            player.sendMessage(ChatUtils.getFormattedText("spawn.no-spawn-found"));
            return false;
        }

        PlayerData data = KitPvP.getDataManager().getPlayerData(player.getUniqueId());
        data.atSpawn = true;

        KitPvP.getDataManager().updateData(data);
        player.teleport(spawn);
        if (!PlayerListener.falldamage.contains(player)) {
            PlayerListener.falldamage.add(player);
        }
        return false;
    }
}
