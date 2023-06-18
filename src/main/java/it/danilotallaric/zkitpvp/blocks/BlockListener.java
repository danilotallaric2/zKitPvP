package it.danilotallaric.zkitpvp.blocks;

import it.danilotallaric.zkitpvp.KitPvP;
import it.danilotallaric.zkitpvp.data.PlayerData;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Arrays;
import java.util.List;

public class BlockListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        PlayerData data = KitPvP.getDataManager().getPlayerData(event.getPlayer().getUniqueId());

        int height = KitPvP.getFileManager().getConfig().getInt("spawn.spawn-height");
        Block block = event.getBlockPlaced();

        String blockString = block.getType().toString();
        List<String> whitelistedBlocks = KitPvP.getFileManager().getConfig().getStringList("block-manager.whitelisted-blocks");

        if (data.isBuilder) {
            event.setCancelled(false);
            return;
        }

        if (block.getLocation().getY() >= height) {

            event.setCancelled(true);
            return;
        }

        if (!whitelistedBlocks.contains(blockString)) {
            event.setCancelled(true);
            return;
        }

        String blocker = KitPvP.getFileManager().getConfig().getString("block-manager.blocking-block");
        if (blockString.equals(blocker)) {
            int cooldown = KitPvP.getFileManager().getConfig().getInt("block-manager.block-timer");
            TempBlock reforgedBlock = new TempBlock(block, cooldown);

            KitPvP.getBlockManager().addBlock(reforgedBlock);
            return;
        }

        int cooldown = KitPvP.getFileManager().getConfig().getInt("block-manager.web-timer");
        List<Block> clones = Arrays.asList(block, block.getRelative(BlockFace.NORTH), block.getRelative(BlockFace.SOUTH), block.getRelative(BlockFace.EAST), block.getRelative(BlockFace.WEST));

        clones.forEach(clone ->
        {
            if (!clone.getType().equals(Material.AIR)) {
                return;
            }

            clone.setType(Material.WEB);
            TempBlock reforgedBlock = new TempBlock(clone, cooldown);

            KitPvP.getBlockManager().addBlock(reforgedBlock);
        });

        TempBlock reforgedBlock = new TempBlock(block, cooldown);

        KitPvP.getBlockManager().addBlock(reforgedBlock);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        PlayerData data = KitPvP.getDataManager().getPlayerData(event.getPlayer().getUniqueId());
        if (data.isBuilder) {
            return;
        }

        event.setCancelled(true);
    }
}
