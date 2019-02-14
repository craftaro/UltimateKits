package com.songoda.ultimatekits.command.commands;

import com.songoda.ultimatekits.Lang;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.command.AbstractCommand;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.utils.Methods;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRemove extends AbstractCommand {

    public CommandRemove(AbstractCommand parent) {
        super("remove", parent, true, false);
    }

    @Override
    protected ReturnType runCommand(UltimateKits instance, CommandSender sender, String... args) {
        if (args.length != 1) {
            sender.sendMessage(instance.getReferences().getPrefix() + Lang.PREVIEW_NO_KIT_SUPPLIED.getConfigValue());
            return ReturnType.FAILURE;
        }
        Player player = (Player) sender;
        Block block = player.getTargetBlock(null, 200);
        Kit kit = instance.getKitManager().removeKitFromLocation(block.getLocation());
        if (kit == null) return ReturnType.FAILURE;
        instance.getHologram().remove(kit);
        player.sendMessage(Methods.formatText(UltimateKits.getInstance().getReferences().getPrefix() + "&8Kit &9" + kit.getName() + " &8unassigned from: &a" + block.getType().toString() + "&8."));
        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "ultimatekits.admin";
    }

    @Override
    public String getSyntax() {
        return "/KitAdmin remove";
    }

    @Override
    public String getDescription() {
        return "Remove a kit from the block you are looking at.";
    }
}
