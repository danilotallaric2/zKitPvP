package it.danilotallaric.zkitpvp.events;

import it.danilotallaric.zkitpvp.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KillStreakEvent extends Event {
    private Player player;
    private int streak;
    private static final HandlerList handlerList = new HandlerList();

    public KillStreakEvent(Player player, int streak) {
        this.player = player;
        this.streak = streak;

        Bukkit.broadcastMessage(ChatUtils.getFormattedText("stats.broadcast-streak")
                .replaceAll("%player%", player.getName())
                .replaceAll("%streak%", String.valueOf(streak)));
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public Player getPlayer() {
        return player;
    }

    public int getStreak() {
        return streak;
    }
}
