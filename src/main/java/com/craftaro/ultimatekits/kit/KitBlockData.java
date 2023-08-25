package com.craftaro.ultimatekits.kit;

import com.craftaro.ultimatekits.UltimateKits;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public class KitBlockData {
    // This is the unique identifier for this block data.
    // It is reset on every plugin load.
    // Used for holograms.
    private final UUID uniqueId = UUID.randomUUID();

    private final Kit kit;
    private final Location location;
    private boolean hologram, particles, items;
    private boolean itemOverride;
    private KitType type;

    public KitBlockData(Kit kit, Location location, KitType type, boolean hologram, boolean particles, boolean items, boolean itemOverride) {
        this.kit = kit;
        this.location = location;
        this.hologram = hologram;
        this.particles = particles;
        this.items = items;
        this.itemOverride = itemOverride;
        this.type = type;
    }

    public KitBlockData(Kit kit, Location location) {
        this(kit, location, KitType.PREVIEW, false, false, false, false);
    }

    public void reset() {
        setShowHologram(false);
        setDisplayingItems(false);
        setHasParticles(false);
        UltimateKits.getInstance().getDisplayItemHandler().displayItem(this);

        UltimateKits.getInstance().removeHologram(this);
        UltimateKits.getInstance().getKitDataManager().updateBlockData(this);
    }

    public Kit getKit() {
        return this.kit;
    }

    public Location getLocation() {
        return this.location.clone();
    }

    public boolean isInLoadedChunk() {
        return this.location != null && this.location.getWorld() != null && this.location.getWorld().isChunkLoaded(((int) this.location.getX()) >> 4, ((int) this.location.getZ()) >> 4);
    }

    public int getX() {
        return this.location.getBlockX();
    }

    public int getY() {
        return this.location.getBlockY();
    }

    public int getZ() {
        return this.location.getBlockZ();
    }

    public World getWorld() {
        return this.location.getWorld();
    }

    public boolean showHologram() {
        return this.hologram;
    }

    public void setShowHologram(boolean hologram) {
        this.hologram = hologram;
    }

    public boolean hasParticles() {
        return this.particles;
    }

    public void setHasParticles(boolean particles) {
        this.particles = particles;
    }

    public boolean isDisplayingItems() {
        return this.items;
    }

    public void setDisplayingItems(boolean items) {
        this.items = items;
    }

    public boolean isItemOverride() {
        return this.itemOverride;
    }

    public void setItemOverride(boolean itemOverride) {
        this.itemOverride = itemOverride;
    }

    public KitType getType() {
        return this.type;
    }

    public void setType(KitType type) {
        this.type = type;
    }

    public String getHologramId() {
        return "UltimateKits-" + this.uniqueId;
    }
}
