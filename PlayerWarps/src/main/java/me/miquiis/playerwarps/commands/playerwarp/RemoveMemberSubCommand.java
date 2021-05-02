package me.miquiis.playerwarps.commands.playerwarp;

import me.miquiis.playerwarps.PlayerWarps;
import me.miquiis.playerwarps.commands.mCommand;
import me.miquiis.playerwarps.commands.mSubCommand;
import me.miquiis.playerwarps.data.WarpData;
import me.miquiis.playerwarps.managers.ConfigManager;
import me.miquiis.playerwarps.managers.MessagesManager;
import me.miquiis.playerwarps.managers.WarpManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.Optional;

public class RemoveMemberSubCommand extends mSubCommand {

    private final PlayerWarps plugin;
    private final ConfigManager configManager;
    private final MessagesManager messagesManager;
    private final WarpManager warpManager;

    public RemoveMemberSubCommand(PlayerWarps plugin)
    {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.messagesManager = plugin.getMessagesManager();
        this.warpManager = plugin.getWarpManager();
    }

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getDescription() {
        return "Removes a member to a specific warp.";
    }

    @Override
    public String getSyntax() {
        return "/playerwarp remove <warpname> <membername>";
    }

    @Override
    public Boolean canConsole() {
        return false;
    }

    @Override
    public String getPermission() {
        return "playerwarps.normal";
    }

    @Override
    public ArrayList<mSubCommand> getSubcommand() {
        return null;
    }

    @Override
    public void perform(mCommand main, CommandSender sender, ArrayList<String> args) {
        if (args.size() == 2)
        {
            Player player = (Player)sender;

            String warpName = args.remove(0);

            OfflinePlayer member = Bukkit.getOfflinePlayer(args.remove(0));

            Optional<WarpData> warpDataOptional = warpManager.getWarp(warpName);

            if (!warpDataOptional.isPresent())
            {
                player.sendMessage(messagesManager.getMessage("warp-not-exist").replace("%WARPNAME%", warpName));
                return;
            }

            WarpData warpData = warpDataOptional.get();

            if (!warpData.canManage(player))
            {
                player.sendMessage(messagesManager.getMessage("warp-no-permission").replace("%WARPNAME%", warpName));
                return;
            }

            if (warpData.removeMember(member.getUniqueId()))
            {
                player.sendMessage(messagesManager.getMessage("warp-remove-member").replace("%WARPNAME%", warpName).replace("%MEMBERNAME%", member.getName()));
                if (member.isOnline())
                {
                    member.getPlayer().teleport(plugin.getEssentialsSpawn().getSpawn("default"), PlayerTeleportEvent.TeleportCause.PLUGIN);
                }
            }
            else
            {
                player.sendMessage(messagesManager.getMessage("warp-not-member").replace("%WARPNAME%", warpName).replace("%MEMBERNAME%", member.getName()));
            }
        }
        else
        {
            sender.sendMessage("§aTry using this syntax: §e" + getSyntax());
        }
    }
}
