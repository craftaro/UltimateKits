package com.songoda.ultimatekits.crate;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CrateManager {

    private final Set<Crate> registeredCrates = new HashSet<>();

    public boolean addCrate(Crate crate) {
        return crate != null && registeredCrates.add(crate);
    }

    public Crate getCrate(String name) {
        for (Crate crate : registeredCrates)
            if (crate.getName().equalsIgnoreCase(name))
                return crate;
        return null;
    }

    public Crate getCrate(ItemStack item) {
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasLore() || item.getType() != Material.CHEST)
            return null;

        return getCrate(ChatColor.stripColor(item.getItemMeta().getLore().get(0)).split(" ")[1]);
    }

    public Set<Crate> getRegisteredCrates() {
        return Collections.unmodifiableSet(registeredCrates);
    }

    public void clear() {
        registeredCrates.clear();
    }
}