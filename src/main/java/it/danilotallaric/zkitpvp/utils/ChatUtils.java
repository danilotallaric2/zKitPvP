package it.danilotallaric.zkitpvp.utils;

import it.danilotallaric.zkitpvp.KitPvP;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ChatUtils {
    public static String getColoredText(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String getFormattedText(String path) {
        return getColoredText(KitPvP.getFileManager().getMessages().getString(path));
    }

    public static String formatMessage(Player player, String line) {
        return ChatUtils.getColoredText(PlaceholderAPI.setPlaceholders(player, line.replaceAll("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                .replaceAll("%ping%", String.valueOf(((CraftPlayer) player).getHandle().ping))));
    }

    public static String formatCondition(Player player, String line) {
        if (!line.contains("%condition#")) return line;

        String name = line.split("%condition#")[1].split("%")[0];
        String condition = PlaceholderAPI.setPlaceholders(player, KitPvP.getFileManager().getConfig().getString("conditions." + name + ".check"));

        if (condition.contains("==")) {
            String[] values = condition.split("==");

            return KitPvP.getFileManager().getConfig().getString("conditions." + name + "." + values[0].equals(values[1]));
        }

        if (condition.contains("!=")) {
            String[] values = condition.split("!=");

            return KitPvP.getFileManager().getConfig().getString("conditions." + name + "." + !values[0].equals(values[1]));
        }

        return line;
    }
}
