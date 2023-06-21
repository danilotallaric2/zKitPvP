
package it.danilotallaric.zkitpvp.listeners;

import it.danilotallaric.zkitpvp.KitPvP;
import it.danilotallaric.zkitpvp.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import java.sql.Time;
import java.util.*;

public class CustomListener implements Listener {
    private final Set<UUID> sugarCooldown = new HashSet<>();
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        double balance = KitPvP.getEconomy().getBalance(event.getPlayer());
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (event.getItem() == null || event.getItem().getType() != Material.SUGAR) return;


        if (sugarCooldown.contains(player.getUniqueId())) {
            player.sendMessage(ChatUtils.getFormattedText("sugar.cooldown"));
            event.setCancelled(true);
            return;
        }


        sugarCooldown.add(player.getUniqueId());
        Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> sugarCooldown.remove(player.getUniqueId()), KitPvP.getFileManager().getConfig().getInt("sugar.timer") * 10L);

        this.removeSugarFromInventory(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, KitPvP.getFileManager().getConfig().getInt("sugar.duration") * 10, 2));
        player.sendMessage(ChatUtils.getFormattedText("sugar.used"));



    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerClaimNote(PlayerInteractEvent event) {

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getPlayer().getItemInHand();

        if (item == null || !KitPvP.isBanknote(item)) {
            return;
        }

        double amount = KitPvP.getBanknoteAmount(item);

        if (Double.compare(amount, 0) < 0) {
            return;
        }

        KitPvP.getEconomy().depositPlayer(player, amount);


        player.sendMessage(ChatUtils.getFormattedText("assegno.claim")
                .replaceAll("%soldi%", String.valueOf(amount)));


        if (item.getAmount() <= 1) {
            event.getPlayer().getInventory().removeItem(item);
        } else {
            item.setAmount(item.getAmount() - 1);
        }
    }



    private void removeSugarFromInventory(Player player) {
        player.getInventory().removeItem(new ItemStack[]{new ItemStack(Material.SUGAR, 1)});
    }
}
