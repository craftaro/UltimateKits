package com.craftaro.ultimatekits.key;

import com.craftaro.core.third_party.de.tr7zw.nbtapi.NBTItem;
import com.craftaro.core.utils.ItemUtils;
import com.craftaro.core.utils.TextUtils;
import com.craftaro.ultimatekits.UltimateKits;
import com.craftaro.ultimatekits.kit.Kit;
import com.craftaro.ultimatekits.settings.Settings;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Key {
    // The name of the key.
    private final String name;

    // The number of items this key will give you; -1 is all
    private final int amount;

    // Should the key be enchanted?
    private final boolean enchanted;

    // The amount of kit given when the key is used.
    private final int kitAmount;

    public Key(String name, int amount, int kitAmount, boolean enchanted) {
        this.name = name;
        this.amount = amount;
        this.kitAmount = kitAmount;
        this.enchanted = enchanted;
    }

    public ItemStack getKeyItem(Kit kit, int amount) {
        UltimateKits plugin = UltimateKits.getInstance();
        ItemStack item = Settings.KEY_MATERIAL.getMaterial().parseItem();
        item.setAmount(amount);

        String kitName = kit != null ? TextUtils.formatText(kit.getName(), true)
                : plugin.getLocale().getMessage("general.type.any").toText();

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(plugin.getLocale().getMessage("interface.key.title")
                .processPlaceholder("kit", kitName).toText());

        if (this.enchanted) {
            ItemUtils.addGlow(item);
        }

        List<String> lore = new ArrayList<>();
        lore.add(plugin.getLocale().getMessage("interface.key.name")
                .processPlaceholder("name", this.name).toText());

        String desc1 = plugin.getLocale().getMessage("interface.key.description1")
                .processPlaceholder("kit", kitName).toText();

        if (kit == null) {
            desc1 = desc1.replaceAll("\\[.*?\\]", "");
        } else {
            desc1 = desc1.replace("[", "").replace("]", "");
        }

        lore.add(desc1);
        if (this.amount == -1) {
            lore.add(plugin.getLocale().getMessage("interface.key.description2").toText());
        } else {
            lore.add(plugin.getLocale().getMessage("interface.key.description3").toText());
        }
        if (this.kitAmount > 1) {
            lore.add(plugin.getLocale().getMessage("interface.key.description4")
                    .processPlaceholder("amt", this.kitAmount).toText());
        }
        meta.setLore(lore);

        item.setItemMeta(meta);

        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString("key", this.name);
        nbtItem.setString("kit", kit == null ? "ANY" : kit.getName());

        return nbtItem.getItem();
    }

    public String getName() {
        return this.name;
    }

    public int getAmount() {
        return this.amount;
    }

    public int getKitAmount() {
        return this.kitAmount;
    }

    public boolean isEnchanted() {
        return this.enchanted;
    }
}
