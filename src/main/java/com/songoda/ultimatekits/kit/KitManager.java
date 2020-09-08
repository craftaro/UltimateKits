package com.songoda.ultimatekits.kit;

import com.songoda.ultimatekits.UltimateKits;
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

    public Kit addKit(Kit kit) {
        if (kit == null) return null;
        registeredKits.add(kit);
        return kit;
    }

    public void removeKit(Kit kit) {
        registeredKits.remove(kit);
        removeLocationsFromKit(kit);
    }

    public void removeLocationsFromKit(Kit kit) {
        for (Map.Entry<Location, KitBlockData> entry : new ArrayList<>(kitsAtLocations.entrySet())) {
            if (entry.getValue().getKit() == kit) {
                entry.getValue().reset();
                kitsAtLocations.remove(entry.getKey());
            }
        }
    }

    public KitBlockData addKitToLocation(Kit kit, Location location) {
        KitBlockData data = new KitBlockData(kit, location);
        kitsAtLocations.put(roundLocation(location), data);
        return data;
    }

    public KitBlockData addKitToLocation(Kit kit, Location location, KitType type, boolean hologram, boolean particles, boolean items, boolean itemOverride) {
        KitBlockData data = new KitBlockData(kit, location, type, hologram, particles, items, itemOverride);
        kitsAtLocations.put(roundLocation(location), data);
        return data;
    }

    public Kit removeKitFromLocation(Location location) {
        KitBlockData kit = getKit(roundLocation(location));

        if (kit == null) return null;

        kit.reset();

        KitBlockData removed = kitsAtLocations.remove(roundLocation(location));
        UltimateKits.getInstance().getDataManager().deleteBlockData(removed);
        return (removed != null ? removed.getKit() : null);
    }

    public Kit getKit(String name) {
        return registeredKits.stream().filter(kit -> kit.getKey().equalsIgnoreCase(name.trim()))
                .findFirst().orElse(null);
    }

    public KitBlockData getKit(Location location) {
        return kitsAtLocations.get(roundLocation(location));
    }

    public List<Kit> getKits() {
        return Collections.unmodifiableList(registeredKits);
    }

    public Map<Location, KitBlockData> getKitLocations() {
        return Collections.unmodifiableMap(kitsAtLocations);
    }

    public void setKitLocations(Map<Location, KitBlockData> kits) {
        kitsAtLocations.clear();
        kitsAtLocations.putAll(kits);
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
        if (kit == null) return;

        int i = 0;
        for (Kit kit2 : registeredKits) {
            if (kit == kit2)
                break;
            i++;
        }

        int action = i - 1;
        if (up) action = i + 1;

        if (action >= 0 && action < registeredKits.size())
            Collections.swap(registeredKits, i, action);

    }
}
