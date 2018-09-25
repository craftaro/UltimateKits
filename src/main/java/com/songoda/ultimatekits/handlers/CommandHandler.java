package com.songoda.ultimatekits.handlers;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.ultimatekits.Lang;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.key.Key;
import com.songoda.ultimatekits.kit.KitsGUI;
import com.songoda.ultimatekits.kit.object.Kit;
import com.songoda.ultimatekits.utils.Debugger;
import com.songoda.ultimatekits.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by songoda on 2/24/2017.
 */
public class CommandHandler implements CommandExecutor {

    private final UltimateKits instance;

    public CommandHandler(UltimateKits instance) {
        this.instance = instance;
    }

    public void help(CommandSender sender, int page) {
        sender.sendMessage("");
        sender.sendMessage(Arconix.pl().getApi().format().formatText("&7Page: &a" + page + " of 2 ======================"));
        if (page == 1) {
            sender.sendMessage(Arconix.pl().getApi().format().formatText(instance.references.getPrefix() + "&7" + instance.getDescription().getVersion() + " Created by &5&l&oBrianna"));
            sender.sendMessage(Arconix.pl().getApi().format().formatText(" &8- " + "&cKits &7View all available kit."));
            sender.sendMessage(Arconix.pl().getApi().format().formatText(" &8- " + "&dPK <kit> &7Preview a kit."));
            sender.sendMessage(Arconix.pl().getApi().format().formatText(" &8- " + "&aUK help <page> &7Displays this page."));
            sender.sendMessage(Arconix.pl().getApi().format().formatText(" &8- " + "&aUK reload &7Reload the Configuration and Language files."));
            sender.sendMessage(Arconix.pl().getApi().format().formatText(" &8- " + "&aUK edit <kit> &7Edit a kit."));
            sender.sendMessage(Arconix.pl().getApi().format().formatText(" &8- " + "&aUK set <kit> &7Make the block you are looking at display a kit"));
        } else if (page == 2) {
            sender.sendMessage(Arconix.pl().getApi().format().formatText(" &8- " + "&aUK createkit <kit> &7Create a kit in a GUI."));
            sender.sendMessage(Arconix.pl().getApi().format().formatText(" &8- " + "&aUK remove &7Remove a kit from the block you are looking at."));
            StringBuilder keys = new StringBuilder();
            for (Key key : instance.getKeyManager().getKeys()) {
                keys.append("/").append(key.getName());
            }
            sender.sendMessage(Arconix.pl().getApi().format().formatText(" &8- " + "&aUK key <kit/all> <" + keys.substring(1) + "> <player/all> <amount> &7Give a kit key to the players of your server. These keys can be used to redeem kit."));
            sender.sendMessage(Arconix.pl().getApi().format().formatText("&aTo edit a kit block hold shift and right click it."));
        } else {
            sender.sendMessage(Arconix.pl().getApi().format().formatText(instance.references.getPrefix() + "That page does not exist!"));
        }
        sender.sendMessage("");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            if (cmd.getName().equalsIgnoreCase("ultimatekits")) {
            } else if (cmd.getName().equalsIgnoreCase("previewkit")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(instance.references.getPrefix() + Lang.NO_CONSOLE_ACCESS.getConfigValue());
                    return true;
                }
                Player p = (Player) sender;
                if (args.length != 1) {
                    p.sendMessage(instance.references.getPrefix() + Lang.PREVIEW_NO_KIT_SUPPLIED.getConfigValue());
                    return true;
                }
                Kit kit = instance.getKitManager().getKit(args[0].toLowerCase().trim());
                if (kit == null) {
                    p.sendMessage(instance.references.getPrefix() + Lang.KIT_DOESNT_EXIST.getConfigValue());
                    return true;
                }
                kit.display(p, false);
            }

        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
        return true;
    }
}