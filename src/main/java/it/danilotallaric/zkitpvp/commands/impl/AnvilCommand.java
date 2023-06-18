package it.danilotallaric.zkitpvp.commands.impl;

import it.danilotallaric.zkitpvp.KitPvP;
import it.danilotallaric.zkitpvp.blocks.BlockListener;
import it.danilotallaric.zkitpvp.blocks.BlockTask;
import it.danilotallaric.zkitpvp.commands.api.KitPvPCommand;
import it.danilotallaric.zkitpvp.data.PlayerDataManager;
import it.danilotallaric.zkitpvp.inventory.InventoryListener;
import it.danilotallaric.zkitpvp.listeners.CustomListener;
import it.danilotallaric.zkitpvp.listeners.PlayerListener;
import it.danilotallaric.zkitpvp.placeholders.MainPlaceholder;
import it.danilotallaric.zkitpvp.tablist.TabUpdater;
import it.danilotallaric.zkitpvp.tasks.GeneralTask;
import it.danilotallaric.zkitpvp.tasks.SaveTask;
import it.danilotallaric.zkitpvp.utils.ChatUtils;

import java.util.Arrays;
import java.util.List;

import it.danilotallaric.zkitpvp.utils.FileManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class AnvilCommand
        extends KitPvPCommand {
    public AnvilCommand() {
        super(KitPvP.getInstance(), "anvil", "kitpvp.commands.discord", false);
    }

    public boolean execute(CommandSender sender, List<String> args) {
        Player player = (Player)sender;
        player.sendMessage(ChatUtils.getFormattedText("discord.message"));
        return false;
    }


}
