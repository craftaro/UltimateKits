package com.songoda.ultimatekits.conversion.hooks;

import bammerbom.ultimatecore.bukkit.api.UC;
import bammerbom.ultimatecore.bukkit.api.UKit;
import com.songoda.ultimatekits.conversion.Hook;
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
    public Set<ItemStack> getItems(String kit) {
        UKit uKit = new UKit(kit);
        Set<ItemStack> items = new HashSet<>(uKit.getItems());
        return items;
    }

    @Override
    public long getDelay(String kit) {
        return 0;
    }
}
