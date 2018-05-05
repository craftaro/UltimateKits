package com.songoda.ultimatekits.conversion;

import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.conversion.hooks.DefaultHook;
import com.songoda.ultimatekits.conversion.hooks.EssentialsHook;
import com.songoda.ultimatekits.conversion.hooks.UltimateCoreHook;
import com.songoda.ultimatekits.utils.Methods;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Convert {

    private final UltimateKits instance;

    private Hook hook;

    public Convert(UltimateKits instance) {
        this.instance = instance;
        if (instance.getServer().getPluginManager().getPlugin("Essentials") != null) {
            try {
                Class.forName("com.earth2me.essentials.metrics.MetricsListener");
                hook = new DefaultHook();
            } catch (ClassNotFoundException ex) {
                hook = new EssentialsHook();
            }
        } else if (instance.getServer().getPluginManager().getPlugin("UltimateCore") != null) {
            hook = new UltimateCoreHook();
        } else {
            hook = new DefaultHook();
        }

        if (hook.getKits().size() == 0) hook = new DefaultHook();

        convertKits();
    }


    public void convertKits() {
        Set<String> kits = hook.getKits();

        if (instance.getKitFile().getConfig().contains("Kits")) return;

        for (String kit : kits) {
            List<String> serializedItems = new ArrayList<>();
            for (ItemStack item : hook.getItems(kit)) {
                serializedItems.add(Methods.serializeItemStack(item));
            }
            instance.getKitFile().getConfig().set("Kits." + kit + ".items", serializedItems);
            instance.getKitFile().getConfig().set("Kits." + kit + ".delay", hook.getDelay(kit));
            instance.getKitFile().getConfig().set("Kits." + kit + ".price", 0D);
        }
        instance.getKitFile().saveConfig();
    }
}
