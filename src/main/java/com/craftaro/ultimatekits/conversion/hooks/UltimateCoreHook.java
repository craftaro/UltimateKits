package com.craftaro.ultimatekits.conversion.hooks;

import bammerbom.ultimatecore.bukkit.api.UC;
import bammerbom.ultimatecore.bukkit.api.UKit;
import com.craftaro.ultimatekits.conversion.Hook;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UltimateCoreHook implements Hook {
    @Override
    public Set<String> getKits() {
        Set<String> list = new HashSet<>();
        List<UKit> kits = UC.getServer().getKits();
        for (UKit kit : kits) {
            list.add(kit.getName());
        }
        return list;
    }

    @Override
    public Set<ItemStack> getItems(String kitName) {
        UKit uKit = new UKit(kitName);
        return new HashSet<>(uKit.getItems());
    }

    @Override
    public long getDelay(String kitName) {
        return 0;
    }
}
