package com.craftaro.ultimatekits.listeners;

import com.craftaro.ultimatekits.UltimateKits;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class ChunkListeners implements Listener {
    private final UltimateKits plugin;

    public ChunkListeners(UltimateKits plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChunkLoad(ChunkLoadEvent event) {
        this.plugin.getKitManager().getKitLocations().values().stream()
                .filter(l -> l.getLocation().getWorld() == event.getWorld()
                        && l.getLocation().getBlockX() >> 4 == event.getChunk().getX()
                        && l.getLocation().getBlockZ() >> 4 == event.getChunk().getZ())
                .forEach(this.plugin::updateHologram);
    }
}
