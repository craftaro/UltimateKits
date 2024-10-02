package com.craftaro.ultimatekits.utils;

import com.craftaro.core.third_party.de.tr7zw.nbtapi.NBT;
import org.bukkit.inventory.ItemStack;

public class ItemSerializer {
    public static ItemStack deserializeItemStackFromJson(String jsonString) {
        return NBT.itemStackFromNBT(NBT.parseNBT(jsonString));
    }

    public static String serializeItemStackToJson(ItemStack itemStack) {
        return NBT.itemStackToNBT(itemStack).toString();
    }
}
