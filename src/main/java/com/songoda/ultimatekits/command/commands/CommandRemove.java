package com.songoda.ultimatekits.command.commands;

import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.command.AbstractCommand;
import com.songoda.ultimatekits.kit.Kit;
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
            instance.getLocale().getMessage("command.kit.nokitsupplied").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }
        Player player = (Player) sender;
        Block block = player.getTargetBlock(null, 200);
        Kit kit = instance.getKitManager().removeKitFromLocation(block.getLocation());
        if (kit == null) return ReturnType.FAILURE;

        if (instance.getHologram() != null)
            instance.getHologram().remove(kit);
        instance.getLocale().newMessage("&8Kit &9" + kit.getName() + " &8unassigned from: &a" + block.getType().toString() + "&8.").sendPrefixedMessage(player);
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
