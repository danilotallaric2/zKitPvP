package it.danilotallaric.zkitpvp.placeholders;

import it.danilotallaric.zkitpvp.KitPvP;
import it.danilotallaric.zkitpvp.data.PlayerData;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

public class MainPlaceholder extends PlaceholderExpansion {
    @Override
    public String getAuthor() {
        return "ImGqbbo";
    }

    @Override
    public String getIdentifier() {
        return "kitpvp";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        long current = System.currentTimeMillis(), endTimestamp, diff;
        PlayerData data = KitPvP.getDataManager().getPlayerData(player.getPlayer().getUniqueId());
        switch (params) {
            case "bounty":
                return String.valueOf(data.getBounty());
            case "bounty_formatted":
                return formatNumber(data.getBounty());
            case "kills":
                return String.valueOf(data.kills);
            case "deaths":
                return String.valueOf(data.deaths);
            case "streak":
                return String.valueOf(data.streak);
            case "combat":
                if (data.endCombatTimestamp == -1) {
                    return "0.0";
                }

                endTimestamp = data.endCombatTimestamp;
                diff = endTimestamp - current;

                if (diff > 0) {
                    return String.valueOf(Math.floor(diff / 100) / 10);
                }
                else {
                    data.endCombatTimestamp = -1;
                    return "0.0";
                }
            case "enderpearl":
                if (data.endEnderTimestamp == -1) {
                    return "0.0";
                }

                endTimestamp = data.endEnderTimestamp;
                diff = endTimestamp - current;

                if (diff > 0) {
                    return String.valueOf(Math.floor(diff / 100) / 10);
                }
                else {
                    data.endEnderTimestamp = -1;
                    return "0.0";
                }
        }

        return null;
    }

    private String formatNumber(long value) {
        String number = String.format("%,d", value);
        String[] commas = new String[]{"K", "M", "B", "T", "Q"};

        String[] split = number.split(",");
        if (split.length == 1) {
            return String.valueOf(value);
        }
        return split[0] + "." + split[1].charAt(0) + commas[split.length - 2];
    }
}
