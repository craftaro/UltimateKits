package com.songoda.ultimatekits.hologram;

import com.songoda.arconix.api.hologram.HologramObject;
import com.songoda.arconix.plugin.Arconix;
import com.songoda.ultimatekits.UltimateKits;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;

public class HologramArconix extends Hologram {

    private com.songoda.arconix.api.packets.Hologram hologramManager;

    public HologramArconix(UltimateKits instance) {
        super(instance);
        this.hologramManager = Arconix.pl().getApi().packetLibrary.getHologramManager();
    }

    @Override
    public void add(Location location, ArrayList<String> lines) {
        fixLocation(location);
        HologramObject hologram = new HologramObject(null, location, lines);
        hologramManager.addHologram(hologram);
    }

    @Override
    public void remove(Location location) {
        fixLocation(location);
        location.add(0, 0.25, 0);
        hologramManager.removeHologram(location, 5);
    }

    @Override
    public void update(Location location, ArrayList<String> lines) {
        remove(location.clone());
        fixLocation(location);
        HologramObject hologram = new HologramObject(null, location, lines);
        Bukkit.getScheduler().scheduleSyncDelayedTask(UltimateKits.getInstance(), () -> {
            hologramManager.addHologram(hologram);
        }, 1L);
    }

    private void fixLocation(Location location) {
        location.add(0.5, 0.75, 0.5);
    }
}
