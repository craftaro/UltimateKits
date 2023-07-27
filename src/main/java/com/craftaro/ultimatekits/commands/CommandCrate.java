package com.craftaro.ultimatekits.commands;

import com.craftaro.core.commands.AbstractCommand;
import com.craftaro.ultimatekits.UltimateKits;
import com.craftaro.ultimatekits.kit.Kit;
import com.craftaro.ultimatekits.utils.Methods;
import com.craftaro.ultimatekits.crate.Crate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandCrate extends AbstractCommand {

    public CommandCrate() {
        super(CommandType.CONSOLE_OK, "crate");
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (args.length < 3 || args.length > 4) return ReturnType.SYNTAX_ERROR;

        OfflinePlayer target = Bukkit.getPlayer(args[0]);

        if (!args[0].equalsIgnoreCase("all") && (target == null || !target.isOnline())) {
            UltimateKits.getInstance().getLocale().newMessage("&cThat username does not exist, or the user is offline!").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        Kit kit = UltimateKits.getInstance().getKitManager().getKit(args[1]);

        if (kit == null) {
            UltimateKits.getInstance().getLocale().getMessage("command.kit.kitdoesntexist").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        Crate crate = UltimateKits.getInstance().getCrateManager().getCrate(args[2]);

        if (crate == null) {
            UltimateKits.getInstance().getLocale().getMessage("command.crate.doesntexist").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        int amount = 1;
        if (args.length > 3) {
            if (!Methods.isNumeric(args[3])) {
                amount = 0;
            } else {
                amount = Integer.parseInt(args[3]);
            }
        }

        if (amount == 0) {
            UltimateKits.getInstance().getLocale().newMessage("&a" + args[3] + " &cis not a number.").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        ItemStack item = crate.getCrateItem(kit, amount);

        if (args[0].equalsIgnoreCase("all")) {
            // Give and send msg to all players online
            for (Player loopPlayer : Bukkit.getOnlinePlayers()) {
                loopPlayer.getInventory().addItem(item);
                UltimateKits.getInstance().getLocale().getMessage("event.crate.given")
                        .processPlaceholder("kit", kit.getName())
                        .processPlaceholder("crate", crate.getName())
                        .sendPrefixedMessage(loopPlayer);
            }
        } else {
            // Give to player and send msg
            target.getPlayer().getInventory().addItem(item);
            UltimateKits.getInstance().getLocale().getMessage("event.crate.given")
                    .processPlaceholder("kit", kit.getName())
                    .processPlaceholder("crate", crate.getName())
                    .sendPrefixedMessage(target.getPlayer());
        }

        // Send msg to admin
        UltimateKits.getInstance().getLocale().getMessage("command.crate.given")
                .processPlaceholder("kit", kit.getName())
                .processPlaceholder("crate", crate.getName())
                .processPlaceholder("player", args[0].equalsIgnoreCase("all") ? "all players" : target.getName());
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        List<String> tab = new ArrayList<>();

        if (args.length == 1) {
            // Players
            tab.add("all");
            for (Player player : Bukkit.getOnlinePlayers())
                tab.add(player.getName());
            return tab;
        } else if (args.length == 2) {
            // Kits
            return UltimateKits.getInstance().getKitManager().getKits().stream()
                    .map(Kit::getName).collect(Collectors.toList());
        } else if (args.length == 3) {
            // Crates
            return UltimateKits.getInstance().getCrateManager().getRegisteredCrates().stream()
                    .map(Crate::getName).collect(Collectors.toList());
        } else if (args.length == 4) {
            return Collections.singletonList("amount");
        }

        return tab;
    }

    @Override
    public String getPermissionNode() {
        return "ultimatekits.admin";
    }

    @Override
    public String getSyntax() {
        return "crate <player/all> <kit> <crate> (amount)";
    }

    @Override
    public String getDescription() {
        return "Gives a crate to a player.";
    }
}
