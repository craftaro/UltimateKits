package com.craftaro.ultimatekits.kit.type;

import com.craftaro.core.chat.AdventureUtils;
import com.craftaro.core.hooks.EconomyManager;
import com.craftaro.core.utils.NumberUtils;
import com.craftaro.ultimatekits.UltimateKits;
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
        return this.amount;
    }

    @Override
    public String getSerialized() {
        return UltimateKits.getInstance().getConfig().getString("Main.Currency Symbol") + this.amount;
    }

    @Override
    public ItemStack getItemForDisplay() {
        ItemStack parseStack = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = parseStack.getItemMeta();

        ArrayList<String> lore = new ArrayList<>();

        int index = 0;
        while (index < String.valueOf(this.amount).length()) {
            lore.add(ChatColor.GREEN + (index == 0 ? UltimateKits.getInstance().getConfig().getString("Main.Currency Symbol") : "") + ChatColor.GREEN + String.valueOf(this.amount).substring(index, Math.min(index + 30, String.valueOf(this.amount).length())));
            index += 30;
        }
        AdventureUtils.formatItemLore(parseStack, lore);
        AdventureUtils.formatItemName(parseStack, UltimateKits.getInstance().getLocale().getMessage("general.type.money").getMessage());
        parseStack.setItemMeta(meta);
        return parseStack;
    }

    @Override
    public ItemStack process(Player player) {
        try {
            EconomyManager.deposit(player, this.amount);
            UltimateKits.getInstance().getLocale().getMessage("event.claim.eco")
                    .processPlaceholder("amt", NumberUtils.formatNumber(this.amount))
                    .sendPrefixedMessage(player);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
