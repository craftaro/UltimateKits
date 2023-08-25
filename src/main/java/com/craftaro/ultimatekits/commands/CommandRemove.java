package com.craftaro.ultimatekits.commands;

import com.craftaro.core.commands.AbstractCommand;
import com.craftaro.core.hooks.HologramManager;
import com.craftaro.ultimatekits.UltimateKits;
import com.craftaro.ultimatekits.kit.Kit;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandRemove extends AbstractCommand {
    private final UltimateKits plugin;

    public CommandRemove(UltimateKits plugin) {
        super(CommandType.PLAYER_ONLY, "remove");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;
        Block block = player.getTargetBlock(null, 200);
        Kit kit = this.plugin.getKitManager().removeKitFromLocation(block.getLocation());
        if (kit == null) {
            return ReturnType.FAILURE;
        }

        if (HologramManager.isEnabled()) {
            this.plugin.getKitManager().getKitLocations().values().stream()
                    .filter(data -> data.getKit() == kit)
                    .forEach(this.plugin::removeHologram);
        }

        this.plugin.getLocale().newMessage("&8Kit &9" + kit.getKey() + " &8unassigned from: &a" + block.getType() + "&8.").sendPrefixedMessage(player);
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
