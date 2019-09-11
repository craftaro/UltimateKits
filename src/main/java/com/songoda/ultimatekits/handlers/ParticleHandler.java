package com.songoda.ultimatekits.handlers;

import com.songoda.core.compatibility.CompatibleParticleHandler;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.KitBlockData;
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
        checkDefaults();
    }

    public void start() {
        amt = plugin.getConfig().getInt("data.particlesettings.ammount") / 2;
        typeName = plugin.getConfig().getString("data.particlesettings.type");
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

    private void checkDefaults() {
        if (plugin.getConfig().getInt("data.particlesettings.ammount") == 0) {
            plugin.getConfig().set("data.particlesettings.ammount", 25);
            plugin.saveConfig();
        }
        if (plugin.getConfig().getString("data.particlesettings.type") != null) return;
        plugin.getConfig().set("data.particlesettings.type", "SPELL_WITCH");
        plugin.saveConfig();
    }

}
