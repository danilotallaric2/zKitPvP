package it.danilotallaric.zkitpvp.commands.api;

import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class Subcommand {

    private final String name, permission, parent;
    private final boolean playerOnly;

    public Subcommand(String parent, String name, String permission, boolean playerOnly) {
        this.parent = parent;
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

    public String getParentCommand() {
        return parent;
    }

    public boolean isPlayerOnly() {
        return playerOnly;
    }

    public abstract void execute(CommandSender sender, List<String> args);

}