package com.songoda.ultimatekits.events;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.object.Kit;
import com.songoda.ultimatekits.kit.object.KitBlockData;
import com.songoda.ultimatekits.utils.Debugger;
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
            Block b = event.getBlock();
            KitBlockData kitBlockData = instance.getKitManager().getKit(b.getLocation());
            if (kitBlockData == null) return;
            Kit kit = kitBlockData.getKit();
            instance.getKitManager().removeKitFromLocation(b.getLocation());
            instance.holo.updateHolograms();
            event.getPlayer().sendMessage(Arconix.pl().getApi().format().formatText(UltimateKits.getInstance().references.getPrefix() + "&8Kit &9" + kit.getName() + " &8unassigned from: &a" + b.getType() + "&8."));

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
