package me.miquiis.playerwarps.events;

import me.miquiis.playerwarps.PlayerWarps;
import me.miquiis.playerwarps.gui.CustomHolder;
import me.miquiis.playerwarps.gui.Icon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;

public class EventsHandler {

    private final PlayerWarps plugin;

    private final ArrayList<CustomEventHandler> listeners = new ArrayList<CustomEventHandler>() {
        {
            add(new CustomGUIHandler());
        }
    };

    public EventsHandler(PlayerWarps plugin)
    {
        this.plugin = plugin;

        for (CustomEventHandler listener : listeners)
        {
            plugin.getServer().getPluginManager().registerEvents(listener, plugin);
            listener.init(plugin);
        }
    }

    public void reload()
    {
        listeners.forEach(list -> {
            list.reload(plugin);
        });
    }

}

abstract class CustomEventHandler implements Listener {

    public abstract void init(PlayerWarps plugin);

    public abstract void reload(PlayerWarps plugin);

}

class CustomGUIHandler extends CustomEventHandler {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e)
    {
        if (e.getClickedInventory() == null) return;

        if (!(e.getView().getTopInventory().getHolder() instanceof CustomHolder)) return;

        if (e.getClickedInventory().getHolder() instanceof CustomHolder)
        {
            e.setCancelled(true);

            if (e.getWhoClicked() instanceof Player)
            {
                Player player = (Player)e.getWhoClicked();

                if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

                CustomHolder customHolder = (CustomHolder)e.getView().getTopInventory().getHolder();

                Icon icon = customHolder.getIcon(e.getRawSlot());
                if (icon == null) return;

                icon.clickActions.forEach(c -> c.execute(player));

            }
        }
        else
        {
            if (e.isRightClick()) e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e)
    {
        if (e.getInventory().getHolder() instanceof CustomHolder)
        {
            e.setCancelled(true);
        }
    }

    @Override
    public void init(PlayerWarps plugin) {

    }

    @Override
    public void reload(PlayerWarps plugin) {

    }
}