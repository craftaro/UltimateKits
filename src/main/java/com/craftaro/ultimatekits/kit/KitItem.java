package com.craftaro.ultimatekits.kit;

import com.craftaro.core.third_party.de.tr7zw.nbtapi.NBTItem;
import com.craftaro.core.utils.TextUtils;
import com.craftaro.ultimatekits.kit.type.KitContent;
import com.craftaro.ultimatekits.kit.type.KitContentCommand;
import com.craftaro.ultimatekits.kit.type.KitContentEconomy;
import com.craftaro.ultimatekits.UltimateKits;
import com.craftaro.ultimatekits.kit.type.KitContentItem;
import com.craftaro.ultimatekits.settings.Settings;
import com.craftaro.ultimatekits.utils.ItemSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KitItem implements Cloneable {

    private KitContent content;
    private KitItemType type;
    private String displayName, displayLore = null;
    private Material displayItem = null;
    private double chance = 0;

    public KitItem(String line) {
        if (line.contains(";") && !line.startsWith("{")) {
            line = translateLine(line);
        }
        processContent(line, null);
    }

    public KitItem(ItemStack item, String line) {
        translateTags(item);
        processContent(line, null);
    }

    public KitItem(ItemStack item) {
        translateTags(item);
        processContent(null, item.clone());
    }

    private void processContent(String line, ItemStack item) {
        if (line != null && line.startsWith(Settings.CURRENCY_SYMBOL.getString())) {
            this.content = new KitContentEconomy(Double.parseDouble(line.substring(1).trim()));
            this.type = KitItemType.ECONOMY;
        } else if (line != null && line.startsWith("/")) {
            this.content = new KitContentCommand(line.substring(1));
            this.type = KitItemType.COMMAND;
        } else {
            ItemStack itemStack = item == null ? ItemSerializer.deserializeItemStackFromJson(line) : item;
            this.content = itemStack != null ? new KitContentItem(itemStack) : null;
            this.type = KitItemType.ITEM;
        }
    }

    private void translateTags(ItemStack item) {
        if (item == null) return;
        NBTItem nbtItem = new NBTItem(item);
        if (nbtItem.hasKey("chance"))
            chance = nbtItem.getDouble("chance");
        if (nbtItem.hasKey("display-item"))
            displayItem = Material.valueOf(nbtItem.getString("display-item"));
        if (nbtItem.hasKey("display-name"))
            displayName = nbtItem.getString("display-name");
        if (nbtItem.hasKey("display-lore"))
            displayLore = nbtItem.getString("display-lore");
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
                    //chance = Integer.parseInt(value);
                    chance = Double.parseDouble(value);
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

    private ItemStack compileOptions(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        if (chance != 0)
            nbtItem.setDouble("chance", chance);
        if (displayItem != null)
            nbtItem.setString("display-item", displayItem.name());
        if (displayName != null)
            nbtItem.setString("display-name", displayName);
        if (displayLore != null)
            nbtItem.setString("display-lore", displayLore);
        return nbtItem.getItem();
    }

    private String compileOptionsText() {
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
        return compileOptionsText() + ";" + this.content.getSerialized();
    }

    public double getChance() {
        return chance == 0 ? 100 : chance;
    }

    public void setChance(double chance) {
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
        ItemStack item = content.getItemForDisplay().clone();
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.hasLore() && meta.getLore().get(0).equals(TextUtils.formatText("&8&oMoveable"))
                ? new ArrayList<>() : new ArrayList<>(Collections.singletonList(TextUtils.formatText("&8&oMoveable")));
        if (meta.hasLore())
            lore.addAll(meta.getLore());
        meta.setLore(lore);
        item.setItemMeta(meta);
        return compileOptions(item);
    }

    public ItemStack getItemForDisplay() {
        if (content == null) return null;
        ItemStack item = content.getItemForDisplay();
        ItemMeta meta = item.getItemMeta();

        if (displayItem != null) {
            item.setType(displayItem);
            meta = item.getItemMeta();
        }
        if (meta != null) {
            if (displayName != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
            }
            if (displayLore != null) {
                meta.setLore(Collections.singletonList(ChatColor.translateAlternateColorCodes('&', displayLore)));
            }

            if (UltimateKits.getInstance().getConfig().getBoolean("Main.Display Chance In Preview")) {
                ArrayDeque<String> lore;
                if (meta.hasLore())
                    lore = new ArrayDeque<>(meta.getLore());
                else
                    lore = new ArrayDeque<>();

                if (!lore.isEmpty()) lore.addFirst("");
                lore.addFirst(ChatColor.GRAY.toString() + UltimateKits.getInstance().getLocale().getMessage("general.type.chance").getMessage() + ": " + ChatColor.GOLD + (chance == 0 ? 100 : chance) + "%");
                meta.setLore(new ArrayList<>(lore));
            }

            item.setItemMeta(meta);
        }
        return item;
    }

    public KitItemType getType() {
        return type;
    }

    public KitItem clone() throws CloneNotSupportedException {
        return (KitItem) super.clone();
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
