package com.songoda.ultimatekits.kit;

import com.songoda.ultimatekits.Lang;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.type.KitContent;
import com.songoda.ultimatekits.kit.type.KitContentCommand;
import com.songoda.ultimatekits.kit.type.KitContentEconomy;
import com.songoda.ultimatekits.kit.type.KitContentItem;
import com.songoda.ultimatekits.utils.Methods;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;

public class KitItem {

    private final KitContent content;
    private String displayName, displayLore = null;
    private Material displayItem = null;
    private int chance = 0;

    public KitItem(String line) {
        if (line.contains(";")) {
            line = translateLine(line);
        }
        if (line.startsWith(UltimateKits.getInstance().getConfig().getString("Main.Currency Symbol"))) {
            this.content = new KitContentEconomy(Double.parseDouble(line.substring(1).trim()));
        } else if (line.startsWith("/")) {
            this.content = new KitContentCommand(line.substring(1));
        } else {
            this.content = new KitContentItem(UltimateKits.getInstance().getItemSerializer().deserializeLegacyItemStack(line));
        }
    }

    public KitItem(ItemStack item, String line) {
        ItemStack itemStack = item.clone();
        ItemMeta meta = itemStack.getItemMeta();
        if (itemStack.hasItemMeta() && meta.hasDisplayName() && meta.getDisplayName().contains(";")) {
            translateLine(meta.getDisplayName());
        }
        if (line.startsWith(UltimateKits.getInstance().getConfig().getString("Main.Currency Symbol"))) {
            this.content = new KitContentEconomy(Double.parseDouble(line.substring(1).trim()));
        } else if (line.startsWith("/")) {
            this.content = new KitContentCommand(line.substring(1));
        } else {
            this.content = new KitContentItem(UltimateKits.getInstance().getItemSerializer().deserializeLegacyItemStack(line));
        }
    }

    public KitItem(ItemStack item) {
        ItemStack itemStack = item.clone();
        ItemMeta meta = itemStack.getItemMeta();
        if (itemStack.hasItemMeta() && meta.hasDisplayName() && meta.getDisplayName().contains(";")) {
            translateLine(meta.getDisplayName());
            String[] split = meta.getDisplayName().replace(String.valueOf(ChatColor.COLOR_CHAR), "").split(";", 2);
            meta.setDisplayName(split[1].contains("faqe") ? null : meta.getDisplayName().split(";", 2)[1]);
            itemStack.setItemMeta(meta);
        }
        String name = itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName() ? meta.getDisplayName() : "";

        if (name.startsWith(UltimateKits.getInstance().getConfig().getString("Main.Currency Symbol"))) {
            this.content = new KitContentEconomy(Double.parseDouble(name.substring(1).trim()));
        } else if (name.startsWith("/")) {
            this.content = new KitContentCommand(name.substring(1));
        } else {
            this.content = new KitContentItem(itemStack);
        }
    }

    private String translateLine(String line) {
        String[] lineSplit = line.trim().split(";", 2);
        String[] kitOptions = lineSplit[0].replace(String.valueOf(ChatColor.COLOR_CHAR), "").split(" ");

        for (String s : kitOptions) {
            if (s.equals("")) continue;
            String[] sSplit = s.split(":", 2);
            if (sSplit.length != 2) return line;
            String option = sSplit[0].toLowerCase();
            String value = sSplit[1].trim();

            switch (option) {
                case "chance":
                    chance = Integer.parseInt(value);
                    break;
                case "display-item":
                    displayItem = Material.valueOf(value);
                    break;
                case "display-lore":
                    displayLore = value.replace("_", " ");
                    break;
                case "display-name":
                    displayName = value.replace("_", " ");
                    break;
            }
        }
        return lineSplit[1];
    }

    private String compileOptions() {
        String line = "";
        if (chance != 0) {
            line += "chance:" + chance;
        }
        if (displayItem != null) {
            line += " display-item:" + displayItem;
        }
        if (displayName != null) {
            line += " display-name:" + displayName;
        }
        if (displayLore != null) {
            line += " display-lore:" + displayLore;
        }
        line.trim();
        return line;
    }

    public KitContent getContent() {
        return content;
    }

    public String getSerialized() {
        if (chance == 0 && displayItem == null && displayName == null && displayLore == null)
            return this.content.getSerialized();
        return compileOptions() + ";" + this.content.getSerialized();
    }

    public int getChance() {
        return chance == 0 ? 100 : chance;
    }

    public void setChance(int chance) {
        this.chance = chance;
    }

    public Material getDisplayItem() {
        return displayItem;
    }

    public void setDisplayItem(Material displayItem) {
        this.displayItem = displayItem;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayLore() {
        return displayLore;
    }

    public void setDisplayLore(String displayLore) {
        this.displayLore = displayLore;
    }

    public ItemStack getItem() {
        return content.getItemForDisplay();
    }

    public ItemStack getMoveableItem() {
        ItemStack item = content.getItemForDisplay();
        ItemMeta meta = item.getItemMeta();
        if (chance != 0 || displayItem != null || displayName != null || displayLore != null) {
            meta.setDisplayName(meta.hasDisplayName() ? Methods.convertToInvisibleString(compileOptions() + ";") + meta.getDisplayName() : Methods.convertToInvisibleString(compileOptions() + ";faqe") + item.getType().name().replace("_", " "));
        }
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getItemForDisplay() {
        ItemStack item = content.getItemForDisplay();
        ItemMeta meta = item.getItemMeta();

        if (displayItem != null) {
            item.setType(displayItem);
        }
        if (displayName != null) {
            meta.setDisplayName(Methods.formatText(displayName));
        }
        if (displayLore != null) {
            meta.setLore(Arrays.asList(Methods.formatText(displayLore)));
        }

        if (UltimateKits.getInstance().getConfig().getBoolean("Main.Display Chance In Preview")) {
            ArrayDeque<String> lore;
            if (meta.hasLore()) {
                lore = new ArrayDeque<>(meta.getLore());
            } else {
                lore = new ArrayDeque<>();
            }

            if (lore.size() != 0) lore.addFirst("");
            lore.addFirst(Methods.formatText("&7" + Lang.CHANCE.getConfigValue() + ": &6" + (chance == 0 ? 100 : chance) + "%"));
            meta.setLore(new ArrayList<>(lore));
        }

        item.setItemMeta(meta);
        return item;
    }

    @Override
    public String toString() {
        return "KitItem:{"
                + "Item:\"" + content.getSerialized() + "\","
                + "Chance:" + chance + "\","
                + "Display Item:" + displayItem + "\","
                + "Display Name:" + displayName + "\","
                + "Display Lore:" + displayLore
                + "}";
    }

}
