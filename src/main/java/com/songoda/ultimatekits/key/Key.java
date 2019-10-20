package com.songoda.ultimatekits.key;

import com.songoda.core.utils.TextUtils;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.Kit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;

public class Key {

    // The name of the key.
    private String name;

    // The amount of items this key will give you. -1 is all;
    private int amount;

    // The amount of kit given when the key is used.
    private int kitAmount;

    public Key(String name, int amount, int kitAmount) {
        this.name = name;
        this.amount = amount;
        this.kitAmount = kitAmount;
    }


    public ItemStack getKeyItem(Kit kit, int amt) {
        UltimateKits plugin = UltimateKits.getInstance();
        ItemStack is = new ItemStack(Material.TRIPWIRE_HOOK, amt);

        String kitName;
        if (kit != null)
            kitName = TextUtils.formatText(kit.getShowableName(), true);
        else
            kitName = "Any";

        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(plugin.getLocale().getMessage("interface.key.title")
                .processPlaceholder("kit", kitName).getMessage());

        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.YELLOW + name + " " + ChatColor.WHITE + "Key");

        String desc1 = plugin.getLocale().getMessage("interface.key.description1")
                .processPlaceholder("kit", kitName).getMessage();

        if (kitName.equals("Any"))
            desc1 = desc1.replaceAll("\\[.*?\\]", "");
        else
            desc1 = desc1.replace("[", "").replace("]", "");

        lore.add(desc1);
        if (this.amount == -1)
            lore.add(plugin.getLocale().getMessage("interface.key.description2").getMessage());
        else
            lore.add(plugin.getLocale().getMessage("interface.key.description3").getMessage());
        if (kitAmount > 1)
            lore.add(plugin.getLocale().getMessage("interface.key.description4")
                    .processPlaceholder("amt", this.kitAmount).getMessage());
        meta.setLore(lore);

        is.setItemMeta(meta);

        return is;
    }


    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public int getKitAmount() {
        return kitAmount;
    }
}
