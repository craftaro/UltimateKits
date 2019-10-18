package com.songoda.ultimatekits.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.Kit;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandGive extends AbstractCommand {

    final UltimateKits instance;

    public CommandGive() {
        super(false, "give");
        instance = UltimateKits.getInstance();
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (args.length != 2) return ReturnType.SYNTAX_ERROR;

        if (!args[0].equalsIgnoreCase("all") && Bukkit.getPlayer(args[0]) == null) {
            instance.getLocale().newMessage("&cThat username does not exist, or the user is offline!").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        if (instance.getKitManager().getKit(args[1]) == null) {
            instance.getLocale().getMessage("command.kit.kitdoesntexist").sendPrefixedMessage(sender);
        }

        Player player = args[0].equalsIgnoreCase("all") ? null : Bukkit.getPlayer(args[0]);
        Kit kit = instance.getKitManager().getKit(args[1]);

        if (player != null) {
            kit.giveKit(player);
            instance.getLocale().getMessage("event.claim.givesuccess")
                    .processPlaceholder("kit", kit.getShowableName())
                    .sendPrefixedMessage(sender);
        } else {
            Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
                kit.giveKit(onlinePlayer);
                instance.getLocale().getMessage("event.claim.givesuccess")
                        .processPlaceholder("kit", kit.getShowableName())
                        .sendPrefixedMessage(sender);
            });
        }

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        List<String> tab = new ArrayList<>();

        if (!(sender instanceof Player)) return tab;

        if (args.length == 1) {
            tab.add("all");
            for (Player player : Bukkit.getOnlinePlayers()) tab.add(player.getName());
        } else if (args.length == 2) {
            for (Kit kit : instance.getKitManager().getKits()) tab.add(kit.getName());
        }

        return tab;
    }

    @Override
    public String getPermissionNode() {
        return "ultimatekits.admin";
    }

    @Override
    public String getSyntax() {
        return "/KitAdmin give <player/all> <kit>";
    }

    @Override
    public String getDescription() {
        return "Give a kit to a player.";
    }
}
