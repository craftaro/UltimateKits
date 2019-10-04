package com.songoda.ultimatekits.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.core.gui.GuiManager;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.gui.KitSelectorGui;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandKit extends AbstractCommand {

    final UltimateKits instance = UltimateKits.getInstance();
    final GuiManager guiManager;

    public CommandKit(GuiManager guiManager) {
        super(true, "kit");
        this.guiManager = guiManager;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (!(sender instanceof Player) && args.length != 2) {
            sender.sendMessage("Kits:");
            for (Kit kit : instance.getKitManager().getKits()) {
                sender.sendMessage(" - " + kit.getName());
            }
            return ReturnType.SUCCESS;
        }
        if (args.length == 0) {
            guiManager.showGUI((Player) sender, new KitSelectorGui(instance, (Player) sender));
        } else if (args.length == 1) {
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
                kit.buy(player, guiManager);
            }
        } else if (args.length == 2) {
            String kitName = args[0].toLowerCase();
            if (instance.getKitManager().getKit(kitName) == null) {
                instance.getLocale().getMessage("command.kit.kitdoesntexist").sendPrefixedMessage(sender);
                return ReturnType.FAILURE;
            }

            Player player2 = Bukkit.getPlayer(args[1]);
            if (player2 == null || !player2.isOnline()) {
                instance.getLocale().getMessage("command.kit.playernotfound").sendPrefixedMessage(sender);
                return ReturnType.FAILURE;
            }
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
        } else {
            return ReturnType.SYNTAX_ERROR;
        }
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return new ArrayList<>();
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
