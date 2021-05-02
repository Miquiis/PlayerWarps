package me.miquiis.playerwarps;

import com.earth2me.essentials.spawn.EssentialsSpawn;
import me.miquiis.playerwarps.commands.CommandManager;
import me.miquiis.playerwarps.data.WarpData;
import me.miquiis.playerwarps.events.EventsHandler;
import me.miquiis.playerwarps.managers.ConfigManager;
import me.miquiis.playerwarps.managers.MessagesManager;
import me.miquiis.playerwarps.managers.WarpManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.time.Instant;

public class PlayerWarps extends JavaPlugin {

    private static PlayerWarps instance;

    private ConfigManager configManager;
    private MessagesManager messagesManager;
    private WarpManager warpManager;

    private CommandManager commandManager;

    private EssentialsSpawn essentialsSpawn;

    private BukkitTask autoSave, autoCheck;

    @Override
    public void onEnable()
    {
        instance = this;

        this.configManager = new ConfigManager(this);
        this.messagesManager = new MessagesManager(this);
        this.warpManager = new WarpManager(this);

        new EventsHandler(this);

        this.commandManager = new CommandManager(this);

        if (getServer().getPluginManager().getPlugin("EssentialsSpawn") != null)
        {
            this.essentialsSpawn = (EssentialsSpawn)getServer().getPluginManager().getPlugin("EssentialsSpawn");
        }

        initTasks();
    }

    @Override
    public void onDisable()
    {
        stopTasks();

        warpManager.close();
    }

    private void initTasks()
    {
        autoSave = getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            warpManager.saveWarps();
        }, 20L * 60, 20L * 60);

        autoCheck = getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            Instant now = Instant.now();
            for (WarpData warpData : warpManager.getWarps())
            {
                if (warpData == null) return;
                if (warpData.getTemporaryMembers() == null) return;
                warpData.getTemporaryMembers().forEach((key, value) -> {

                    if (key == null) return; if (value == null) return;

                    if (value.isBefore(now)) {
                        warpData.removeMember(key);
                        getServer().getScheduler().runTask(this, () -> {
                            Player member = Bukkit.getPlayer(key);
                            if (member != null && member.isOnline()) {
                                member.getPlayer().teleport(getEssentialsSpawn().getSpawn("default"), PlayerTeleportEvent.TeleportCause.PLUGIN);
                            }
                        });
                    }
                });
            }
        }, 20*60, 20*60);
    }

    private void stopTasks()
    {
        if (autoSave != null) autoSave.cancel();
        if (autoCheck != null) autoCheck.cancel();
    }

    public EssentialsSpawn getEssentialsSpawn() {
        return essentialsSpawn;
    }

    public MessagesManager getMessagesManager() {
        return messagesManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public WarpManager getWarpManager() {
        return warpManager;
    }

    public static PlayerWarps getInstance() {
        return instance;
    }
}
