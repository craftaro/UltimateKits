package com.craftaro.ultimatekits.conversion.hooks;

import bammerbom.ultimatecore.bukkit.api.UC;
import bammerbom.ultimatecore.bukkit.api.UKit;
import com.craftaro.ultimatekits.conversion.ConversionKit;
import com.craftaro.ultimatekits.conversion.Hook;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class UltimateCoreHook implements Hook {

    @Override
    public Map<String, ConversionKit> getKits() {
        Map<String, ConversionKit> list = new LinkedHashMap<>();
        List<UKit> kits = UC.getServer().getKits();
        for (UKit kit : kits) {
            list.put(kit.getName(), new ConversionKit(new HashSet<>(kit.getItems()), 0));
        }
        return list;
    }
}
