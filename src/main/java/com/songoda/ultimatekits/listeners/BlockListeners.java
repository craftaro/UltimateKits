package com.songoda.ultimatekits.listeners;

import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.kit.KitBlockData;
import com.songoda.ultimatekits.utils.Debugger;
import com.songoda.ultimatekits.utils.Methods;
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
        try {
            Block block = event.getBlock();
            KitBlockData kitBlockData = instance.getKitManager().getKit(block.getLocation());
            if (kitBlockData == null) return;
            Kit kit = kitBlockData.getKit();
            instance.getKitManager().removeKitFromLocation(block.getLocation());

            if (instance.getHologram() != null)
                instance.getHologram().remove(kitBlockData);
            event.getPlayer().sendMessage(Methods.formatText(instance.getReferences().getPrefix() + "&8Kit &9" + kit.getName() + " &8unassigned from: &a" + block.getType() + "&8."));

        } catch (Exception e) {
            Debugger.runReport(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        try {
            Block b = e.getBlockAgainst();
            KitBlockData kitBlockData = instance.getKitManager().getKit(b.getLocation());
            if (kitBlockData != null) {
                e.setCancelled(true);
            }
        } catch (Exception ee) {
            Debugger.runReport(ee);
        }
    }
}
