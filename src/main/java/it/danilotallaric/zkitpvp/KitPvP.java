package it.danilotallaric.zkitpvp;

import it.danilotallaric.zkitpvp.blocks.BlockListener;
import it.danilotallaric.zkitpvp.blocks.BlockTask;
import it.danilotallaric.zkitpvp.commands.api.KitPvPCommand;
import it.danilotallaric.zkitpvp.commands.impl.*;
import it.danilotallaric.zkitpvp.data.PlayerDataManager;
import it.danilotallaric.zkitpvp.inventory.InventoryListener;
import it.danilotallaric.zkitpvp.listeners.CustomListener;
import it.danilotallaric.zkitpvp.listeners.PlayerListener;
import it.danilotallaric.zkitpvp.placeholders.MainPlaceholder;
import it.danilotallaric.zkitpvp.tablist.TabUpdater;
import it.danilotallaric.zkitpvp.tasks.GeneralTask;
import it.danilotallaric.zkitpvp.tasks.SaveTask;
import it.danilotallaric.zkitpvp.utils.FileManager;
import it.danilotallaric.zkitpvp.data.PlayerData;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class KitPvP extends JavaPlugin implements Listener {
    private static final Pattern MONEY_PATTERN = Pattern.compile("((([1-9]\\d{0,2}(,\\d{3})*)|(([1-9]\\d*)?\\d))(\\.?\\d?\\d?)?$)");
    private static List<String> baseLore;
    private static ItemStack base;

    public static KitPvP instance;
    private FileConfiguration config;

    @Override
    public void onEnable() {
        instance =  this;
        int pluginId = 18511;
        Metrics metrics = new Metrics(this, pluginId);
        assegni();
        metrics.addCustomChart(new Metrics.SimplePie("chart_id", () -> "My value"));
        KitPvP.getInstance().getServer().getPluginManager().registerEvents(KitPvP.getInstance(), KitPvP.getInstance());


        KitPvP.fileManager = new FileManager(KitPvP.instance);

        Arrays.asList(new MainCommand(), new AssegnoCommand(), new InvseeCommand(), new PotionCommand(),new SpawnCommand(), new BuildCommand2(), new DropSettingsCommand(), new StoreCommand(), new FixCommand(), new DiscordCommand(), new GiveExpCommand())
                .forEach(KitPvPCommand::registerExecutor);

        Arrays.asList(new BlockListener(), new CustomListener(), new PlayerListener(), new InventoryListener())
                .forEach(event -> Bukkit.getPluginManager().registerEvents(event, KitPvP.getInstance()));

        KitPvP.dataManager = new PlayerDataManager();
        KitPvP.blockManager = new BlockTask();

        if (KitPvP.getInstance().getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new MainPlaceholder().register();
        }

        if (KitPvP.fileManager.getConfig().getBoolean("tab-list.enabled")) {
            Bukkit.getScheduler().runTaskTimer(KitPvP.getInstance(), new TabUpdater(), 10L, 10L);
        }
        KitPvP.startAlwaysDayTask();
        KitPvP.saveManager = new SaveTask();

        Bukkit.getScheduler().runTaskTimerAsynchronously(KitPvP.getInstance(), KitPvP.saveManager, 600L, 600L);
        Bukkit.getScheduler().runTaskTimerAsynchronously(KitPvP.getInstance(), new GeneralTask(), 2L, 2L);

        if (KitPvP.getInstance().getServer().getPluginManager().isPluginEnabled("Vault")) {
            RegisteredServiceProvider<Economy> service = KitPvP.getInstance().getServer().getServicesManager().getRegistration(Economy.class);
            if (service != null) {
                economy = service.getProvider();
            }
        }

    }



    public static double getBanknoteAmount(ItemStack itemstack) {
        if (itemstack.getItemMeta().hasDisplayName() && itemstack.getItemMeta().hasLore()) {
            String display = itemstack.getItemMeta().getDisplayName();
            List<String> lore = itemstack.getItemMeta().getLore();

            if (display.equals(getMessage("assegno.name"))) {
                for (String money : lore) {
                    Matcher matcher = MONEY_PATTERN.matcher(money);

                    if (matcher.find()) {
                        String amount = matcher.group(1);
                        return Double.parseDouble(amount.replaceAll(",", ""));


                    }
                }
            }
        }
        return 0;
    }


    public static String getMessage(String path) {
        if (!KitPvP.getFileManager().getConfig().isString(path)) {
            return path;
        }

        return ChatColor.translateAlternateColorCodes('&', KitPvP.getFileManager().getConfig().getString(path));
    }
    public static boolean isBanknote(ItemStack itemstack) {
        if (itemstack.getType() == base.getType() && itemstack.getDurability() == base.getDurability()
                && itemstack.getItemMeta().hasDisplayName() && itemstack.getItemMeta().hasLore()) {
            String display = itemstack.getItemMeta().getDisplayName();
            List<String> lore = itemstack.getItemMeta().getLore();

            return display.equals(getMessage("assegno.name")) && lore.size() == KitPvP.getFileManager().getConfig().getStringList("assegno.lore").size();
        }
        return false;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        final Block brokenBlock = event.getBlock();

        if (brokenBlock.getType() == Material.ANVIL) {

            new BukkitRunnable() {
                @Override
                public void run() {
                    brokenBlock.setType(Material.ANVIL);
                    brokenBlock.getState().update(true);
                }
            }.runTaskLater(this, 1);
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (event.toWeatherState()) {

            event.setCancelled(true);
        }
    }

    public static void startAlwaysDayTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                World world = Bukkit.getWorlds().get(0);
                world.setTime(1000);
            }
        }.runTaskTimer(KitPvP.getInstance(), 0, 600);
    }

    public void reloadConfiguration() {
        fileManager = new FileManager(instance);

        assegni();
    }

    public void assegni() {
        baseLore = getConfig().getStringList("assegno.lore");
        base = new ItemStack(Material.getMaterial(getConfig().getString("assegno.material", "PAPER")), 1, (short) getConfig().getInt("assegno.data"));
        ItemMeta meta = base.getItemMeta();

        meta.setDisplayName(colorMessage(getConfig().getString("assegno.name", "Assegno")));
        base.setItemMeta(meta);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent playerInteractEvent) {
        Player player = playerInteractEvent.getPlayer();
        if (playerInteractEvent.getAction() != Action.RIGHT_CLICK_BLOCK || playerInteractEvent.getClickedBlock().getType() != Material.HOPPER) return;
        Inventory inventory = Bukkit.createInventory(null, 54, "Cestino");
        player.openInventory(inventory);
        playerInteractEvent.setCancelled(true);
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent blockDamageEvent) {
        if (!blockDamageEvent.getBlock().getType().equals((Object) Material.ANVIL)) return;
        blockDamageEvent.setCancelled(true);
    }

    public static String colorMessage(String message) {
        if (message == null) {
            return message;
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String formatDouble(double value) {
        NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);

        int max = KitPvP.getFileManager().getConfig().getInt("assegno.maximum-float");
        int min = KitPvP.getFileManager().getConfig().getInt("assegno.minimum-float");

        nf.setMaximumFractionDigits(max);
        nf.setMinimumFractionDigits(min);
        return nf.format(value);
    }
    public static ItemStack createBanknote(String creatorName, double amount) {
        if (creatorName.equals("CONSOLE")) {
            creatorName = "CONSOLE";
        }
        List<String> formatLore = new ArrayList<String>();

        for (String baseLore : KitPvP.baseLore) {
            formatLore.add(colorMessage(baseLore.replace("[money]", formatDouble(amount)).replace("[player]", creatorName)));
        }

        ItemStack ret = base.clone();
        ItemMeta meta = ret.getItemMeta();
        meta.setLore(formatLore);
        if (KitPvP.fileManager.getConfig().getBoolean("assegno.glow")) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        ret.setItemMeta(meta);

        return ret;
    }

    @Override
    public void onDisable() {
        if (saveManager == null) return;
        saveManager.run();
    }



    public static KitPvP getInstance() {
        return instance;
    }

    public static PlayerDataManager dataManager;

    public static PlayerDataManager getDataManager() {
        return dataManager;
    }

    public static BlockTask blockManager;

    public static BlockTask getBlockManager() {
        return blockManager;
    }

    public static SaveTask saveManager;

    public static SaveTask getSaveManager() {
        return saveManager;
    }

    public static FileManager fileManager;

    public static FileManager getFileManager() {
        return fileManager;
    }

    public static Economy economy;

    public static Economy getEconomy() {
        return economy;
    }
}
