package com.songoda.ultimatekits.command.commands;

import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.command.AbstractCommand;
import com.songoda.ultimatekits.gui.GUIKitSelector;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandKit extends AbstractCommand {

    public CommandKit() {
        super("Kit", null, false, false);
    }

    @Override
    protected ReturnType runCommand(UltimateKits instance, CommandSender sender, String... args) {
        if (!(sender instanceof Player) && args.length != 2) {
            sender.sendMessage("Kits:");
            for (Kit kit : instance.getKitManager().getKits()) {
                sender.sendMessage(" - " + kit.getName());
            }
            return ReturnType.SUCCESS;
        }
        if (args.length == 0) {
            new GUIKitSelector(instance, (Player) sender);
            return ReturnType.SUCCESS;
        }
        if (args.length == 1) {
            Player player = (Player) sender;
            String kitName = args[0].toLowerCase();
            if (instance.getKitManager().getKit(kitName) == null) {
                instance.getLocale().getMessage("command.kit.kitdoesntexist").sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }
            Kit kit = instance.getKitManager().getKit(kitName);
            if (sender.hasPermission("ultimatekits.admin")) {
                kit.processGenericUse(player, true);
            } else {
                kit.buy(player);
            }
            return ReturnType.SUCCESS;
        }
        if (args.length == 2) {
            String kitName = args[0].toLowerCase();
            if (instance.getKitManager().getKit(kitName) == null) {
                instance.getLocale().getMessage("command.kit.kitdoesntexist").sendPrefixedMessage(sender);
                return ReturnType.FAILURE;
            }

            if (Bukkit.getPlayerExact(args[1]) == null) {
                instance.getLocale().getMessage("command.kit.playernotfound").sendPrefixedMessage(sender);
                return ReturnType.FAILURE;
            }
            Player player2 = Bukkit.getPlayer(args[1]);
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!Methods.canGiveKit(player)) {
                    UltimateKits.getInstance().getLocale().getMessage("command.general.noperms")
                            .sendPrefixedMessage(player);
                    return ReturnType.FAILURE;
                }
            }
            Kit kit = instance.getKitManager().getKit(kitName);
            kit.processGenericUse(player2, true);
            instance.getLocale().newMessage("&7You gave &9" + player2.getDisplayName() + "&7 kit &9" + kit.getShowableName() + "&7.")
                    .sendPrefixedMessage(sender);
            return ReturnType.SUCCESS;
        }
        return ReturnType.SYNTAX_ERROR;
    }

    @Override
    public String getPermissionNode() {
        return null;
    }

    @Override
    public String getSyntax() {
        return "/Kit & /Kits";
    }

    @Override
    public String getDescription() {
        return "View all available kits.";
    }
}
