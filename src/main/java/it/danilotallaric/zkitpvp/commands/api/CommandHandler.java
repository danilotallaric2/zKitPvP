package it.danilotallaric.zkitpvp.commands.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandHandler {

    private static final HashMap<KitPvPCommand, List<Subcommand>> commands = new HashMap<>();

    public static void addSubCommand(KitPvPCommand parent, Subcommand command) {
        List<Subcommand> subCommands = commands.containsKey(parent) ? commands.get(parent) : new ArrayList<>();

        subCommands.add(command);
        commands.put(parent, subCommands);
    }

    public static List<Subcommand> getSubCommands(KitPvPCommand parent) {
        if (!commands.containsKey(parent)) return new ArrayList<>();

        return commands.get(parent);
    }

    public static void removeSubCommand(KitPvPCommand parent, Subcommand command) {
        if (!commands.containsKey(parent)) return;

        List<Subcommand> subCommands = commands.get(parent);
        subCommands.remove(command);

        commands.put(parent, subCommands);
    }

}