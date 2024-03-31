package com.craftaro.ultimatekits.kit;

import com.craftaro.core.third_party.de.tr7zw.nbtapi.NBTItem;
import com.craftaro.core.utils.TextUtils;
import com.craftaro.ultimatekits.UltimateKits;
import com.craftaro.ultimatekits.kit.type.KitContent;
import com.craftaro.ultimatekits.kit.type.KitContentCommand;
import com.craftaro.ultimatekits.kit.type.KitContentEconomy;
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
        if (item == null) {
            return;
        }
        NBTItem nbtItem = new NBTItem(item);
        if (nbtItem.hasKey("chance")) {
            this.chance = nbtItem.getDouble("chance");
        }
        if (nbtItem.hasKey("display-item")) {
            this.displayItem = Material.valueOf(nbtItem.getString("display-item"));
        }
        if (nbtItem.hasKey("display-name")) {
            this.displayName = nbtItem.getString("display-name");
        }
        if (nbtItem.hasKey("display-lore")) {
            this.displayLore = nbtItem.getString("display-lore");
        }
    }

    private String translateLine(String line) {
        String[] lineSplit = line.trim().split(";", 2);
        String[] kitOptions = lineSplit[0].replace(String.valueOf(ChatColor.COLOR_CHAR), "").split("~");
        for (String s : kitOptions) {
            if (s.isEmpty()) {
                continue;
            }
            String[] sSplit = s.split(":", 2);
            if (sSplit.length != 2) {
                return line;
            }
            String option = sSplit[0].toLowerCase();
            String value = sSplit[1].trim();

            switch (option) {
                case "chance":
                    //chance = Integer.parseInt(value);
                    this.chance = Double.parseDouble(value);
                    break;
                case "display-item":
                    this.displayItem = Material.valueOf(value);
                    break;
                case "display-lore":
                    this.displayLore = value;
                    break;
                case "display-name":
                    this.displayName = value;
                    break;
            }
        }
        return lineSplit[1];
    }

    private ItemStack compileOptions(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        if (this.chance != 0) {
            nbtItem.setDouble("chance", this.chance);
        }
        if (this.displayItem != null) {
            nbtItem.setString("display-item", this.displayItem.name());
        }
        if (this.displayName != null) {
            nbtItem.setString("display-name", this.displayName);
        }
        if (this.displayLore != null) {
            nbtItem.setString("display-lore", this.displayLore);
        }
        return nbtItem.getItem();
    }

    private String compileOptionsText() {
        String line = "";
        if (this.chance != 0) {
            line += "chance:" + this.chance;
        }
        if (this.displayItem != null) {
            line += "~display-item:" + this.displayItem;
        }
        if (this.displayName != null) {
            line += "~display-name:" + this.displayName;
        }
        if (this.displayLore != null) {
            line += "~display-lore:" + this.displayLore;
        }
        return line.trim();
    }

    public KitContent getContent() {
        return this.content;
    }

    public String getSerialized() {
        if (this.chance == 0 && this.displayItem == null && this.displayName == null && this.displayLore == null) {
            return this.content.getSerialized();
        }
        return compileOptionsText() + ";" + this.content.getSerialized();
    }

    public double getChance() {
        return this.chance == 0 ? 100 : this.chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public Material getDisplayItem() {
        return this.displayItem;
    }

    public void setDisplayItem(Material displayItem) {
        this.displayItem = displayItem;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayLore() {
        return this.displayLore;
    }

    public void setDisplayLore(String displayLore) {
        this.displayLore = displayLore;
    }

    public ItemStack getItem() {
        return this.content.getItemForDisplay();
    }

    public ItemStack getMoveableItem() {
        if (this.content == null) {
            return null;
        }
        ItemStack item = this.content.getItemForDisplay().clone();
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setBoolean("moveable", true);
        return compileOptions(nbtItem.getItem());
    }

    public ItemStack getItemForDisplay(Kit kit) {
        if (this.content == null) {
            return null;
        }
        ItemStack item = this.content.getItemForDisplay();
        ItemMeta meta = item.getItemMeta();

        if (this.displayItem != null) {
            item.setType(this.displayItem);
            meta = item.getItemMeta();
        }
        if (meta != null) {
            if (this.displayName != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.displayName));
            }
            if (this.displayLore != null) {
                meta.setLore(Collections.singletonList(ChatColor.translateAlternateColorCodes('&', this.displayLore)));
            }

            if (UltimateKits.getInstance().getConfig().getBoolean("Main.Display Chance In Preview") && !kit.all100Percent()) {
                ArrayDeque<String> lore;
                if (meta.hasLore()) {
                    lore = new ArrayDeque<>(meta.getLore());
                } else {
                    lore = new ArrayDeque<>();
                }

                if (!lore.isEmpty()) {
                    lore.addFirst("");
                }
                lore.addFirst(ChatColor.GRAY + UltimateKits.getInstance().getLocale().getMessage("general.type.chance").getMessage() + ": " + ChatColor.GOLD + (this.chance == 0 ? 100 : this.chance) + "%");
                meta.setLore(new ArrayList<>(lore));
            }

            item.setItemMeta(meta);
        }
        return item;
    }

    public KitItemType getType() {
        return this.type;
    }

    public KitItem clone() throws CloneNotSupportedException {
        return (KitItem) super.clone();
    }

    @Override
    public String toString() {
        return "KitItem:{"
                + "Item:\"" + this.content.getSerialized() + "\","
                + "Chance:" + this.chance + "\","
                + "Display Item:" + this.displayItem + "\","
                + "Display Name:" + this.displayName + "\","
                + "Display Lore:" + this.displayLore
                + "}";
    }
}
