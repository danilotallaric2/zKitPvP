package it.danilotallaric.zkitpvp.commands.impl.subcommands;

import it.danilotallaric.zkitpvp.KitPvP;
import it.danilotallaric.zkitpvp.commands.api.Subcommand;
import it.danilotallaric.zkitpvp.utils.ChatUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;

public class SetSpawnCommand extends Subcommand {

    public SetSpawnCommand() {
        super("kitpvp", "setspawn", "kitpvp.commands.setspawn", true);
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        Player player = (Player) sender;

        Location location = player.getLocation();
        FileConfiguration config = KitPvP.getFileManager().getConfig();

        File file = new File(KitPvP.getInstance().getDataFolder(), "config.yml");

        config.set("spawn-location", location);
        KitPvP.getFileManager().saveFile(config, file);

        sender.sendMessage(ChatUtils.getFormattedText("admin.new-spawn-set"));
    }

}
