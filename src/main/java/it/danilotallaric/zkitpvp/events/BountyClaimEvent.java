package it.danilotallaric.zkitpvp.events;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class BountyClaimEvent extends Event {
    private final Player killed, killer;
    private final Location deathLocation;
    private final long bounty;
    private static final HandlerList handlerList = new HandlerList();

    public BountyClaimEvent(Player killed, Player killer, Location deathLocation, long bounty) {
        this.deathLocation = deathLocation;
        this.killed = killed;
        this.killer = killer;
        this.bounty = bounty;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
