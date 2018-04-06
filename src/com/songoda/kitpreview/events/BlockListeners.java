package com.songoda.kitpreview.events;

import com.songoda.arconix.Arconix;
import com.songoda.arconix.method.formatting.TextComponent;
import com.songoda.kitpreview.KitPreview;
import com.songoda.kitpreview.kits.object.Kit;
import com.songoda.kitpreview.kits.object.KitBlockData;
import com.songoda.kitpreview.utils.Debugger;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Created by songoda on 2/24/2017.
 */
public class BlockListeners implements Listener {

    private final KitPreview instance;

    public BlockListeners(KitPreview instance) {
        this.instance = instance;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        try {
            Block b = event.getBlock();
            String loc = Arconix.pl().serialize().serializeLocation(b);

            KitBlockData kitBlockData = instance.getKitManager().getKit(b.getLocation());
            if (kitBlockData == null) return;
            Kit kit = kitBlockData.getKit();
            instance.holo.updateHolograms();
            event.getPlayer().sendMessage(TextComponent.formatText(KitPreview.getInstance().references.getPrefix() + "&8Kit &9" + kit + " &8unassigned from: &a" + b.getType() + "&8."));

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
