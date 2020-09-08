package com.songoda.ultimatekits.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.core.hooks.HologramManager;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.Kit;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandRemove extends AbstractCommand {

    final UltimateKits instance = UltimateKits.getInstance();

    public CommandRemove() {
        super(true, "remove");
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;
        Block block = player.getTargetBlock(null, 200);
        Kit kit = instance.getKitManager().removeKitFromLocation(block.getLocation());
        if (kit == null) return ReturnType.FAILURE;

        if (HologramManager.isEnabled()) {
            instance.getKitManager().getKitLocations().values().stream()
                    .filter(data -> data.getKit() == kit)
                    .forEach(data -> instance.removeHologram(data));
        }

        instance.getLocale().newMessage("&8Kit &9" + kit.getKey() + " &8unassigned from: &a" + block.getType().toString() + "&8.").sendPrefixedMessage(player);
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return new ArrayList<>();
    }

    @Override
    public String getPermissionNode() {
        return "ultimatekits.admin";
    }

    @Override
    public String getSyntax() {
        return "remove";
    }

    @Override
    public String getDescription() {
        return "Remove a kit from the block you are looking at.";
    }
}
