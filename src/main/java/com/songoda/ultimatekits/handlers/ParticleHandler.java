package com.songoda.ultimatekits.handlers;

import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.KitBlockData;
import com.songoda.ultimatekits.utils.Debugger;
import com.songoda.ultimatekits.utils.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Map;

/**
 * Created by songoda on 2/24/2017.
 */
public class ParticleHandler {

    private final UltimateKits plugin;

    public ParticleHandler(UltimateKits plugin) {
        this.plugin = plugin;
        checkDefaults();
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(UltimateKits.getInstance(), this::applyParticles, 0, 10L);
    }

    private void applyParticles() {
        try {
            int amt = plugin.getConfig().getInt("data.particlesettings.ammount");
            String type = plugin.getConfig().getString("data.particlesettings.type");

            Map<Location, KitBlockData> kitBlocks = plugin.getKitManager().getKitLocations();
            for (KitBlockData kitBlockData : kitBlocks.values()) {
                if (kitBlockData.getLocation().getWorld() == null || !kitBlockData.hasParticles()) continue;

                Location location = kitBlockData.getLocation();
                location.add(.5, 0, .5);

                if (plugin.isServerVersionAtLeast(ServerVersion.V1_8))
                    location.getWorld().spawnParticle(org.bukkit.Particle.valueOf(type), location, amt, 0.25, 0.25, 0.25);
            }

        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    private void checkDefaults() {
        try {
            if (plugin.getConfig().getInt("data.particlesettings.ammount") == 0) {
                plugin.getConfig().set("data.particlesettings.ammount", 25);
                plugin.saveConfig();
            }
            if (plugin.getConfig().getString("data.particlesettings.type") != null) return;
            plugin.getConfig().set("data.particlesettings.type", "SPELL_WITCH");
            plugin.saveConfig();
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

}
