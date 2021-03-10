package com.songoda.ultimatekits.kit.type;

import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.utils.ItemSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class KitContentItem implements KitContent {

    private final ItemStack itemStack;

    private String serialized = null;

    public KitContentItem(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public String getSerialized() {
        if (serialized != null) return serialized;
        serialized = ItemSerializer.serializeItemStackToJson(itemStack);
        return serialized;
    }

    @Override
    public ItemStack getItemForDisplay() {
        return itemStack.clone();
    }

    @Override
    public ItemStack process(Player player) {
        ItemStack parseStack = itemStack;

        if (parseStack.hasItemMeta() && parseStack.getItemMeta().hasLore()) {
            ItemMeta meta = parseStack.getItemMeta();
            List<String> newLore = new ArrayList<>();
            for (String str : parseStack.getItemMeta().getLore()) {
                str = str.replace("{PLAYER}", player.getName()).replace("<PLAYER>", player.getName());
                newLore.add(str);
            }
            meta.setLore(newLore);
            parseStack.setItemMeta(meta);
        }
        return parseStack;
    }
}
