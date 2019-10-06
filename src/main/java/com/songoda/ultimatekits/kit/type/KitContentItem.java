package com.songoda.ultimatekits.kit.type;

import com.songoda.ultimatekits.UltimateKits;
import org.bukkit.inventory.ItemStack;

public class KitContentItem implements KitContent {

    private final ItemStack itemStack;

    private String serialized = null;

    public KitContentItem(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public String getSerialized() {
        if (serialized != null) return serialized;
        serialized = UltimateKits.getInstance().getItemSerializer().serializeItemStackToJson(itemStack);
        return serialized;
    }

    @Override
    public ItemStack getItemForDisplay() {
        return itemStack.clone();
    }
}
