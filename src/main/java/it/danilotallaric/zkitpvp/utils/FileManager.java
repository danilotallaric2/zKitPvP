package it.danilotallaric.zkitpvp.utils;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class FileManager {
    public FileManager(Plugin plugin) {
        this.instance = plugin;
        this.config = saveConfig("config.yml");
        this.messages = saveConfig("messages.yml");
    }

    public void saveFile(FileConfiguration configuration, File file) {
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private FileConfiguration saveConfig(String configName) {
        File file = new File(instance.getDataFolder(), configName);

        if (!file.exists()) {
            instance.saveResource(configName, false);
        }

        return loadConfig(file);
    }

    public FileConfiguration loadConfig(File file) {
        YamlConfiguration configuration = new YamlConfiguration();

        try {
            configuration.load(file);
        } catch (IOException | InvalidConfigurationException ex) {
            ex.printStackTrace();
        }

        return configuration;
    }

    private final FileConfiguration config;

    public FileConfiguration getConfig() {
        return config;
    }

    private final FileConfiguration messages;

    public FileConfiguration getMessages() {
        return messages;
    }

    private Plugin instance;
}
