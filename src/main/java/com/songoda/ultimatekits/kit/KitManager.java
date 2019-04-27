package com.songoda.ultimatekits.kit;

import com.songoda.ultimatekits.UltimateKits;
import org.bukkit.Location;

import java.util.*;

public final class KitManager {

    private final Map<Location, KitBlockData> kitsAtLocations = new HashMap<>();
    private List<Kit> registeredKits = new LinkedList<>();

    public boolean addKit(Kit kit) {
        if (kit == null) return false;
        return registeredKits.add(kit);
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

    public void addKitToLocation(Kit kit, Location location) {
        kitsAtLocations.put(roundLocation(location), new KitBlockData(kit, location));
    }

    public void addKitToLocation(Kit kit, Location location, KitType type, boolean hologram, boolean particles, boolean items, boolean itemOverride) {
        KitBlockData kitBlockData = kitsAtLocations.put(roundLocation(location), new KitBlockData(kit, location, type, hologram, particles, items, itemOverride));
        if (UltimateKits.getInstance().getHologram() != null)
            UltimateKits.getInstance().getHologram().update(kitBlockData);
    }

    public Kit removeKitFromLocation(Location location) {
        KitBlockData kit = getKit(roundLocation(location));

        if (kit == null) return null;

        kit.reset();

        KitBlockData removed = kitsAtLocations.remove(roundLocation(location));
        return (removed != null ? removed.getKit() : null);
    }

    public Kit getKit(String name) {
        for (Kit kit : registeredKits)
            if (kit.getName().equalsIgnoreCase(name)) return kit;
        return null;
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
