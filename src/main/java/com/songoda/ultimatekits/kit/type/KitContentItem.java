package com.songoda.ultimatekits.kit.type;

import com.songoda.ultimatekits.utils.Methods;
import org.bukkit.inventory.ItemStack;

public class KitContentItem implements KitContent {

    private ItemStack itemStack;

    public KitContentItem(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public String getSerialized() {
        return Methods.serializeItemStack(itemStack);
    }

    @Override
    public ItemStack getItemForDisplay() {
        return itemStack.clone();
    }
}
