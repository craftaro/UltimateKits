package com.craftaro.ultimatekits.commands;

import com.craftaro.core.commands.AbstractCommand;
import com.craftaro.core.gui.GuiManager;
import com.craftaro.ultimatekits.UltimateKits;
import com.craftaro.ultimatekits.gui.CategorySelectorGui;
import com.craftaro.ultimatekits.gui.KitSelectorGui;
import com.craftaro.ultimatekits.kit.Kit;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandKit extends AbstractCommand {
    private final UltimateKits plugin;
    private final GuiManager guiManager;

    public CommandKit(UltimateKits plugin, GuiManager guiManager) {
        super(CommandType.CONSOLE_OK, "kit");
        this.plugin = plugin;
        this.guiManager = guiManager;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (args.length > 2) {
            return ReturnType.SYNTAX_ERROR;
        }

        if (args.length == 0 && sender instanceof Player) {
            // /kit - Opens GUI.
            if (this.plugin.getKitManager().getKits().stream().anyMatch(kit -> kit.getCategory() != null)) {
                this.guiManager.showGUI((Player) sender, new CategorySelectorGui(this.plugin, (Player) sender));
            } else {
                this.guiManager.showGUI((Player) sender, new KitSelectorGui(this.plugin, (Player) sender, null));
            }
            return ReturnType.SUCCESS;
        }

        if (this.plugin.getKitManager().getKit(args[0]) == null) {
            this.plugin.getLocale().getMessage("command.kit.kitdoesntexist").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        Kit kit = this.plugin.getKitManager().getKit(args[0]);

        if (args.length == 1) {
            // /kit <kit> - Gives kit to self.
            if (!(sender instanceof Player)) {
                return ReturnType.NEEDS_PLAYER;
            }

            if (!kit.hasPermissionToClaim((Player) sender)) {
                this.plugin.getLocale().getMessage("command.general.noperms").sendPrefixedMessage(sender);
                return ReturnType.FAILURE;
            }

            kit.processGenericUse((Player) sender, false);
            return ReturnType.SUCCESS;
        } else {
            // /kit <kit> <player> - Gives kit to another player.

            if (!sender.hasPermission("ultimatekits.admin")) {
                this.plugin.getLocale().getMessage("command.general.noperms").sendPrefixedMessage(sender);
                return ReturnType.FAILURE;
            }

            if (!args[1].equalsIgnoreCase("all") && Bukkit.getPlayer(args[1]) == null) {
                this.plugin.getLocale().newMessage("&cThat username does not exist, or the user is offline!").sendPrefixedMessage(sender);
                return ReturnType.FAILURE;
            }

            Player player = Bukkit.getPlayer(args[1]);
            String who = player != null ? player.getName() : "everyone";

            if (player != null) {
                kit.processGenericUse(player, true);
                this.plugin.getLocale().getMessage("event.claim.givesuccess")
                        .processPlaceholder("kit", kit.getName())
                        .sendPrefixedMessage(sender);
            } else {
                Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
                    kit.processGenericUse(onlinePlayer, true);
                    this.plugin.getLocale().getMessage("event.claim.givesuccess")
                            .processPlaceholder("kit", kit.getName())
                            .sendPrefixedMessage(sender);
                });
            }

            this.plugin.getLocale().newMessage("&7You gave &9" + who + "&7 kit &9" + kit.getName() + "&7.")
                    .sendPrefixedMessage(sender);
            return ReturnType.SUCCESS;
        }
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        List<String> tab = new ArrayList<>();

        if (!(sender instanceof Player)) {
            return tab;
        }

        if (args.length == 1) {
            for (Kit kit : this.plugin.getKitManager().getKits()) {
                tab.add(kit.getKey());
            }
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
