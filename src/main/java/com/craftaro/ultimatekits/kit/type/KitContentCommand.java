package com.craftaro.ultimatekits.kit.type;

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
        return command;
    }

    @Override
    public String getSerialized() {
        return "/" + command;
    }

    @Override
    public ItemStack getItemForDisplay() {
        ItemStack stack = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = stack.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        int index = 0;
        while (index < command.length()) {
            lore.add(ChatColor.GREEN + (index == 0 ? "/" : "") + ChatColor.GREEN + command.substring(index, Math.min(index + 30, command.length())));
            index += 30;
        }
        meta.setLore(lore);
        meta.setDisplayName(UltimateKits.getInstance().getLocale().getMessage("general.type.command").getMessage());
        stack.setItemMeta(meta);
        return stack;
    }

    @Override
    public ItemStack process(Player player) {
        String parsed = command;
        parsed = parsed.replace("{player}", player.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsed);
        return null;
    }
}
