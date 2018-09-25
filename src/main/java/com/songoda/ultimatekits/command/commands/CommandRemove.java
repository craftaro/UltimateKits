package com.songoda.ultimatekits.command.commands;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.ultimatekits.Lang;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.command.AbstractCommand;
import com.songoda.ultimatekits.kit.object.Kit;
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
            sender.sendMessage(instance.references.getPrefix() + Lang.PREVIEW_NO_KIT_SUPPLIED.getConfigValue());
            return ReturnType.FAILURE;
        }
        Player player = (Player) sender;
        Block b = player.getTargetBlock(null, 200);
        Kit kit = instance.getKitManager().removeKitFromLocation(b.getLocation());
        UltimateKits.getInstance().holo.updateHolograms();
        player.sendMessage(Arconix.pl().getApi().format().formatText(UltimateKits.getInstance().references.getPrefix() + "&8Kit &9" + kit.getName() + " &8unassigned from: &a" + b.getType().toString() + "&8."));
        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "ultimatekits.admin";
    }

    @Override
    public String getSyntax() {
        return "/uk remove";
    }

    @Override
    public String getDescription() {
        return "Remove a kit from the block you are looking at.";
    }
}
