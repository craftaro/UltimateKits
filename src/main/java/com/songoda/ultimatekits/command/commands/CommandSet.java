package com.songoda.ultimatekits.command.commands;

import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.command.AbstractCommand;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.utils.Methods;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandSet extends AbstractCommand {

    public CommandSet(AbstractCommand parent) {
        super(parent, true, "set");
    }

    @Override
    protected ReturnType runCommand(UltimateKits instance, CommandSender sender, String... args) {
        if (args.length != 2) {
            instance.getLocale().getMessage("command.kit.nokitsupplied").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }
        Player player = (Player) sender;
        String kit = args[1].toLowerCase();
        if (instance.getKitManager().getKit(kit) == null) {
            instance.getLocale().getMessage("command.kit.kitdoesntexist").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }
        Block b = player.getTargetBlock(null, 200);
        instance.getKitManager().addKitToLocation(instance.getKitManager().getKit(kit), b.getLocation());
        instance.getLocale().newMessage("&8Kit &a" + kit + " &8set to: &a" + b.getType().toString() + "&8.")
                .sendPrefixedMessage(sender);
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
        return "ultimatekits.admin";
    }

    @Override
    public String getSyntax() {
        return "/KitAdmin set <kit>";
    }

    @Override
    public String getDescription() {
        return "Make the block you are looking at display a kit.";
    }
}
