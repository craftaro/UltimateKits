package com.songoda.ultimatekits.command.commands;

import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.command.AbstractCommand;
import com.songoda.ultimatekits.kit.Kit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandPreviewKit extends AbstractCommand {

    public CommandPreviewKit() {
        super(true, true,"PreviewKit");
    }

    @Override
    protected ReturnType runCommand(UltimateKits plugin, CommandSender sender, String... args) {
        Player player = (Player) sender;
        if (args.length != 1) {
            plugin.getLocale().getMessage("command.kit.nokitsupplied").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }
        Kit kit = plugin.getKitManager().getKit(args[0].toLowerCase().trim());
        if (kit == null) {
            plugin.getLocale().getMessage("command.kit.kitdoesntexist").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }
        kit.display(player, null);
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(UltimateKits instance, CommandSender sender, String... args) {
        if (!(sender instanceof Player)) return null;

        if (args.length == 2) {
            List<String> tab = new ArrayList<>();
            for (Kit kit : UltimateKits.getInstance().getKitManager().getKits())
                tab.add(kit.getName());
            return tab;
        }
        return new ArrayList<>();
    }

    @Override
    public String getPermissionNode() {
        return null;
    }

    @Override
    public String getSyntax() {
        return "/PreviewKit <kit>";
    }

    @Override
    public String getDescription() {
        return "Preview a kit.";
    }
}
