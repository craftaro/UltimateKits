package com.songoda.ultimatekits.listeners;

import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.kit.KitBlockData;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Created by songoda on 2/24/2017.
 */
public class BlockListeners implements Listener {

    private final UltimateKits instance;

    public BlockListeners(UltimateKits instance) {
        this.instance = instance;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        KitBlockData kitBlockData = instance.getKitManager().getKit(block.getLocation());
        if (kitBlockData == null) return;
        Kit kit = kitBlockData.getKit();

        instance.removeHologram(kitBlockData);

        instance.getKitManager().removeKitFromLocation(block.getLocation());

        instance.getLocale().newMessage("&8Kit &9" + kit.getName() + " &8unassigned from: &a" + block.getType() + "&8.")
                .sendPrefixedMessage(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        Block block = e.getBlockAgainst();
        KitBlockData kitBlockData = instance.getKitManager().getKit(block.getLocation());
        if (kitBlockData != null) {
            e.setCancelled(true);
        }
    }
}
