package com.craftaro.ultimatekits.conversion;

import com.craftaro.ultimatekits.UltimateKits;
import com.craftaro.ultimatekits.conversion.hooks.CMIHook;
import com.craftaro.ultimatekits.conversion.hooks.DefaultHook;
import com.craftaro.ultimatekits.conversion.hooks.EssentialsHook;
import com.craftaro.ultimatekits.conversion.hooks.UltimateCoreHook;
import com.craftaro.ultimatekits.kit.Kit;
import com.craftaro.ultimatekits.kit.KitItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class Convert {

    public static void runKitConversions(UltimateKits plugin) {
        if (!plugin.getKitConfig().contains("Kits")) {
            if (Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
                try {
                    Class.forName("com.earth2me.essentials.metrics.MetricsListener");
                } catch (ClassNotFoundException ex) {
                    convertKits(plugin, new EssentialsHook());
                }
            }
            if (Bukkit.getPluginManager().isPluginEnabled("UltimateCore"))
                convertKits(plugin, new UltimateCoreHook());
            if (Bukkit.getPluginManager().isPluginEnabled("CMI"))
                convertKits(plugin, new CMIHook());
        }
        if (!isInJsonFormat(plugin))
            convertKits(plugin, new DefaultHook());
    }

    private static void convertKits(UltimateKits plugin, Hook hook) {
        try {
            Map<String, ConversionKit> kits = hook.getKits();
            for (Map.Entry<String, ConversionKit> entry : kits.entrySet()) {
                Kit kitObj = plugin.getKitManager().addKit(new Kit(entry.getKey()));
                if (kitObj == null)
                    continue;

                ConversionKit cvt = entry.getValue();

                for (ItemStack item : cvt.getItemStacks()) {
                    if (item == null || item.getType() == Material.AIR)
                        continue;
                    kitObj.getContents().add(new KitItem(item));
                }
                kitObj.setDelay(cvt.getDelay());
            }
            plugin.saveKits(true);
        } catch (NoSuchMethodError | NoClassDefFoundError e) {
            System.out.println("UltimateKits conversion failed.");
        }
    }

    private static boolean isInJsonFormat(UltimateKits plugin) {
        if (!plugin.getKitConfig().contains("Kits"))
            return false;

        for (String kit : plugin.getKitConfig().getConfigurationSection("Kits").getKeys(false))
            if (plugin.getKitConfig().contains("Kits." + kit + ".items")) {
                List<String> itemList = plugin.getKitConfig().getStringList("Kits." + kit + ".items");
                if (!itemList.isEmpty())
                    if (itemList.get(0).startsWith("{"))
                        return true;
            }

        return false;
    }
}
