package com.craftaro.ultimatekits.kit.type;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface KitContent {
    String getSerialized();

    ItemStack getItemForDisplay();

    ItemStack process(Player player);
}
