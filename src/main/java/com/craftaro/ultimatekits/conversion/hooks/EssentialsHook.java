package com.craftaro.ultimatekits.conversion.hooks;

import com.craftaro.ultimatekits.conversion.ConversionKit;
import com.craftaro.ultimatekits.conversion.Hook;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.Kit;
import com.earth2me.essentials.MetaItemStack;
import com.earth2me.essentials.config.EssentialsConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class EssentialsHook implements Hook {
    private final Essentials essentials;

    public EssentialsHook() {
        this.essentials = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
    }

    @Override
    public Map<String, ConversionKit> getKits() {
        EssentialsConfiguration cs = this.essentials.getKits().getRootConfig();
        Map<String, ConversionKit> kits = new LinkedHashMap<>();
        try {
            for (String name : cs.getKeys()) {
                Set<ItemStack> stacks = new HashSet<>();
                Kit kitObj = new Kit(name, this.essentials);
                for (String nonParse : kitObj.getItems()) {
                    String[] parts = nonParse.split(" +");
                    ItemStack item = this.essentials.getItemDb().get(parts[0], parts.length > 1 ? Integer.parseInt(parts[1]) : 1);
                    MetaItemStack metaStack = new MetaItemStack(item);
                    if (parts.length > 2 != nonParse.startsWith("/")) {
                        try {
                            metaStack.parseStringMeta(null, true, parts, 2, this.essentials);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    stacks.add(metaStack.getItemStack());
                }
                kits.put(name, new ConversionKit(stacks, Integer.toUnsignedLong((int) cs.getInt("delay", 0))));
            }
        } catch (Exception e) {
            return kits;
        }
        return kits;
    }
}
