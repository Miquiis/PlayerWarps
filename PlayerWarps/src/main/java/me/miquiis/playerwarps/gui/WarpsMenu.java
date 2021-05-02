package me.miquiis.playerwarps.gui;

import me.miquiis.playerwarps.PlayerWarps;
import me.miquiis.playerwarps.data.WarpData;
import me.miquiis.playerwarps.managers.ConfigManager;
import me.miquiis.playerwarps.managers.MessagesManager;
import me.miquiis.playerwarps.managers.WarpManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Set;

public class WarpsMenu extends CustomHolder {

    private Inventory inventory;
    private WarpManager warpManager;
    private MessagesManager messagesManager;
    private ConfigManager configManager;
    private Set<WarpData> accessWarps;
    private int page;

    public WarpsMenu(Player player, WarpManager warpManager, int page) {
        super(9*3, "§dList of Warps");
        this.warpManager = warpManager;
        this.messagesManager = PlayerWarps.getInstance().getMessagesManager();
        this.configManager = PlayerWarps.getInstance().getConfigManager();
        this.accessWarps = warpManager.getHasAccessWarps(player);
        this.page = page;
    }

    public Inventory getInventory()
    {
        final Icon previousPage = new Icon(CustomItem.createItem(Material.PAPER, "§c§lPrevious Page", new ArrayList<String>(){{add("§7Click to go to the previous page.");}}));
        final Icon nextPage = new Icon(CustomItem.createItem(Material.PAPER, "§2§lNext Page", new ArrayList<String>(){{add("§7Click to go to the next page.");}}));

        super.setIcon(18, previousPage.addClickAction(p -> {
            if (page == 1) return;
            WarpsMenu warpMenu = new WarpsMenu(p, warpManager, page - 1);
            p.openInventory(warpMenu.getInventory());
        }));
        super.setIcon(26, nextPage.addClickAction(p -> {
            if (accessWarps.size() < 25 * (page + 1)) return;
            WarpsMenu warpMenu = new WarpsMenu(p, warpManager, page + 1);
            p.openInventory(warpMenu.getInventory());
        }));

        int slot = 0;

        for (WarpData warpData : accessWarps)
        {
            if (slot == 18 || slot == 26) continue;

            final Icon head = new Icon(CustomItem.createSkull(
                    Bukkit.getOfflinePlayer(warpData.getOwnerUUID()),
                    configManager.getWarpHead(warpData),
                    configManager.getWarpLores(warpData)
            ));

            super.setIcon(slot, head.addClickAction(player -> {
                if (warpData.hasAccess(player))
                {
                    player.teleport(warpData.getWarpLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                    player.sendMessage(messagesManager.getMessage("warp-teleport").replace("%WARPNAME%", warpData.getWarpName()));
                }
                else
                {
                    player.sendMessage(messagesManager.getMessage("warp-no-access").replace("%WARPNAME%", warpData.getWarpName()));
                }
            }));
            slot++;
        }

        return super.getInventory();
    }

}
