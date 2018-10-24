package com.songoda.ultimatekits.handlers;

import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.KitBlockData;
import com.songoda.ultimatekits.utils.Debugger;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Map;

/**
 * Created by songoda on 2/24/2017.
 */
public class ParticleHandler {

    private final UltimateKits instance;

    public ParticleHandler(UltimateKits instance) {
        this.instance = instance;
        checkDefaults();
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(UltimateKits.getInstance(), this::applyParticles, 0, 10L);
    }

    private void applyParticles() {
        try {
            int amt = instance.getConfig().getInt("data.particlesettings.ammount");
            String type = instance.getConfig().getString("data.particlesettings.type");

            Map<Location, KitBlockData> kitBlocks = instance.getKitManager().getKitLocations();
            for (KitBlockData kitBlockData : kitBlocks.values()) {
                if (kitBlockData.getLocation().getWorld() == null || !kitBlockData.hasParticles()) continue;

                Location location = kitBlockData.getLocation();
                location.add(.5, 0, .5);

                location.getWorld().spawnParticle(org.bukkit.Particle.valueOf(type), location, amt, 0.25, 0.25, 0.25);
            }

        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    private void checkDefaults() {
        try {
            if (instance.getConfig().getInt("data.particlesettings.ammount") == 0) {
                instance.getConfig().set("data.particlesettings.ammount", 25);
                instance.saveConfig();
            }
            if (instance.getConfig().getString("data.particlesettings.type") != null) return;
            instance.getConfig().set("data.particlesettings.type", "SPELL_WITCH");
            instance.saveConfig();
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

}
