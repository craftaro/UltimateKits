package com.songoda.ultimatekits.kit.type;

import com.songoda.arconix.api.methods.formatting.TextComponent;
import com.songoda.ultimatekits.Lang;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class KitContentCommand implements KitContent {

    private String command; // Stored like "eco give <player> 100"

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
            lore.add(TextComponent.formatText("&a" + (index == 0 ? "/" : "") + "&a" + command.substring(index, Math.min(index + 30, command.length()))));
            index += 30;
        }
        meta.setLore(lore);
        meta.setDisplayName(Lang.COMMAND.getConfigValue());
        stack.setItemMeta(meta);
        return stack;
    }
}
