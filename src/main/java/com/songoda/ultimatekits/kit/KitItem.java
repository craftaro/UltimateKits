package com.songoda.ultimatekits.kit;

import com.songoda.core.utils.TextUtils;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.type.KitContent;
import com.songoda.ultimatekits.kit.type.KitContentCommand;
import com.songoda.ultimatekits.kit.type.KitContentEconomy;
import com.songoda.ultimatekits.kit.type.KitContentItem;
import com.songoda.ultimatekits.settings.Settings;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;

public class KitItem {

    private KitContent content;
    private String displayName, displayLore = null;
    private Material displayItem = null;
    private int chance = 0;

    public KitItem(String line) {
        if (line.contains(";") && !line.startsWith("{")) {
            line = translateLine(line);
        }
        processContent(line, null);
    }

    public KitItem(ItemStack item, String line) {
        ItemStack itemStack = item.clone();
        ItemMeta meta = itemStack.getItemMeta();
        if (itemStack.hasItemMeta() && meta.hasDisplayName() && meta.getDisplayName().contains(";")) {
            translateLine(meta.getDisplayName());
        }
        processContent(line, null);
    }

    public KitItem(ItemStack item) {
        ItemStack itemStack = item.clone();
        ItemMeta meta = itemStack.getItemMeta();
        if (itemStack.hasItemMeta() && meta.hasDisplayName() && meta.getDisplayName().contains(";")) {
            String translated = translateLine(meta.getDisplayName()).replace(TextUtils.convertToInvisibleString("faqe"), "");
            if (translated.equalsIgnoreCase(WordUtils.capitalize(item.getType().toString().toLowerCase().replace("_", " "))))
                meta.setDisplayName(null);
            else
                meta.setDisplayName(translated);
            itemStack.setItemMeta(meta);
        }
        processContent(null, itemStack);
    }

    private void processContent(String line, ItemStack item) {
        if (line != null && line.startsWith(Settings.CURRENCY_SYMBOL.getString())) {
            this.content = new KitContentEconomy(Double.parseDouble(line.substring(1).trim()));
        } else if (line != null && line.startsWith("/")) {
            this.content = new KitContentCommand(line.substring(1));
        } else {
            ItemStack itemStack = item == null ? UltimateKits.getInstance().getItemSerializer().deserializeItemStackFromJson(line) : item;
            this.content = itemStack != null ? new KitContentItem(itemStack) : null;
        }
    }

    private String translateLine(String line) {
        String[] lineSplit = line.trim().split(";", 2);
        String[] kitOptions = lineSplit[0].replace(String.valueOf(ChatColor.COLOR_CHAR), "").split("~");
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
                    displayLore = value;
                    break;
                case "display-name":
                    displayName = value;
                    break;
            }
        }
        return lineSplit[1];
    }

    private String compileOptions() {
        String line = "";
        if (chance != 0)
            line += "chance:" + chance;
        if (displayItem != null)
            line += "~display-item:" + displayItem;
        if (displayName != null)
            line += "~display-name:" + displayName;
        if (displayLore != null)
            line += "~display-lore:" + displayLore;
        return line.trim();
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
        if (content == null) return null;
        ItemStack item = content.getItemForDisplay();
        ItemMeta meta = item.getItemMeta();
        if (chance != 0 || displayItem != null || displayName != null || displayLore != null) {
            String capitalizedName = meta.hasDisplayName() ? meta.getDisplayName() :
                    WordUtils.capitalize(item.getType().toString().toLowerCase().replace("_", " "));
            if (capitalizedName.contains(TextUtils.convertToInvisibleString(";faqe")))
                capitalizedName = meta.getDisplayName().split(TextUtils.convertToInvisibleString(";faqe"))[1];
            meta.setDisplayName(TextUtils.convertToInvisibleString(compileOptions() + ";faqe") + capitalizedName);
        }
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getItemForDisplay() {
        if (content == null) return null;
        ItemStack item = content.getItemForDisplay();
        ItemMeta meta = item.getItemMeta();

        if (displayItem != null) {
            item.setType(displayItem);
        }
        if (meta != null) {
            if (displayName != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
            }
            if (displayLore != null) {
                meta.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', displayLore)));
            }

            if (UltimateKits.getInstance().getConfig().getBoolean("Main.Display Chance In Preview")) {
                ArrayDeque<String> lore;
                if (meta.hasLore()) {
                    lore = new ArrayDeque<>(meta.getLore());
                } else {
                    lore = new ArrayDeque<>();
                }

                if (!lore.isEmpty()) lore.addFirst("");
                lore.addFirst(ChatColor.GRAY.toString() + UltimateKits.getInstance().getLocale().getMessage("general.type.chance") + ": " + ChatColor.GOLD + (chance == 0 ? 100 : chance) + "%");
                meta.setLore(new ArrayList<>(lore));
            }

            item.setItemMeta(meta);
        }
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
