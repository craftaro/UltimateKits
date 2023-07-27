package com.craftaro.ultimatekits.crate;

import com.craftaro.core.utils.TextUtils;
import com.craftaro.ultimatekits.UltimateKits;
import com.craftaro.ultimatekits.kit.Kit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Crate {

    // Name of the crate
    private String name;

    // The amount of items this crate will give you
    private int amount;

    // The amount of kits the player is given
    private int kitAmount;

    public Crate(String name, int amount, int kitAmount) {
        this.name = name;
        this.amount = amount;
        this.kitAmount = kitAmount;
    }

    public ItemStack getCrateItem(Kit kit, int amount) {
        UltimateKits plugin = UltimateKits.getInstance();

        ItemStack itemStack = new ItemStack(Material.CHEST, amount);

        String kitName;
        if (kit != null)
            kitName = TextUtils.formatText(kit.getName(), true);
        else
            kitName = "Any";

        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(plugin.getLocale().getMessage("interface.crate.title")
                .processPlaceholder("kit", kitName)
                .processPlaceholder("crate", name)
                .getMessage());

        meta.addEnchant(Enchantment.DURABILITY, 1, true);

        List<String> lore = new ArrayList<>();
        // Dtools Ultra Crate
        lore.add(ChatColor.DARK_PURPLE + kitName + " " + ChatColor.YELLOW + name + " " + ChatColor.WHITE + "Crate");

        String desc1 = plugin.getLocale().getMessage("interface.crate.description1")
                .processPlaceholder("kit", kitName)
                .processPlaceholder("crate", name)
                .getMessage();

        if (kitName.equals("Any"))
            desc1 = desc1.replaceAll("\\[.*?]", "");
        else
            desc1 = desc1.replace("[", "").replace("]", "");

        lore.add(desc1);
        if (this.amount == -1)
            lore.add(plugin.getLocale().getMessage("interface.crate.description2")
                    .processPlaceholder("kit", kitName)
                    .processPlaceholder("crate", name)
                    .getMessage());
        else
            lore.add(plugin.getLocale().getMessage("interface.crate.description3")
                    .processPlaceholder("kit", kitName)
                    .processPlaceholder("crate", name)
                    .getMessage());
        if (kitAmount > 1)
            lore.add(plugin.getLocale().getMessage("interface.crate.description4")
                    .processPlaceholder("amt", this.kitAmount)
                    .processPlaceholder("kit", kitName)
                    .processPlaceholder("crate", name)
                    .getMessage());

        meta.setLore(lore);

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getKitAmount() {
        return kitAmount;
    }

    public void setKitAmount(int kitAmount) {
        this.kitAmount = kitAmount;
    }
}
