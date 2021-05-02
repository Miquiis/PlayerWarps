package me.miquiis.playerwarps.commands.playerwarp;

import me.miquiis.playerwarps.PlayerWarps;
import me.miquiis.playerwarps.commands.mCommand;
import me.miquiis.playerwarps.commands.mSubCommand;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PlayerWarpCommand extends mCommand {

    private final PlayerWarps plugin;

    public PlayerWarpCommand(PlayerWarps plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "playerwarp";
    }

    @Override
    public String getDescription() {
        return "PlayerWarps Base Command.";
    }

    @Override
    public String getSyntax() {
        return "/playerwarp";
    }

    @Override
    public String getPermission() {
        return "playerwarps.normal";
    }

    @Override
    public String getPermissionMessage() {
        return "§cYou don't have permission to execute this command.";
    }

    @Override
    public TextComponent getHelp() {
        TextComponent main = new TextComponent("§f[§b§lPlayerWarps§r§f]§9 List of Commands and Usage:");

        for (mSubCommand subCommand : getSubcommand())
        {
            TextComponent subText = new TextComponent("\n§2> §a" + subCommand.getSyntax());
            subText.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                    "§bCommand: §2" + subCommand.getName() + "\n" +
                            "§bDescription: §2" + subCommand.getDescription() + "\n" +
                            "§bUsage: §2" + subCommand.getSyntax() + "\n" +
                            "§bPermission: §2" + subCommand.getPermission()
            ).create() ) );
            subText.setClickEvent( new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, subCommand.getSyntax()));
            main.addExtra(subText);
        }

        return main;
    }

    @Override
    public ArrayList<mSubCommand> getSubcommand() {
        return new ArrayList<mSubCommand>(){
            {
                add(new TeleportSubCommand(plugin));
                add(new ListSubCommand(plugin));
                add(new CreateSubCommand(plugin));
                add(new DeleteSubCommand(plugin));
                add(new SetWarpSubCommand(plugin));
                add(new AddMemberSubCommand(plugin));
                add(new RemoveMemberSubCommand(plugin));
                add(new ToggleLockSubCommand(plugin));
            }
        };
    }

    @Override
    public void perform(CommandSender sender, ArrayList<String> args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getPermissionMessage());
            return;
        }

        String doubleCommand = "";

        if (args.size() > 1)
        {
           doubleCommand = args.get(0) + " " + args.get(1);
        }

        if (args.size() > 0) {
            for (mSubCommand sub : getSubcommand()) {
                if (args.get(0).equalsIgnoreCase(sub.getName()) || doubleCommand.equalsIgnoreCase(sub.getName())) {

                    if (!(sender instanceof Player) && !sub.canConsole())
                    {
                        sender.sendMessage("§cYou must be a player to execute this command");
                        return;
                    }

                    if (!sender.hasPermission(sub.getPermission())) {
                        sender.sendMessage(getPermissionMessage());
                        return;
                    }

                    for (int i = 0; i < sub.getName().split(" ").length; i++)
                        args.remove(0);

                    sub.perform(this, sender, args);
                    return;

                }
            }
        }

        if (!sender.hasPermission("playerwarps.help")) {
            sender.sendMessage(getPermissionMessage());
            return;
        }

        sender.spigot().sendMessage(getHelp());
        return;
    }
}
