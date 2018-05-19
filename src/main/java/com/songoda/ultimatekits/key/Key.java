package com.songoda.ultimatekits.key;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.ultimatekits.Lang;
import com.songoda.ultimatekits.kit.object.Kit;
import com.songoda.ultimatekits.utils.Debugger;
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

    // The amount of kit given when the key is used.
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
                kitName = Arconix.pl().getApi().format().formatText(kit.getShowableName(), true);
            else
                kitName = "Any";

            ItemMeta meta = is.getItemMeta();
            meta.setDisplayName(Arconix.pl().getApi().format().formatText(Lang.KEY_TITLE.getConfigValue(kitName)));

            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            List<String> lore = new ArrayList<>();
            lore.add(Arconix.pl().getApi().format().formatText("&e" + name + " &fKey"));

            String desc1 = Lang.KEY_DESC1.getConfigValue(kitName);

            if (kitName.equals("Any"))
                desc1 = desc1.replaceAll("\\[.*?\\]","");
            else
                desc1 = desc1.replace("[", "").replace("]", "");

            lore.add(Arconix.pl().getApi().format().formatText(desc1));
            if (this.amt == -1)
                lore.add(Arconix.pl().getApi().format().formatText(Lang.KEY_DESC2.getConfigValue()));
            else
                lore.add(Arconix.pl().getApi().format().formatText(Lang.KEY_DESC3.getConfigValue()));
            if (kitAmount > 1)
                lore.add(Arconix.pl().getApi().format().formatText(Lang.KEY_DESC4.getConfigValue(this.kitAmount)));
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
