package com.craftaro.ultimatekits.kit.type;

import com.craftaro.core.hooks.EconomyManager;
import com.craftaro.ultimatekits.UltimateKits;
import com.craftaro.ultimatekits.utils.Methods;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

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

    @Override
    public ItemStack process(Player player) {
        try {
            EconomyManager.deposit(player, amount);
            UltimateKits.getInstance().getLocale().getMessage("event.claim.eco")
                    .processPlaceholder("amt", Methods.formatEconomy(amount))
                    .sendPrefixedMessage(player);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
