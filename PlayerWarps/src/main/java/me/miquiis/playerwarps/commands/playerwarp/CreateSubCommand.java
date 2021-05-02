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
import java.util.OptionalInt;

public class CreateSubCommand extends mSubCommand {

    private final ConfigManager configManager;
    private final MessagesManager messagesManager;
    private final WarpManager warpManager;

    public CreateSubCommand(PlayerWarps plugin)
    {
        this.configManager = plugin.getConfigManager();
        this.messagesManager = plugin.getMessagesManager();
        this.warpManager = plugin.getWarpManager();
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getDescription() {
        return "Creates a warp in a certain location.";
    }

    @Override
    public String getSyntax() {
        return "/playerwarp create <warpname> <warpowner>";
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
        if (args.size() == 2)
        {
            Player player = (Player)sender;

            String warpName = args.remove(0);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args.remove(0));

            if (warpManager.createPlayerWarp(warpName, offlinePlayer.getUniqueId(), player.getLocation()))
            {
                player.sendMessage(messagesManager.getMessage("warp-create").replace("%WARPNAME%", warpName));
            }
            else
            {
                player.sendMessage(messagesManager.getMessage("warp-exists").replace("%WARPNAME%", warpName));
            }
        }
        else
        {
            sender.sendMessage("§aTry using this syntax: §e" + getSyntax());
        }
    }
}
