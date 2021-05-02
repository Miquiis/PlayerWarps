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

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ListSubCommand extends mSubCommand {

    private final ConfigManager configManager;
    private final MessagesManager messagesManager;
    private final WarpManager warpManager;

    public ListSubCommand(PlayerWarps plugin)
    {
        this.configManager = plugin.getConfigManager();
        this.messagesManager = plugin.getMessagesManager();
        this.warpManager = plugin.getWarpManager();
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "Shows a list of all owned warps.";
    }

    @Override
    public String getSyntax() {
        return "/playerwarp list";
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
        if (args.size() == 0)
        {
            Player player = (Player)sender;

            String listOfWarps = "§9§lList of Warps:";

            boolean hasWarps = false;

            for (WarpData warpData : warpManager.getPlayerWarps(player.getUniqueId()))
            {
                Instant now = Instant.now();
                hasWarps = true;
                listOfWarps += "\n§cName: §e" + warpData.getWarpName();
                for (UUID memberUUID : warpData.getMembersUUID())
                {
                    OfflinePlayer member = Bukkit.getOfflinePlayer(memberUUID);
                    listOfWarps += "\n §2> §a" + member.getName();
                }
                for (Map.Entry<UUID, Instant> memberUUID : warpData.getTemporaryMembers().entrySet())
                {
                    OfflinePlayer member = Bukkit.getOfflinePlayer(memberUUID.getKey());
                    Long days = ChronoUnit.DAYS.between(now, memberUUID.getValue()), hours = ChronoUnit.HOURS.between(now, memberUUID.getValue()) % 24, minutes = ChronoUnit.MINUTES.between(now, memberUUID.getValue()) % 60;
                    listOfWarps += "\n §2> §a" + member.getName() + String.format(" §8%s Day(s), %s Hour(s) and %s Minute(s) left", days,hours,minutes);
                }
            }

            if (hasWarps)
            {
                player.sendMessage(listOfWarps);
            }
            else
            {
                player.sendMessage(messagesManager.getMessage("list-no-warps"));
            }
        }
        else
        {
            sender.sendMessage("§aTry using this syntax: §e" + getSyntax());
        }
    }
}
