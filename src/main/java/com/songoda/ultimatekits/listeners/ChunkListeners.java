package com.songoda.ultimatekits.listeners;

import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.key.Key;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.kit.KitBlockData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by songoda on 2/24/2017.
 */
public class ChunkListeners implements Listener {

    private final UltimateKits instance;

    public ChunkListeners(UltimateKits instance) {
        this.instance = instance;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChunkLoad(ChunkLoadEvent event) {
        instance.getKitManager().getKitLocations().values().stream()
                .filter(l -> l.getLocation().getWorld() == event.getWorld()
                             && l.getLocation().getBlockX() >> 4 == event.getChunk().getX()
                             && l.getLocation().getBlockZ() >> 4 == event.getChunk().getZ())
                .forEach(instance::updateHologram);
    }
}
