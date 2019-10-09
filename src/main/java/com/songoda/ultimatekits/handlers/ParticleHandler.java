package com.songoda.ultimatekits.handlers;

import com.songoda.core.compatibility.CompatibleParticleHandler;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.KitBlockData;
import com.songoda.ultimatekits.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Map;

/**
 * Created by songoda on 2/24/2017.
 */
public class ParticleHandler {

    private final UltimateKits plugin;
    int amt;
    String typeName;
    CompatibleParticleHandler.ParticleType type;

    public ParticleHandler(UltimateKits plugin) {
        this.plugin = plugin;
    }

    public void start() {
        amt = Settings.PARTICLE_AMOUNT.getInt() / 2;
        typeName = Settings.PARTICLE_TYPE.getString();
        type = CompatibleParticleHandler.ParticleType.getParticle(typeName);
        if (type == null) {
            type = CompatibleParticleHandler.ParticleType.SPELL_WITCH;
        }
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(UltimateKits.getInstance(), this::applyParticles, 0, 5L);
    }

    private void applyParticles() {
        Map<Location, KitBlockData> kitBlocks = plugin.getKitManager().getKitLocations();
        for (KitBlockData kitBlockData : kitBlocks.values()) {
            if (kitBlockData.getLocation().getWorld() == null || !kitBlockData.hasParticles()) continue;

            Location location = kitBlockData.getLocation();
            location.add(.5, 0, .5);

            CompatibleParticleHandler.spawnParticles(type, location, amt, 0.25, 0.25, 0.25, 0.5);
        }
    }

}
