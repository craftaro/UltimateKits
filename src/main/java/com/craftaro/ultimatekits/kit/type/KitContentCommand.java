package com.craftaro.ultimatekits.kit.type;

import com.craftaro.core.chat.AdventureUtils;
import com.craftaro.ultimatekits.UltimateKits;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class KitContentCommand implements KitContent {
    private final String command; // Stored like "eco give <player> 100"

    public KitContentCommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return this.command;
    }

    @Override
    public String getSerialized() {
        return "/" + this.command;
    }

    @Override
    public ItemStack getItemForDisplay() {
        ItemStack stack = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = stack.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        int index = 0;
        while (index < this.command.length()) {
            lore.add(ChatColor.GREEN + (index == 0 ? "/" : "") + ChatColor.GREEN + this.command.substring(index, Math.min(index + 30, this.command.length())));
            index += 30;
        }
        AdventureUtils.formatItemLore(stack, lore);
        AdventureUtils.formatItemName(stack, UltimateKits.getInstance().getLocale().getMessage("general.type.command").getMessage());
        stack.setItemMeta(meta);
        return stack;
    }

    @Override
    public ItemStack process(Player player) {
        String parsed = this.command;
        parsed = parsed.replace("{player}", player.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsed);
        return null;
    }
}
