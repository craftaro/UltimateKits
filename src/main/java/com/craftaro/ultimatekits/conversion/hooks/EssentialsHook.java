package com.craftaro.ultimatekits.conversion.hooks;

import com.craftaro.ultimatekits.conversion.Hook;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.Kit;
import com.earth2me.essentials.MetaItemStack;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class EssentialsHook implements Hook {
    private final Essentials essentials;

    public EssentialsHook() {
        this.essentials = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
    }

    public Set<ItemStack> getItems(String kitName) {
        Set<ItemStack> stacks = new HashSet<>();
        try {
            Kit kit = new Kit(kitName, this.essentials);

            for (String nonParse : kit.getItems()) {
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stacks;
    }

    public Set<String> getKits() {
        ConfigurationSection cs = this.essentials.getSettings().getKits();
        Set<String> kits = new HashSet<>();
        try {
            cs.getKeys(false);
        } catch (Exception e) {
            return kits;
        }
        kits.addAll(cs.getKeys(false));
        return kits;
    }

    public long getDelay(String kitName) {
        Object object = this.essentials.getSettings().getKit(kitName).getOrDefault("delay", 0);
        try {
            return Integer.toUnsignedLong((int) object);
        } catch (Exception ex) {
            return (long) object;
        }
    }
}
