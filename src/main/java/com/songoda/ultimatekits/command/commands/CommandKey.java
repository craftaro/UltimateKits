package com.songoda.ultimatekits.command.commands;

import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.command.AbstractCommand;
import com.songoda.ultimatekits.key.Key;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandKey extends AbstractCommand {

    public CommandKey(AbstractCommand parent) {
        super(parent, false, "key");
    }

    @Override
    protected ReturnType runCommand(UltimateKits instance, CommandSender sender, String... args) {
        if (args.length != 4 && args.length != 5) {
            return ReturnType.SYNTAX_ERROR;
        }
        Kit kit = instance.getKitManager().getKit(args[1]);
        if (kit == null && !args[1].toLowerCase().equals("all")) {
            instance.getLocale().getMessage("command.kit.kitdoesntexist").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }
        if (Bukkit.getPlayer(args[3]) == null && !args[3].trim().equalsIgnoreCase("all")) {
            instance.getLocale().newMessage("&cThat username does not exist, or the user is offline!").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }
        int amt = 1;
        if (args.length == 5) {
            if (!Methods.isNumeric(args[4])) {
                amt = 0;
            } else {
                amt = Integer.parseInt(args[4]);
            }
        }
        if (amt == 0) {
            instance.getLocale().newMessage("&a" + args[3] + " &cis not a number.").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        Key key = instance.getKeyManager().getKey(args[2]);
        if (key == null) {
            instance.getLocale().newMessage("&a" + args[3] + " &cis not a key.").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }


        if (!args[3].trim().equals("all")) {
            Player p = Bukkit.getPlayer(args[3]);
            p.getInventory().addItem(key.getKeyItem(kit, amt));
            instance.getLocale().getMessage("event.key.given")
                    .processPlaceholder("kit", kit == null ? "Any" : kit.getShowableName())
                    .sendPrefixedMessage(p);
            return ReturnType.SUCCESS;
        }
        for (Player pl : instance.getServer().getOnlinePlayers()) {
            pl.getInventory().addItem(key.getKeyItem(kit, amt));
            instance.getLocale().getMessage("event.key.given")
                    .processPlaceholder("kit", kit == null ? "Any" : kit.getShowableName())
                    .sendPrefixedMessage(pl);
        }
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(UltimateKits instance, CommandSender sender, String... args) {
        if (!(sender instanceof Player)) return null;

        List<String> tab = new ArrayList<>();

        if (args.length == 2) {
            tab.add("all");
            for (Kit kit : UltimateKits.getInstance().getKitManager().getKits())
                tab.add(kit.getName());
            return tab;
        } else if (args.length == 3) {
            for (Key key : UltimateKits.getInstance().getKeyManager().getKeys())
                tab.add(key.getName());
            return tab;
        } else if (args.length == 4) {
            tab.add("all");
            for (Player player : Bukkit.getOnlinePlayers())
                tab.add(player.getName());
            return tab;
        } else if (args.length == 5) return Arrays.asList("amount");
        return tab;
    }

    @Override
    public String getPermissionNode() {
        return "ultimatekits.admin";
    }

    @Override
    public String getSyntax() {
        StringBuilder keys = new StringBuilder();
        for (Key key : UltimateKits.getInstance().getKeyManager().getKeys()) {
            keys.append("/").append(key.getName());
        }
        return "/KitAdmin key <kit/all> <" + keys.substring(1) + "> <player/all> <amount>";
    }

    @Override
    public String getDescription() {
        return "Give a kit key to the players of your server. These keys can be used to redeem kit.";
    }
}
