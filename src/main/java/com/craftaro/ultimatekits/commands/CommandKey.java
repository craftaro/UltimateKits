package com.craftaro.ultimatekits.commands;

import com.craftaro.core.commands.AbstractCommand;
import com.craftaro.core.utils.NumberUtils;
import com.craftaro.core.utils.PlayerUtils;
import com.craftaro.ultimatekits.UltimateKits;
import com.craftaro.ultimatekits.key.Key;
import com.craftaro.ultimatekits.kit.Kit;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandKey extends AbstractCommand {
    private final UltimateKits plugin;

    public CommandKey(UltimateKits plugin) {
        super(CommandType.CONSOLE_OK, "key");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (args.length != 3 && args.length != 4) {
            return ReturnType.SYNTAX_ERROR;
        }
        Kit kit = this.plugin.getKitManager().getKit(args[0]);
        if (kit == null && !args[0].equalsIgnoreCase("all")) {
            this.plugin.getLocale().getMessage("command.kit.kitdoesntexist").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }
        Player playerTo = null;
        if (!args[2].trim().equalsIgnoreCase("all") && (playerTo = Bukkit.getPlayer(args[2])) == null) {
            this.plugin.getLocale().newMessage("&cThat username does not exist, or the user is offline!").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }
        int amt = 1;
        if (args.length == 4) {
            if (!NumberUtils.isInt(args[3])) {
                amt = 0;
            } else {
                amt = Integer.parseInt(args[3]);
            }
        }
        if (amt == 0) {
            this.plugin.getLocale().newMessage("&a" + args[3] + " &cis not a number.").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        Key key = this.plugin.getKeyManager().getKey(args[1]);
        if (key == null) {
            this.plugin.getLocale().newMessage("&a" + args[1] + " &cis not a key.").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }


        if (playerTo != null) {
            PlayerUtils.giveItem(playerTo, key.getKeyItem(kit, amt));
            this.plugin.getLocale().getMessage("event.key.given")
                    .processPlaceholder("kit", kit == null ? "Any" : kit.getName())
                    .sendPrefixedMessage(playerTo);
            return ReturnType.SUCCESS;
        }
        for (Player pl : this.plugin.getServer().getOnlinePlayers()) {
            PlayerUtils.giveItem(pl, key.getKeyItem(kit, amt));
            this.plugin.getLocale().getMessage("event.key.given")
                    .processPlaceholder("kit", kit == null ? "Any" : kit.getName())
                    .sendPrefixedMessage(pl);
        }
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        if (!(sender instanceof Player)) {
            return null;
        }

        List<String> tab = new ArrayList<>();

        if (args.length == 1) {
            tab.add("all");
            for (Kit kit : this.plugin.getKitManager().getKits()) {
                tab.add(kit.getKey());
            }
            return tab;
        } else if (args.length == 2) {
            for (Key key : this.plugin.getKeyManager().getKeys()) {
                tab.add(key.getName());
            }
            return tab;
        } else if (args.length == 3) {
            tab.add("all");
            for (Player player : Bukkit.getOnlinePlayers()) {
                tab.add(player.getName());
            }
            return tab;
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
        StringBuilder keys = new StringBuilder();
        for (Key key : this.plugin.getKeyManager().getKeys()) {
            keys.append("/").append(key.getName());
        }
        return "key <kit/all> <" + keys.substring(1) + "> <player/all> <amount>";
    }

    @Override
    public String getDescription() {
        return "Give a kit key to the players of your server. These keys can be used to redeem kit.";
    }
}
