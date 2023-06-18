package it.danilotallaric.zkitpvp.commands.api;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public abstract class KitPvPCommand implements CommandExecutor {

    private String noPermissionMsg, onlyPlayerMsg, noSubCommandFoundMsg;
    private final String name, permission;
    private final boolean playerOnly;
    private final JavaPlugin plugin;

    public KitPvPCommand(JavaPlugin plugin, String name, String permission, boolean playerOnly) {
        this.plugin = plugin;
        this.name = name;
        this.permission = permission;
        this.playerOnly = playerOnly;
    }

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

    public boolean isPlayerOnly() {
        return playerOnly;
    }

    public void registerExecutor() {
        plugin.getCommand(name).setExecutor(this);
    }

    public void setNoPermissionMessage(String message) {
        this.noPermissionMsg = message;
    }

    public void setNoSubCommandFoundMessage(String message) {
        this.noSubCommandFoundMsg = message;
    }

    public void setOnlyPlayerMessage(String message) {
        this.onlyPlayerMsg = message;
    }

    public abstract boolean execute(CommandSender sender, List<String> args);

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (playerOnly && !(sender instanceof Player) && onlyPlayerMsg != null) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', onlyPlayerMsg));
            return true;
        }

        if (permission != null && !sender.hasPermission(permission) && noPermissionMsg != null) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', noPermissionMsg));
            return true;
        }

        if (args.length > 0 && CommandHandler.getSubCommands(this).size() > 0) {
            List<Subcommand> subcommands = CommandHandler.getSubCommands(this);
            Subcommand subcommand = subcommands.stream().filter(sub -> sub.getName().equalsIgnoreCase(args[0])).findFirst().orElse(null);

            if (subcommand == null) {
                if (noSubCommandFoundMsg != null) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', noSubCommandFoundMsg));
                }
                return true;
            }

            if (subcommand.isPlayerOnly() && !(sender instanceof Player) && onlyPlayerMsg != null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', onlyPlayerMsg));
                return true;
            }

            subcommand.execute(sender, Arrays.asList(args));
        }
        else execute(sender, Arrays.asList(args));
        return true;
    }
}