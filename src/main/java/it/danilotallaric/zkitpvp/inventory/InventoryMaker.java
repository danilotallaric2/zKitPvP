package it.danilotallaric.zkitpvp.inventory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class InventoryMaker implements Supplier<Inventory> {

    private final Inventory inventory;

    public static HashMap<Integer, Consumer<InventoryClickEvent>> actions = new HashMap<>();

    public InventoryMaker(int size, String title) {
        this.inventory = Bukkit.createInventory(null, size, ChatColor.translateAlternateColorCodes('&', title));
    }

    public InventoryMaker addAction(int slotId, Consumer<InventoryClickEvent> consumer) {
        actions.put(slotId, consumer);
        return this;
    }

    public InventoryMaker addItemStack(int slotId, ItemStack itemStack) {
        this.inventory.setItem(slotId, itemStack);
        return this;
    }

    public String getTitle() {
        return this.inventory.getTitle();
    }

    public int getSize() {
        return this.inventory.getSize();
    }

    public List<ItemStack> getContents() {
        return Arrays.asList(this.inventory.getContents());
    }

    @Override
    public Inventory get() {
        InventoryListener.actions.put(this.inventory, actions);
        return this.inventory;
    }

}