package pl.plajer.villagedefense3.handlers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.utils.BigTextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author IvanTheBuilder
 */
public class ConfigurationManager {

    private static Main plugin;

    public ConfigurationManager(Main plugin){
        ConfigurationManager.plugin = plugin;
    }

    public static File getFile(String filename) {
        return new File(plugin.getDataFolder() + File.separator + filename + ".yml");
    }

    public static FileConfiguration getConfig(String filename) {
        File file = new File(plugin.getDataFolder() + File.separator + filename + ".yml");
        if(!file.exists()) {
            try {
                plugin.getLogger().info("Creating " + filename + ".yml because it does not exist!");
                file.createNewFile();
            } catch(IOException ex) {
                ex.printStackTrace();
                BigTextUtils.errorOccured();
                Bukkit.getConsoleSender().sendMessage("Cannot save file " + filename + ".yml!");
                Bukkit.getConsoleSender().sendMessage("Create blank file " + filename + ".yml or restart the server!");
            }
        }
        file = new File(plugin.getDataFolder(), filename + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
            config.save(file);
        } catch(InvalidConfigurationException | IOException ex) {
            ex.printStackTrace();
            BigTextUtils.errorOccured();
            Bukkit.getConsoleSender().sendMessage("Cannot save file " + filename + ".yml!");
            Bukkit.getConsoleSender().sendMessage("Create blank file " + filename + ".yml or restart the server!");
        }
        return config;
    }

    public static void saveConfig(FileConfiguration config, String name){
        try {
            config.save(new File(plugin.getDataFolder(), name + ".yml"));
        } catch(IOException e) {
            e.printStackTrace();
            BigTextUtils.errorOccured();
            Bukkit.getConsoleSender().sendMessage("Cannot save file " + name + ".yml!");
            Bukkit.getConsoleSender().sendMessage("Create blank file " + name  + ".yml or restart the server!");
        }
    }

}
