package it.danilotallaric.zkitpvp.data;

import dev.demeng.sentinel.wrapper.SentinelClient;
import dev.demeng.sentinel.wrapper.exception.*;
import it.danilotallaric.zkitpvp.KitPvP;
import it.danilotallaric.zkitpvp.commands.impl.AnvilCommand;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerData {

    private final UUID uuid;

    public void addBounty(long bounty) {
        this.bounty += bounty;
    }

    public long getBounty() {
        return bounty;
    }

    public void setBounty(long bounty) {
        this.bounty = bounty;
    }

    public long endCombatTimestamp, endEnderTimestamp;
    public List<Player> assisters = new ArrayList<>();
    public boolean isBuilder = false, inCombat = false, inEnderCooldown = false, atSpawn = true, pickupArrows = true, pickupGoldenApple = true;
    public int kills = 0, deaths = 0, streak = 0;

    public Player getLastPlayer() {
        return lastPlayer;
    }

    public void setLastPlayer(Player lastPlayer) {
        this.lastPlayer = lastPlayer;
    }

    private Player lastPlayer;
    private long bounty;
    private double balance;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;

        YamlConfiguration configuration = new YamlConfiguration();
        File file = new File(KitPvP.getInstance().getDataFolder().getAbsolutePath() + "/data", uuid.toString());

        if (file.exists()) {
            try {
                configuration.load(file);
                balance = configuration.getDouble("balance", 0);

                kills = configuration.getInt("kills", 0);
                deaths = configuration.getInt("deaths", 0);
                streak = configuration.getInt("streak", 0);
                bounty = configuration.getLong("bounty", 0);

                pickupArrows = configuration.getBoolean("pickup-arrows", true);
                pickupGoldenApple = configuration.getBoolean("pickup-apples", true);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double amount) {
        balance = amount;
    }

    public void deposit(double amount) {
        balance += amount;
    }

    public void withdraw(double amount) {
        if (balance - amount < 0) balance = 0;
        else balance -= amount;
    }

    public UUID getUUID() {
        return uuid;
    }
}
