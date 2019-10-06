package com.songoda.ultimatekits.kit.type;

import com.songoda.ultimatekits.UltimateKits;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import org.bukkit.ChatColor;

public class KitContentEconomy implements KitContent {

    private final double amount;

    public KitContentEconomy(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String getSerialized() {
        return UltimateKits.getInstance().getConfig().getString("Main.Currency Symbol") + amount;
    }

    @Override
    public ItemStack getItemForDisplay() {
        ItemStack parseStack = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = parseStack.getItemMeta();

        ArrayList<String> lore = new ArrayList<>();

        int index = 0;
        while (index < String.valueOf(amount).length()) {
            lore.add(ChatColor.GREEN + (index == 0 ? UltimateKits.getInstance().getConfig().getString("Main.Currency Symbol") : "") + ChatColor.GREEN + String.valueOf(amount).substring(index, Math.min(index + 30, String.valueOf(amount).length())));
            index += 30;
        }
        meta.setLore(lore);
        meta.setDisplayName(UltimateKits.getInstance().getLocale().getMessage("general.type.money").getMessage());
        parseStack.setItemMeta(meta);
        return parseStack;
    }
}
