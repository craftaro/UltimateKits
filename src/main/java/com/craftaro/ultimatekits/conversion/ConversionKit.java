package com.craftaro.ultimatekits.conversion;

import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashSet;
import java.util.Set;

public class ConversionKit {

    private final Set<ItemStack> itemStack = new LinkedHashSet<>();
    private final long delay;

    public ConversionKit(Set<ItemStack> itemStack, long delay) {
        this.itemStack.addAll(itemStack);
        this.delay = delay;
    }

    public Set<ItemStack> getItemStacks() {
        return this.itemStack;
    }

    public long getDelay() {
        return this.delay;
    }
}
