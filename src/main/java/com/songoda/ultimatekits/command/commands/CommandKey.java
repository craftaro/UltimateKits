package com.songoda.ultimatekits.command.commands;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.ultimatekits.Lang;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.command.AbstractCommand;
import com.songoda.ultimatekits.key.Key;
import com.songoda.ultimatekits.kit.object.Kit;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandKey extends AbstractCommand {

    public CommandKey(AbstractCommand parent) {
        super("key", parent, false, false);
    }

    @Override
    protected ReturnType runCommand(UltimateKits instance, CommandSender sender, String... args) {
        if (args.length != 4 && args.length != 5) {
            return ReturnType.SYNTAX_ERROR;
        }
        Kit kit = instance.getKitManager().getKit(args[1]);
        if (kit == null && !args[1].toLowerCase().equals("all")) {
            sender.sendMessage(instance.references.getPrefix() + Lang.KIT_DOESNT_EXIST.getConfigValue(kit));
            return ReturnType.FAILURE;
        }
        if (Bukkit.getPlayer(args[3]) == null && !args[3].trim().equalsIgnoreCase("all")) {
            sender.sendMessage(instance.references.getPrefix() + Arconix.pl().getApi().format().formatText("&cThat username does not exist, or the user is offline!"));
            return ReturnType.FAILURE;
        }
        int amt = 1;
        if (args.length == 5) {
            if (!Arconix.pl().getApi().doMath().isNumeric(args[4])) {
                amt = 0;
            } else {
                amt = Integer.parseInt(args[4]);
            }
        }
        if (amt == 0) {
            sender.sendMessage(instance.references.getPrefix() + Arconix.pl().getApi().format().formatText("&a" + args[3] + " &cis not a number."));
            return ReturnType.FAILURE;
        }

        Key key = instance.getKeyManager().getKey(args[2]);
        if (key == null) {
            sender.sendMessage(instance.references.getPrefix() + Arconix.pl().getApi().format().formatText("&a" + args[3] + " &cis not a key."));
            return ReturnType.FAILURE;
        }


        if (!args[3].trim().equals("all")) {
            Player p = Bukkit.getPlayer(args[3]);
            p.getInventory().addItem(key.getKeyItem(kit, amt));
            p.sendMessage(instance.references.getPrefix() + Lang.KEY_GIVEN.getConfigValue(kit.getShowableName()));
            return ReturnType.SUCCESS;
        }
        for (Player pl : instance.getServer().getOnlinePlayers()) {
            pl.getInventory().addItem(key.getKeyItem(kit, amt));
            pl.sendMessage(instance.references.getPrefix() + Lang.KEY_GIVEN.getConfigValue(kit.getShowableName()));
        }
        return ReturnType.SUCCESS;
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
        return "/uk key <kit/all> <" + keys.substring(1) + "> <player/all> <amount>";
    }

    @Override
    public String getDescription() {
        return "Give a kit key to the players of your server. These keys can be used to redeem kit.";
    }
}
