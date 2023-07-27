package com.craftaro.ultimatekits.conversion;

import com.craftaro.ultimatekits.UltimateKits;
import com.craftaro.ultimatekits.kit.Kit;
import com.craftaro.ultimatekits.kit.KitItem;
import com.craftaro.ultimatekits.conversion.hooks.CMIHook;
import com.craftaro.ultimatekits.conversion.hooks.DefaultHook;
import com.craftaro.ultimatekits.conversion.hooks.EssentialsHook;
import com.craftaro.ultimatekits.conversion.hooks.UltimateCoreHook;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

public class Convert {

    public static void runKitConversions() {
        if (!UltimateKits.getInstance().getKitConfig().contains("Kits")) {
            if (Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
                try {
                    Class.forName("com.earth2me.essentials.metrics.MetricsListener");
                } catch (ClassNotFoundException ex) {
                    convertKits(new EssentialsHook());
                }
            }
            if (Bukkit.getPluginManager().isPluginEnabled("UltimateCore")) {
                convertKits(new UltimateCoreHook());
            }
            if (Bukkit.getPluginManager().isPluginEnabled("CMI")) {
                convertKits(new CMIHook());
            }
        }
        if (!isInJsonFormat()) {
            convertKits(new DefaultHook());
        }
    }

    private static void convertKits(Hook hook) {
        try {
            Set<String> kits = hook.getKits();
            for (String kit : kits) {
                Kit kitObj = UltimateKits.getInstance().getKitManager().addKit(new Kit(kit));
                if (kitObj == null) continue;
                for (ItemStack item : hook.getItems(kit)) {
                    if (item == null || item.getType() == Material.AIR) continue;
                    kitObj.getContents().add(new KitItem(item));
                }
                kitObj.setDelay(hook.getDelay(kit));
            }
            UltimateKits.getInstance().saveKits(true);
        } catch (NoSuchMethodError | NoClassDefFoundError e) {
            System.out.println("UltimateKits conversion failed.");
        }
    }

    private static boolean isInJsonFormat() {
        if (!UltimateKits.getInstance().getKitConfig().contains("Kits")) return false;
        for (String kit : UltimateKits.getInstance().getKitConfig().getConfigurationSection("Kits").getKeys(false)) {
            if (UltimateKits.getInstance().getKitConfig().contains("Kits." + kit + ".items")) {
                List<String> itemList = UltimateKits.getInstance().getKitConfig().getStringList("Kits." + kit + ".items");
                if (itemList.size() > 0) {
                    if (itemList.get(0).startsWith("{")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}