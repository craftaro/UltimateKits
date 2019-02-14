package com.songoda.ultimatekits.command.commands;

import com.songoda.ultimatekits.Lang;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.command.AbstractCommand;
import com.songoda.ultimatekits.utils.Methods;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSet extends AbstractCommand {

    public CommandSet(AbstractCommand parent) {
        super("set", parent, true, false);
    }

    @Override
    protected ReturnType runCommand(UltimateKits instance, CommandSender sender, String... args) {
        if (args.length != 2) {
            sender.sendMessage(instance.getReferences().getPrefix() + Lang.PREVIEW_NO_KIT_SUPPLIED.getConfigValue());
            return ReturnType.FAILURE;
        }
        Player player = (Player) sender;
        String kit = args[1].toLowerCase();
        if (instance.getKitManager().getKit(kit) == null) {
            player.sendMessage(instance.getReferences().getPrefix() + Lang.KIT_DOESNT_EXIST.getConfigValue(kit));
            return ReturnType.FAILURE;
        }
        Block b = player.getTargetBlock(null, 200);
        instance.getKitManager().addKitToLocation(instance.getKitManager().getKit(kit), b.getLocation());
        sender.sendMessage(Methods.formatText(instance.getReferences().getPrefix() + "&8Kit &a" + kit + " &8set to: &a" + b.getType().toString() + "&8."));
        return ReturnType.SUCCESS;
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
