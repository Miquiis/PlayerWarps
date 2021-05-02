package me.miquiis.playerwarps.managers;

import me.miquiis.playerwarps.PlayerWarps;
import me.miquiis.playerwarps.data.WarpData;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class ConfigManager {

    private final PlayerWarps plugin;
    private final FileConfiguration config;
    private final FileConfiguration gui;

    public ConfigManager(PlayerWarps plugin)
    {
        this.plugin = plugin;
        this.config = new YamlConfiguration();
        this.gui = new YamlConfiguration();
        load();
    }

    // GUI Configuration

    public String getWarpHead(WarpData warpData)
    {
        return getGUIString("acessible-warp-name").replace('&', '§').replace("{WARPNAME}", warpData.getWarpName());
    }

    public List<String> getWarpLores(WarpData warpData)
    {
        List<String> list = getGUIListString("accessible-warp-lores");
        for (int i = 0; i < list.size(); i++)
        {
            list.set(i, list.get(i).replace("{OWNER}", Bukkit.getOfflinePlayer(warpData.getOwnerUUID()).getName()).replace("{WARPNAME}", warpData.getWarpName()));
        }
        return list;
    }

    public String getGUIString(String path)
    {
        return gui.getString(path).replace('&', '§');
    }

    public List<String> getGUIListString(String path)
    {
        return colorList(gui.getStringList(path));
    }

    private List<String> colorList(List<String> list)
    {
        for (int i = 0; i < list.size(); i++)
        {
            list.set(i, list.get(i).replace('&', '§'));
        }
        return list;
    }

    public String getString(String path)
    {
        return config.getString(path).replace('&', '§');
    }

    public Integer getInt(String path)
    {
        return config.getInt(path);
    }

    public Double getDouble(String path) { return config.getDouble(path); }

    public List<String> getListStrings(String path) { return config.getStringList(path); }

    public Set<String> getConfigurationSection(String path) { return config.getConfigurationSection(path).getKeys(false); }

    private void load()
    {
        try
        {
            File configFile = new File(plugin.getDataFolder(), "config.yml");
            if (!configFile.exists())
            {
                plugin.saveResource("config.yml", false);
            }
            config.load(configFile);

            File guiFile = new File(plugin.getDataFolder(), "gui.yml");
            if (!guiFile.exists())
            {
                plugin.saveResource("gui.yml", false);
            }
            gui.load(guiFile);
        }
        catch (IOException | InvalidConfigurationException e)
        {
            e.printStackTrace();
        }
    }

}
