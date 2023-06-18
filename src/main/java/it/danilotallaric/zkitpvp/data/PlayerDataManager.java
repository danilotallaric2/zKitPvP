package it.danilotallaric.zkitpvp.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerDataManager {

    final List<PlayerData> playerDataList = new ArrayList<>();

    public List<PlayerData> getAllData() {
        return playerDataList;
    }

    public PlayerData getPlayerData(UUID uuid) {
        synchronized (playerDataList) {
            PlayerData playerData = playerDataList.stream().filter(x -> x.getUUID().equals(uuid)).findFirst().orElse(null);

            if (playerData == null) {
                playerDataList.add(new PlayerData(uuid));
                return getPlayerData(uuid);
            }

            return playerData;
        }
    }

    public void updateData(PlayerData data) {
        if (data == null) return;
        synchronized (playerDataList) {
            playerDataList.remove(getPlayerData(data.getUUID()));
            playerDataList.add(data);
        }
    }
}
