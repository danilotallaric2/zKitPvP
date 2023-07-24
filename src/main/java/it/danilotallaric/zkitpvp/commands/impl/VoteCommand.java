package it.danilotallaric.zkitpvp.commands.impl;

import it.danilotallaric.zkitpvp.KitPvP;
import it.danilotallaric.zkitpvp.commands.api.KitPvPCommand;
import it.danilotallaric.zkitpvp.utils.ChatUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class VoteCommand extends KitPvPCommand {
    public VoteCommand() {
        super(KitPvP.getInstance(), "vote", "zkitpvp.commands.vote", false);
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        Player player = (Player)sender;
        player.sendMessage(ChatUtils.getFormattedText("vote.message"));

        return false;
    }
}
