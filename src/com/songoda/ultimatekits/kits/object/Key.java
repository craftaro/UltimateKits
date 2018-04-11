package com.songoda.ultimatekits.kits.object;

import com.songoda.arconix.method.formatting.TextComponent;
import com.songoda.ultimatekits.Lang;
import com.songoda.ultimatekits.utils.Debugger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Key {

    // The name of the key.
    private String name;

    // The amount of items this key will give you. -1 is all;
    private int amt;

    // The amount of kits given when the key is used.
    private int kitAmount;

    public Key(String name, int amt, int kitAmount) {
        this.name = name;
        this.amt = amt;
        this.kitAmount = kitAmount;
    }

    public ItemStack getKeyItem(Kit kit, int amt) {
        ItemStack is = null;
        try {
            is = new ItemStack(Material.TRIPWIRE_HOOK, amt);

            String kitName;
            if (kit != null)
                kitName = TextComponent.formatText(kit.getShowableName(), true);
            else
                kitName = "All";

            ItemMeta meta = is.getItemMeta();
            meta.setDisplayName(TextComponent.formatText(Lang.KEY_TITLE.getConfigValue(kitName)));

            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            List<String> lore = new ArrayList<String>();
            lore.add(TextComponent.formatText(name + " &fKey"));
            lore.add(TextComponent.formatText(Lang.KEY_DESC1.getConfigValue(kitName)));
            if (this.amt == -1)
                lore.add(TextComponent.formatText(Lang.KEY_DESC2.getConfigValue()));
            else
                lore.add(TextComponent.formatText(Lang.KEY_DESC3.getConfigValue()));
            if (kitAmount > 1)
                lore.add(TextComponent.formatText(Lang.KEY_DESC4.getConfigValue(this.kitAmount)));
            meta.setLore(lore);

            is.setItemMeta(meta);
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }

        return is;
    }


    public String getName() {
        return name;
    }

    public int getAmt() {
        return amt;
    }

    public int getKitAmount() {
        return kitAmount;
    }
}
