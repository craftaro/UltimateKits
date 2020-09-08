package com.songoda.ultimatekits.listeners;

import com.songoda.ultimatekits.UltimateKits;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

/**
 * Created by songoda on 2/24/2017.
 */
public class ChunkListeners implements Listener {

    private final UltimateKits plugin;

    public ChunkListeners(UltimateKits plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChunkLoad(ChunkLoadEvent event) {
        plugin.getKitManager().getKitLocations().values().stream()
                .filter(l -> l.getLocation().getWorld() == event.getWorld()
                        && l.getLocation().getBlockX() >> 4 == event.getChunk().getX()
                        && l.getLocation().getBlockZ() >> 4 == event.getChunk().getZ())
                .forEach(plugin::updateHologram);
    }
}
