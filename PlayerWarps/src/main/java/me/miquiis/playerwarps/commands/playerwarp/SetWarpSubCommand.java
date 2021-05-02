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

import java.util.ArrayList;
import java.util.Optional;

public class SetWarpSubCommand extends mSubCommand {

    private final ConfigManager configManager;
    private final MessagesManager messagesManager;
    private final WarpManager warpManager;

    public SetWarpSubCommand(PlayerWarps plugin)
    {
        this.configManager = plugin.getConfigManager();
        this.messagesManager = plugin.getMessagesManager();
        this.warpManager = plugin.getWarpManager();
    }

    @Override
    public String getName() {
        return "setwarp";
    }

    @Override
    public String getDescription() {
        return "Sets a location to a specific warp.";
    }

    @Override
    public String getSyntax() {
        return "/playerwarp setwarp <warpname>";
    }

    @Override
    public Boolean canConsole() {
        return false;
    }

    @Override
    public String getPermission() {
        return "playerwarps.admin";
    }

    @Override
    public ArrayList<mSubCommand> getSubcommand() {
        return null;
    }

    @Override
    public void perform(mCommand main, CommandSender sender, ArrayList<String> args) {
        if (args.size() == 1)
        {
            Player player = (Player)sender;

            String warpName = args.remove(0);

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

            warpData.setWarp(player.getLocation());
            player.sendMessage(messagesManager.getMessage("warp-set-position").replace("%WARPNAME%", warpName));
        }
        else
        {
            sender.sendMessage("§aTry using this syntax: §e" + getSyntax());
        }
    }
}
