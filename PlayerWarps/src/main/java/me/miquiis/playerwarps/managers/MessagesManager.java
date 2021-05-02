package me.miquiis.playerwarps.managers;

import me.miquiis.playerwarps.PlayerWarps;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MessagesManager {

    private final PlayerWarps plugin;
    private final Map<String, String> messages;
    private final FileConfiguration config;

    public MessagesManager(PlayerWarps plugin)
    {
        this.plugin = plugin;
        this.config = new YamlConfiguration();
        this.messages = new HashMap<>();

        loadMessages();
    }

    private void loadMessages()
    {
        try
        {
            File configFile = new File(plugin.getDataFolder(), "messages.yml");
            if (!configFile.exists())
            {
                plugin.saveResource("messages.yml", false);
            }
            config.load(configFile);

            for (String key : config.getConfigurationSection("custom-messages").getKeys(false))
            {
                messages.put(key, config.getString("custom-messages." + key));
            }

        }
        catch (IOException | InvalidConfigurationException e)
        {
            e.printStackTrace();
        }
    }

    public String getMessage(String path)
    {
        return formatMessage(messages.get(path) == null ? "&cMissing message in: &a" + path : messages.get(path));
    }

    public String getRawMessage(String path)
    {
        return messages.get(path);
    }

    public String formatMessage(String message)
    {
        return message.replace('&', '§');
    }

}
