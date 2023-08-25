package com.craftaro.ultimatekits.kit;

import com.craftaro.ultimatekits.UltimateKits;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class KitManager {
    private final Map<Location, KitBlockData> kitsAtLocations = new HashMap<>();
    private final List<Kit> registeredKits = new LinkedList<>();
    private boolean hasOrderChanged = false;

    public Kit addKit(Kit kit) {
        if (kit == null) {
            return null;
        }

        this.registeredKits.add(kit);
        return kit;
    }

    public void removeKit(Kit kit) {
        this.registeredKits.remove(kit);
        removeLocationsFromKit(kit);
    }

    public void removeLocationsFromKit(Kit kit) {
        for (Map.Entry<Location, KitBlockData> entry : new ArrayList<>(this.kitsAtLocations.entrySet())) {
            if (entry.getValue().getKit() == kit) {
                entry.getValue().reset();
                this.kitsAtLocations.remove(entry.getKey());
            }
        }
    }

    public KitBlockData addKitToLocation(Kit kit, Location location) {
        KitBlockData data = new KitBlockData(kit, location);
        this.kitsAtLocations.put(roundLocation(location), data);
        return data;
    }

    public KitBlockData addKitToLocation(Kit kit, Location location, KitType type, boolean hologram, boolean particles, boolean items, boolean itemOverride) {
        KitBlockData data = new KitBlockData(kit, location, type, hologram, particles, items, itemOverride);
        this.kitsAtLocations.put(roundLocation(location), data);
        return data;
    }

    public Kit removeKitFromLocation(Location location) {
        KitBlockData kit = getKit(roundLocation(location));

        if (kit == null) {
            return null;
        }

        kit.reset();

        KitBlockData removed = this.kitsAtLocations.remove(roundLocation(location));
        UltimateKits.getInstance().getKitDataManager().deleteBlockData(removed);
        return (removed != null ? removed.getKit() : null);
    }

    public Kit getKit(String name) {
        return this.registeredKits.stream().filter(kit -> kit.getKey().equalsIgnoreCase(name.trim()))
                .findFirst().orElse(null);
    }

    public KitBlockData getKit(Location location) {
        return this.kitsAtLocations.get(roundLocation(location));
    }

    public List<Kit> getKits() {
        return Collections.unmodifiableList(this.registeredKits);
    }

    public Map<Location, KitBlockData> getKitLocations() {
        return Collections.unmodifiableMap(this.kitsAtLocations);
    }

    public void setKitLocations(Map<Location, KitBlockData> kits) {
        this.kitsAtLocations.clear();
        this.kitsAtLocations.putAll(kits);
    }

    public void clearKits() {
        this.registeredKits.clear();
        this.kitsAtLocations.clear();
    }

    private Location roundLocation(Location location) {
        location = location.clone();
        location.setX(location.getBlockX());
        location.setY(location.getBlockY());
        location.setZ(location.getBlockZ());
        return location;
    }

    public void moveKit(Kit kit, boolean up) {
        if (kit == null) {
            return;
        }

        int i = 0;
        for (Kit kit2 : this.registeredKits) {
            if (kit == kit2) {
                break;
            }
            i++;
        }

        int action = i - 1;
        if (up) {
            action = i + 1;
        }

        if (action >= 0 && action < this.registeredKits.size()) {
            Collections.swap(this.registeredKits, i, action);
        }
        this.hasOrderChanged = true;
    }

    public boolean hasOrderChanged() {
        return this.hasOrderChanged;
    }

    public void savedOrderChange() {
        this.hasOrderChanged = false;
    }
}
