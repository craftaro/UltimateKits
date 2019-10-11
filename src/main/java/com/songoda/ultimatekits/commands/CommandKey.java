package com.songoda.ultimatekits.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.core.utils.PlayerUtils;
import com.songoda.ultimatekits.UltimateKits;
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
    
    final UltimateKits instance = UltimateKits.getInstance();

    public CommandKey() {
        super(false, "key");
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (args.length != 3 && args.length != 4) {
            return ReturnType.SYNTAX_ERROR;
        }
        Kit kit = instance.getKitManager().getKit(args[0]);
        if (kit == null && !args[0].toLowerCase().equals("all")) {
            instance.getLocale().getMessage("command.kit.kitdoesntexist").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }
        Player playerTo = null;
        if (!args[2].trim().equalsIgnoreCase("all") && (playerTo = Bukkit.getPlayer(args[2])) == null) {
            instance.getLocale().newMessage("&cThat username does not exist, or the user is offline!").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }
        int amt = 1;
        if (args.length == 4) {
            if (!Methods.isNumeric(args[3])) {
                amt = 0;
            } else {
                amt = Integer.parseInt(args[3]);
            }
        }
        if (amt == 0) {
            instance.getLocale().newMessage("&a" + args[3] + " &cis not a number.").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        Key key = instance.getKeyManager().getKey(args[1]);
        if (key == null) {
            instance.getLocale().newMessage("&a" + args[1] + " &cis not a key.").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }


        if (playerTo != null) {
            PlayerUtils.giveItem(playerTo, key.getKeyItem(kit, amt));
            instance.getLocale().getMessage("event.key.given")
                    .processPlaceholder("kit", kit == null ? "Any" : kit.getShowableName())
                    .sendPrefixedMessage(playerTo);
            return ReturnType.SUCCESS;
        }
        for (Player pl : instance.getServer().getOnlinePlayers()) {
            PlayerUtils.giveItem(pl, key.getKeyItem(kit, amt));
            instance.getLocale().getMessage("event.key.given")
                    .processPlaceholder("kit", kit == null ? "Any" : kit.getShowableName())
                    .sendPrefixedMessage(pl);
        }
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        if (!(sender instanceof Player)) return null;

        List<String> tab = new ArrayList<>();

        if (args.length == 1) {
            tab.add("all");
            for (Kit kit : UltimateKits.getInstance().getKitManager().getKits())
                tab.add(kit.getName());
            return tab;
        } else if (args.length == 2) {
            for (Key key : UltimateKits.getInstance().getKeyManager().getKeys())
                tab.add(key.getName());
            return tab;
        } else if (args.length == 3) {
            tab.add("all");
            for (Player player : Bukkit.getOnlinePlayers())
                tab.add(player.getName());
            return tab;
        } else if (args.length == 4) return Arrays.asList("amount");
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
