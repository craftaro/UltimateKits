package com.songoda.ultimatekits.kit.type;

import com.songoda.ultimatekits.utils.Methods;
import org.bukkit.inventory.ItemStack;

public class KitContentItem implements KitContent {

    private ItemStack itemStack;

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
        serialized = Methods.serializeItemStack(itemStack);
        return serialized;
    }

    @Override
    public ItemStack getItemForDisplay() {
        return itemStack.clone();
    }
}
