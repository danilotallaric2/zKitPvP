package it.danilotallaric.zkitpvp.listeners;

import it.danilotallaric.zkitpvp.KitPvP;
import it.danilotallaric.zkitpvp.data.PlayerData;
import it.danilotallaric.zkitpvp.events.BountyClaimEvent;
import it.danilotallaric.zkitpvp.events.KillStreakEvent;
import it.danilotallaric.zkitpvp.tasks.GeneralTask;
import it.danilotallaric.zkitpvp.utils.ChatUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.Dye;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

import java.util.*;

public class PlayerListener implements Listener {
    public static List<Player> falldamage = new ArrayList<>();
    @EventHandler(priority=EventPriority.HIGH)
    public void onFallDamage(EntityDamageEvent entityDamageEvent) {
        if (entityDamageEvent.isCancelled()) {
            return;
        }
        if (!(entityDamageEvent.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player)entityDamageEvent.getEntity();
        if (entityDamageEvent.getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }
        if (!falldamage.contains(player)) {
            return;
        }
        falldamage.remove(player);
        entityDamageEvent.setCancelled(true);
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        Player player = event.getPlayer();
        if (KitPvP.getFileManager().getConfig().getBoolean("on-join.give-kit") && player.getInventory().getContents().length == 0) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), KitPvP.getFileManager().getConfig().getString("on-join.kit-command").replaceAll("%player%", player.getName()));
        }

        PlayerData data = KitPvP.getDataManager().getPlayerData(player.getUniqueId());
        data.atSpawn = true;

        KitPvP.getDataManager().updateData(data);
        Location spawn = (Location) KitPvP.getFileManager().getConfig().get("spawn-location");
        if (!falldamage.contains(player)) {
            falldamage.add(player);
        }
        if (spawn == null) return;

        player.teleport(spawn);

    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();
        if (!KitPvP.getFileManager().getConfig().getBoolean("chat.enabled", false)) return;

        String format = KitPvP.getFileManager().getConfig().getString("chat.format", "&7%player-name%: &f%message%");
        String parsed = ChatUtils.getColoredText(PlaceholderAPI.setPlaceholders(sender.getPlayer(), format));

        event.setFormat(parsed.replaceAll("%player-name%", "%s").replaceAll("%message%", "%s"));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        Player player = event.getPlayer();
        PlayerData data = KitPvP.getDataManager().getPlayerData(player.getUniqueId());

        if (data.inCombat) executeDeath(player);
    }

    @EventHandler
    public void onPotionDrank(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (!item.getType().equals(Material.POTION)) return;

        Inventory inventory = event.getPlayer().getInventory();
        Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> inventory.remove(Material.GLASS_BOTTLE), 1L);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Inventory inventory = event.getInventory();
        if (!(inventory instanceof EnchantingInventory)) return;

        inventory.setItem(1, getLapis());
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        Inventory inventory = event.getInventory();
        inventory.setItem(1, getLapis());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if (!(inventory instanceof EnchantingInventory)) return;

        if (event.getSlot() != 1) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        if (!(inventory instanceof EnchantingInventory)) return;

        inventory.setItem(1, null);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player entity = (Player) event.getEntity();
        if (entity.hasMetadata("NPC")) return;
        if (entity.getHealth() - event.getFinalDamage() > 0) return;

        executeDeath(entity);
        event.setDamage(0.0);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerAxe(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) return;

        Player damaged = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();

        ItemStack hand = damager.getItemInHand();

        if (hand == null) return;
        if (!hand.getType().toString().contains("AXE")) return;

        int damage = KitPvP.getFileManager().getConfig().getInt("axe.increment");

        PlayerInventory inventory = damaged.getInventory();
        Arrays.stream(inventory.getArmorContents()).filter(Objects::nonNull)
                .forEach(x -> x.setDurability((short) (x.getDurability() + damage)));
    }


    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        event.setFoodLevel(20);
    }

    @EventHandler
    public void onAnvilBroke(BlockDamageEvent event) {
        Block block = event.getBlock();
        if (!block.getType().equals(Material.ANVIL)) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();

        if (item == null) return;
        if (!event.getAction().equals(Action.RIGHT_CLICK_AIR) && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;

        Player player = event.getPlayer();
        switch (item.getType()) {
            case EXP_BOTTLE:
                event.setCancelled(true);

                Random random = new Random();

                int newExp = 0;
                int count = item.getAmount();

                for (int i = 0; i < count; i++) {
                    newExp += random.nextInt(30 - 5) + 5;
                }

                player.giveExp(newExp);
                player.setItemInHand(null);
                break;
            case ENDER_PEARL:
                PlayerData data = KitPvP.getDataManager().getPlayerData(player.getUniqueId());

                if (data.inEnderCooldown) {
                    event.setCancelled(true);
                    return;
                }

                data.inEnderCooldown = true;

                data.endEnderTimestamp = (long) (System.currentTimeMillis() + GeneralTask.enderTimer * 1000L);

                KitPvP.getDataManager().updateData(data);
                break;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (!(event.getEntity() instanceof Player)) return;

        Player entity = (Player) event.getEntity();
        if (entity.getHealth() - event.getFinalDamage() > 0) {
            updateData(entity);
        }

        if (damager instanceof Projectile) {
            damager = (Entity) ((Projectile) damager).getShooter();
        }

        if (!(damager instanceof Player)) return;

        updateData((Player) damager);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommandExecute(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        Player player = event.getPlayer();

        if (checkForCommand(message, player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommandExecute2(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        Player player = event.getPlayer();

        if (checkForCommand(message, player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDamagePlayer(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (!(event.getEntity() instanceof Player)) return;

        Player entity = (Player) event.getEntity();
        if (damager instanceof Projectile) {
            damager = (Entity) ((Projectile) damager).getShooter();
        }

        if (!(damager instanceof Player)) return;

        if (damager.equals(entity)) return;

        Player attacker = (Player) damager;
        PlayerData data = KitPvP.getDataManager().getPlayerData(entity.getUniqueId());

        if (data.getLastPlayer() != null && data.getLastPlayer() != attacker) {
            data.assisters.remove(data.getLastPlayer());
            data.assisters.add(data.getLastPlayer());
        }

        data.setLastPlayer(attacker);
        KitPvP.getDataManager().updateData(data);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleDeath(PlayerDeathEvent event) {
        Random random = new Random();

        Player killed = event.getEntity();
        PlayerData data = KitPvP.getDataManager().getPlayerData(killed.getUniqueId());

        data.setBounty(0);

        data.streak = 0;
        data.deaths++;

        KitPvP.getFileManager().getConfig().getStringList("on-death.commands").forEach(x -> {
            String message = x.replaceAll("%player%", !killed.hasMetadata("NPC") ? killed.getName() : "%player%");
            if (!message.equals(x)) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), message);
            }
        });

        int killAmount = KitPvP.getFileManager().getConfig().getInt("on-death.money-killer");
        int assistAmount = KitPvP.getFileManager().getConfig().getInt("on-death.money-assist");

        data.assisters.forEach(ass -> {
            if (ass != null && !ass.hasMetadata("NPC")) {
                KitPvP.getEconomy().depositPlayer(ass, assistAmount);
                ass.sendMessage(ChatUtils.getFormattedText("kills.assist").replaceAll("%player%", killed.getName()));
            }
        });

        if (data.getLastPlayer() != null) {
            PlayerData killerData = KitPvP.getDataManager().getPlayerData(data.getLastPlayer().getUniqueId());
            if (!data.getLastPlayer().hasMetadata("NPC")) {
                if (data.getLastPlayer().getExp() < 0.5f) {
                    data.getLastPlayer().setExp(0.5f);
                }
                else {
                    data.getLastPlayer().setExp(0.0f);
                    data.getLastPlayer().setLevel(data.getLastPlayer().getLevel() + 1);
                }

                KitPvP.getEconomy().depositPlayer(data.getLastPlayer(), killAmount);
                data.getLastPlayer().sendMessage(ChatUtils.getFormattedText("kills.kill").replaceAll("%player%", killed.getName()));

                boolean giveEffect = KitPvP.getFileManager().getConfig().getBoolean("on-death.give-effect");
                if (giveEffect) {
                    String effectName = KitPvP.getFileManager().getConfig().getString("on-death.effect-name");

                    int effectStrength = KitPvP.getFileManager().getConfig().getInt("on-death.effect-strength");
                    int effectTime = KitPvP.getFileManager().getConfig().getInt("on-death.effect-time");

                    data.getLastPlayer().addPotionEffect(new PotionEffect(PotionEffectType.getByName(effectName), effectTime * 20, effectStrength));
                }
            }

            List<String> messages = KitPvP.getFileManager().getMessages().getStringList("kills.death");
            String selected = messages.get(random.nextInt(messages.size() - 1))
                    .replaceAll("%killer%", data.getLastPlayer().getName()).replaceAll("%player%", killed.getName());

            killed.sendMessage(ChatUtils.getColoredText(selected));

            if (!data.getLastPlayer().equals(killed)) {
                int streak = KitPvP.getFileManager().getConfig().getInt("streak.threshold");

                killerData.kills++;
                killerData.streak++;

                if (killerData.streak % streak == 0)
                    Bukkit.getPluginManager().callEvent(new KillStreakEvent(data.getLastPlayer(), killerData.streak));
            }

            data.setLastPlayer(null);
            data.assisters.clear();

            KitPvP.getDataManager().updateData(killerData);
        }
        else
            killed.sendMessage(ChatUtils.getFormattedText("kills.default-death").replaceAll("%player%", killed.getName()));

        KitPvP.getDataManager().updateData(data);
    }

    @EventHandler
    public void onKillStreak(KillStreakEvent event) {
        Random random = new Random();

        Player killer = event.getPlayer();
        PlayerInventory inventory = killer.getInventory();

        boolean giveItem = KitPvP.getFileManager().getConfig().getBoolean("streak.give-item");
        if (giveItem) {
            inventory.addItem(new ItemStack(Material.valueOf(KitPvP.getFileManager().getConfig().getString("streak.item-name")), 1));
        }

        PlayerData data = KitPvP.getDataManager().getPlayerData(killer.getUniqueId());

        int chance = KitPvP.getFileManager().getConfig().getInt("bounties.chance");
        int chosen = random.nextInt(100);

        if (chosen > chance) return;

        int minimum = KitPvP.getFileManager().getConfig().getInt("bounties.minimum");
        int maximum = KitPvP.getFileManager().getConfig().getInt("bounties.maximum");

        int multiplier = KitPvP.getFileManager().getConfig().getInt("bounties.multiplier");
        int bounty = random.nextInt(maximum - minimum) + minimum;

        int finalbounty = bounty * multiplier;

        data.addBounty(finalbounty);

        Bukkit.broadcastMessage(ChatUtils.getFormattedText("bounties.bounty-set")
                .replaceAll("%bounty%", String.valueOf(data.getBounty())).replaceAll("%player%", killer.getName()));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBountyClaim(PlayerDeathEvent event) {
        Player killed = event.getEntity();
        PlayerData data = KitPvP.getDataManager().getPlayerData(killed.getUniqueId());

        Player killer = data.getLastPlayer();
        if (killer == null || data.getBounty() == 0) return;

        Location location = killed.getLocation();
        if (killer.equals(killed)) return;

        Bukkit.broadcastMessage(ChatUtils.getFormattedText("bounties.bounty-claimed")
                .replaceAll("%bounty%", String.valueOf(data.getBounty()))
                .replaceAll("%player%", killed.getName())
                .replaceAll("%killer%", killer.getName()));

        if (data.getBounty() >= 2000) location.getWorld().strikeLightningEffect(location);
        if (data.getBounty() >= 5000)
            Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.WITHER_SPAWN, 4, 1));

        KitPvP.getEconomy().depositPlayer(killer, data.getBounty());
        Bukkit.getPluginManager().callEvent(new BountyClaimEvent(killed, killer, killed.getLocation(), data.getBounty()));
    }

    @EventHandler
    public void onPlayerPickup(PlayerPickupItemEvent event) {
        ItemStack item = event.getItem().getItemStack();
        PlayerData data = KitPvP.getDataManager().getPlayerData(event.getPlayer().getUniqueId());

        if (item.getType().equals(Material.GOLDEN_APPLE) && !item.isSimilar(new ItemStack(Material.GOLDEN_APPLE, item.getAmount(), (short) 1)) && !data.pickupGoldenApple)
            event.setCancelled(true);
        if (item.getType().equals(Material.ARROW) && !data.pickupArrows) event.setCancelled(true);
    }

    private void updateData(Player player) {
        PlayerData data = KitPvP.getDataManager().getPlayerData(player.getUniqueId());

        data.inCombat = true;
        data.endCombatTimestamp = (long) (System.currentTimeMillis() + GeneralTask.combatTimer * 1000L);

        KitPvP.getDataManager().updateData(data);
    }

    private void executeDeath(Player entity) {
        PlayerData data = KitPvP.getDataManager().getPlayerData(entity.getUniqueId());
        if (!falldamage.contains(entity)) {
            falldamage.add(entity);
        }
        entity.closeInventory();
        entity.getActivePotionEffects().forEach(effect -> entity.removePotionEffect(effect.getType()));

        entity.setFoodLevel(20);

        entity.setLevel(entity.getLevel() / 4);
        entity.setExp(0.0f);

        entity.setHealth(entity.getMaxHealth());
        entity.setVelocity(new Vector(0, 0, 0));

        PlayerInventory inventory = entity.getInventory();

        List<ItemStack> items = Arrays.asList(inventory.getContents());
        List<ItemStack> armor = Arrays.asList(inventory.getArmorContents());

        List<ItemStack> entireInventory = new ArrayList<>(items);
        entireInventory.addAll(armor);

        dropAll(entireInventory, entity.getLocation());

        inventory.setArmorContents(new ItemStack[4]);
        inventory.clear();

        Bukkit.getPluginManager().callEvent(new PlayerDeathEvent(entity, entireInventory, 0, "sqrt(-1)"));

        Location spawn = (Location) KitPvP.getFileManager().getConfig().get("spawn-location");
        if (spawn == null) spawn = entity.getWorld().getSpawnLocation();

        if (entity.isOnline()) entity.teleport(spawn);

        Bukkit.getPluginManager().callEvent(new PlayerRespawnEvent(entity, spawn, false));
        Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> entity.setFireTicks(0), 1L);

        data.endEnderTimestamp = -1;
        data.endCombatTimestamp = -1;

        data.atSpawn = true;
        data.inCombat = false;
        data.inEnderCooldown = false;

        KitPvP.getDataManager().updateData(data);
    }

    private void dropAll(List<ItemStack> items, Location location) {
        for (ItemStack item : items) {
            if (item != null && item.getType() != Material.AIR) {
                location.getWorld().dropItem(location, item);
            }
        }
    }

    private boolean checkForCommand(String message, Player player) {
        if (!message.startsWith("/")) return false;

        PlayerData data = KitPvP.getDataManager().getPlayerData(player.getUniqueId());
        if (!data.inCombat) return false;
        if (player.isOp()) return false;

        List<String> whitelisted = KitPvP.getFileManager().getConfig().getStringList("combat.allowed-commands");

        String[] splitted = message.split(" ");
        String firstCmd = splitted.length > 0 ? splitted[0] : message;

        boolean match = whitelisted.stream().anyMatch(x -> x.equals(firstCmd));

        if (!match) {
            player.sendMessage(ChatUtils.getFormattedText("combat.command-disabled"));
        }

        return !match;
    }

    private ItemStack getLapis() {
        Dye dye = new Dye();
        dye.setColor(DyeColor.BLUE);

        ItemStack lapis = new ItemStack(dye.toItemStack());
        lapis.setAmount(64);

        return lapis;
    }

    @EventHandler
    public void onInventoryClick2(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.getType().equals(Material.POTION)) {
            return;
        }

        int maxStackSize = 64;

        event.setCurrentItem(null);

        int amount = Math.min(clickedItem.getAmount(), maxStackSize);

        ItemStack newItem = new ItemStack(clickedItem.getType(), amount, clickedItem.getDurability());
    }

}
