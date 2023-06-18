package it.danilotallaric.zkitpvp.tasks;

import it.danilotallaric.zkitpvp.KitPvP;
import it.danilotallaric.zkitpvp.data.PlayerData;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SaveTask implements Runnable {

    @Override
    public void run() {
        List<PlayerData> allData = new ArrayList<>(KitPvP.getDataManager().getAllData());
        allData.forEach(data -> {
            UUID uuid = data.getUUID();

            YamlConfiguration configuration = new YamlConfiguration();
            File file = new File(KitPvP.getInstance().getDataFolder().getAbsolutePath() + "/data", uuid.toString());

            configuration.set("balance", data.getBalance());

            configuration.set("kills", data.kills);
            configuration.set("deaths", data.deaths);
            configuration.set("streak", data.streak);
            configuration.set("bounty", data.getBounty());

            configuration.set("pickup-arrows", data.pickupArrows);
            configuration.set("pickup-apples", data.pickupGoldenApple);

            KitPvP.getFileManager().saveFile(configuration, file);
        });
    }

}
