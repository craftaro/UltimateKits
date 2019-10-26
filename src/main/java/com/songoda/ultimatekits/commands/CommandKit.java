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
        super(false, "kit");
        this.guiManager = guiManager;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (args.length > 2) return ReturnType.SYNTAX_ERROR;

        if (args.length == 0 && sender instanceof Player) {
            // /kit - Opens GUI.
            guiManager.showGUI((Player) sender, new KitSelectorGui(instance, (Player) sender));
            return ReturnType.SUCCESS;
        }

        if (!sender.hasPermission("ultimatekits.admin")) {
            instance.getLocale().getMessage("command.general.noperms").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        if (instance.getKitManager().getKit(args[0]) == null) {
            instance.getLocale().getMessage("command.kit.kitdoesntexist").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        Kit kit = instance.getKitManager().getKit(args[0]);

        if (args.length == 1) {
            // /kit <kit> - Gives kit to self.
            if (!(sender instanceof Player)) {
                instance.getLocale().newMessage("&cYou must be a player to use this command!").sendMessage(sender);
                return ReturnType.FAILURE;
            }

            kit.giveKit((Player) sender);
            return ReturnType.SUCCESS;
        } else if (args.length == 2) {
            // /kit <kit> <player> - Gives kit to another player.

            if (!args[1].equalsIgnoreCase("all") && Bukkit.getPlayer(args[1]) == null) {
                instance.getLocale().newMessage("&cThat username does not exist, or the user is offline!").sendPrefixedMessage(sender);
                return ReturnType.FAILURE;
            }

            Player player = Bukkit.getPlayer(args[1]);
            String who = player != null ? player.getName() : "everyone";

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

            instance.getLocale().newMessage("&7You gave &9" + who + "&7 kit &9" + kit.getShowableName() + "&7.")
                    .sendPrefixedMessage(sender);
            return ReturnType.SUCCESS;
        }

        return ReturnType.SYNTAX_ERROR;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        List<String> tab = new ArrayList<>();

        if (!(sender instanceof Player)) return tab;

        if (args.length == 1) {
            for (Kit kit : instance.getKitManager().getKits()) tab.add(kit.getName());
        } else if (args.length == 2) {
            tab.add("all");
            Bukkit.getOnlinePlayers().forEach(player -> tab.add(player.getName()));
        }

        return tab;
    }

    @Override
    public String getPermissionNode() {
        return null;
    }

    @Override
    public String getSyntax() {
        return "/Kit & /Kits | /kit <kit_name> [player]";
    }

    @Override
    public String getDescription() {
        return "View all available kits.";
    }
}
