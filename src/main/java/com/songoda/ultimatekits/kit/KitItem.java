package com.songoda.ultimatekits.kit;

import com.songoda.arconix.api.methods.formatting.TextComponent;
import com.songoda.arconix.api.methods.math.AMath;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.type.KitContent;
import com.songoda.ultimatekits.kit.type.KitContentCommand;
import com.songoda.ultimatekits.kit.type.KitContentEconomy;
import com.songoda.ultimatekits.kit.type.KitContentItem;
import com.songoda.ultimatekits.utils.Methods;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitItem {

    private final KitContent content;
    private int chance = 0;

    public KitItem(String item) {
        String item2 = item.replace(String.valueOf(ChatColor.COLOR_CHAR), "");
        if (item2.substring(0, Math.min(item2.length(), 5)).contains(":")) {
            this.chance = Integer.parseInt(item2.split(":", 2)[0]);
            item = item.split(":", 2)[1].trim();
        }
        if (item.startsWith(UltimateKits.getInstance().getConfig().getString("Main.Currency Symbol"))) {
            this.content = new KitContentEconomy(Double.parseDouble(item.substring(1).trim()));
        } else if (item.startsWith("/")) {
            this.content = new KitContentCommand(item.substring(1));
        } else {
            this.content = new KitContentItem(Methods.deserializeItemStack(item));
        }
    }

    public KitItem(ItemStack item) {
        ItemStack itemStack = item.clone();
        ItemMeta meta = itemStack.getItemMeta();
        if (meta.getDisplayName().contains(":")) {
            String[] split = meta.getDisplayName().replace(String.valueOf(ChatColor.COLOR_CHAR), "").split(":", 2);
            if (AMath.isInt(split[0])) {
                this.chance = Integer.parseInt(split[0]);
                meta.setDisplayName(split[1].contains("aqf") ? null : meta.getDisplayName().split(":", 2)[1]);
                itemStack.setItemMeta(meta);
            }
        }
        String name = meta.hasDisplayName() ? meta.getDisplayName() : "";

        if (name.startsWith(UltimateKits.getInstance().getConfig().getString("Main.Currency Symbol"))) {
            this.content = new KitContentEconomy(Double.parseDouble(name.substring(1).trim()));
        } else if (name.startsWith("/")) {
            this.content = new KitContentCommand(name.substring(1));
        } else {
            this.content = new KitContentItem(itemStack);
        }
    }

    public KitContent getContent() {
        return content;
    }

    public String getSerialized() {
        if (chance == 0) return this.content.getSerialized();
        return chance + ":" + this.content.getSerialized();
    }

    public int getChance() {
        return chance == 0 ? 100 : chance;
    }

    public void setChance(int chance) {
        this.chance = chance;
    }

    public ItemStack getItem() {
        return content.getItemForDisplay();
    }

    public ItemStack getMoveableItem() {
        ItemStack item = content.getItemForDisplay();
        ItemMeta meta = item.getItemMeta();
        if (chance != 0) meta.setDisplayName(meta.hasDisplayName() ? TextComponent.convertToInvisibleString(chance + ":") + meta.getDisplayName() : TextComponent.convertToInvisibleString(chance + ":aqf") + item.getType().name().replace("_", " "));
        item.setItemMeta(meta);
        return item;
    }


    @Override
    public String toString() {
        return "KitItem:{"
                + "Item:\"" + content.getSerialized() + "\","
                + "Chance:" + chance
                + "}";
    }

}
