package com.songoda.ultimatekits.command.commands;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.ultimatekits.Lang;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.command.AbstractCommand;
import com.songoda.ultimatekits.kit.KitsGUI;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandKits extends AbstractCommand {

    public CommandKits() {
        super("Kits", null, true, false);
    }

    @Override
    protected ReturnType runCommand(UltimateKits instance, CommandSender sender, String... args) {
        if (args.length == 0) {
            KitsGUI.show((Player) sender, 1);
            return ReturnType.SUCCESS;
        }
        if (args.length == 1) {
            Player p = (Player) sender;
            String kitName = args[0].toLowerCase();
            if (instance.getKitManager().getKit(kitName) == null) {
                p.sendMessage(instance.getReferences().getPrefix() + Lang.KIT_DOESNT_EXIST.getConfigValue(kitName));
                return ReturnType.FAILURE;
            }
            Kit kit = instance.getKitManager().getKit(kitName);
            if (sender.hasPermission("ultimatekits.admin")) {
                kit.give(p, false, false, true);
            } else {
                kit.buy(p);
            }
            return ReturnType.SUCCESS;
        }
        if (args.length == 2) {
            String kitName = args[0].toLowerCase();
            if (instance.getKitManager().getKit(kitName) == null) {
                sender.sendMessage(instance.getReferences().getPrefix() + Lang.KIT_DOESNT_EXIST.getConfigValue(kitName));
                return ReturnType.FAILURE;
            }

            if (Bukkit.getPlayerExact(args[1]) == null) {
                sender.sendMessage(instance.getReferences().getPrefix() + Lang.PLAYER_NOT_FOUND.getConfigValue(kitName));
                return ReturnType.FAILURE;
            }
            Player p2 = Bukkit.getPlayer(args[1]);
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (!Methods.canGiveKit(p)) {
                    p.sendMessage(instance.getReferences().getPrefix() + Lang.NO_PERM.getConfigValue());
                    return ReturnType.FAILURE;
                }
            }
            Kit kit = instance.getKitManager().getKit(kitName);
            kit.give(p2, false, false, true);
            sender.sendMessage(instance.getReferences().getPrefix() + Arconix.pl().getApi().format().formatText("&7You gave &9" + p2.getDisplayName() + "&7 kit &9" + kit.getShowableName() + "&7."));
            return ReturnType.SUCCESS;
        }
        sender.sendMessage(instance.getReferences().getPrefix() + Arconix.pl().getApi().format().formatText(Lang.SYNTAX.getConfigValue()));
        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return null;
    }

    @Override
    public String getSyntax() {
        return "/Kits";
    }

    @Override
    public String getDescription() {
        return "View all available kits.";
    }
}
