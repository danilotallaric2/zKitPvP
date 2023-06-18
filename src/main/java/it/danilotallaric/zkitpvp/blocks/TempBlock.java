package it.danilotallaric.zkitpvp.blocks;

import org.bukkit.block.Block;

public class TempBlock {
    public TempBlock(Block block, int timer) {
        this.block = block;
        this.status = 0;
        this.timer = timer;
        this.maxTimer = timer;
    }

    public double timer, maxTimer;
    public int status;
    private final Block block;

    public Block getBlock() {
        return block;
    }
}
