package com.craftaro.ultimatekits.handlers;

import com.craftaro.core.compatibility.CompatibleParticleHandler;
import com.craftaro.ultimatekits.UltimateKits;
import com.craftaro.ultimatekits.kit.KitBlockData;
import com.craftaro.ultimatekits.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Map;

public class ParticleHandler {
    private final UltimateKits plugin;
    private int amt;
    private CompatibleParticleHandler.ParticleType type;

    public ParticleHandler(UltimateKits plugin) {
        this.plugin = plugin;
    }

    public void start() {
        this.amt = Settings.PARTICLE_AMOUNT.getInt() / 2;
        String typeName = Settings.PARTICLE_TYPE.getString();
        this.type = CompatibleParticleHandler.ParticleType.getParticle(typeName);
        if (this.type == null) {
            this.type = CompatibleParticleHandler.ParticleType.SPELL_WITCH;
        }
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(UltimateKits.getInstance(), this::applyParticles, 0, 5L);
    }

    private void applyParticles() {
        Map<Location, KitBlockData> kitBlocks = this.plugin.getKitManager().getKitLocations();
        for (KitBlockData kitBlockData : new ArrayList<>(kitBlocks.values())) {
            if (kitBlockData.getLocation().getWorld() == null || !kitBlockData.hasParticles()) {
                continue;
            }

            Location location = kitBlockData.getLocation();
            location.add(.5, 0, .5);

            CompatibleParticleHandler.spawnParticles(this.type, location, this.amt, 0.25, 0.25, 0.25, 0.5);
        }
    }
}
