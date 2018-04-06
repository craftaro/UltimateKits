package com.songoda.kitpreview.conversion;

import org.bukkit.inventory.ItemStack;

import java.util.Set;

public interface Hook {

    Set<String> getKits();

    Set<ItemStack> getItems(String kit);

    long getDelay(String kit);

}