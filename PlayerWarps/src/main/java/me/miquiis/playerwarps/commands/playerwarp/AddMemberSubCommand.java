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

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Optional;

public class AddMemberSubCommand extends mSubCommand {

    private final ConfigManager configManager;
    private final MessagesManager messagesManager;
    private final WarpManager warpManager;

    public AddMemberSubCommand(PlayerWarps plugin)
    {
        this.configManager = plugin.getConfigManager();
        this.messagesManager = plugin.getMessagesManager();
        this.warpManager = plugin.getWarpManager();
    }

    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getDescription() {
        return "Adds a member to a specific warp.";
    }

    @Override
    public String getSyntax() {
        return "/playerwarp add <warpname> <membername> <day:hour:minute>";
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
        if (args.size() == 3)
        {
            Player player = (Player)sender;

            String warpName = args.remove(0);

            OfflinePlayer member = Bukkit.getOfflinePlayer(args.remove(0));

            Integer day, hour, minute;

            try {
                String[] time = args.remove(0).split(":");
                day = Integer.valueOf(time[0]);
                hour = Integer.valueOf(time[1]);
                minute = Integer.valueOf(time[2]);
            } catch (Exception e)
            {
                sender.sendMessage("§aTry using this syntax: §e" + getSyntax());
                return;
            }

            Instant timeEnd = Instant.now().plus(day, ChronoUnit.DAYS).plus(hour, ChronoUnit.HOURS).plus(minute, ChronoUnit.MINUTES);

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

            warpData.addTemporaryMember(member.getUniqueId(), timeEnd);
            player.sendMessage(messagesManager.getMessage("warp-add-member").replace("%WARPNAME%", warpName).replace("%MEMBERNAME%", member.getName()));
            return;
        }
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

            if (warpData.addMember(member.getUniqueId()))
            {
                player.sendMessage(messagesManager.getMessage("warp-add-member").replace("%WARPNAME%", warpName).replace("%MEMBERNAME%", member.getName()));
            }
            else
            {
                player.sendMessage(messagesManager.getMessage("warp-already-member").replace("%WARPNAME%", warpName).replace("%MEMBERNAME%", member.getName()));
            }
        }
        else
        {
            sender.sendMessage("§aTry using this syntax: §e" + getSyntax());
        }
    }
}
