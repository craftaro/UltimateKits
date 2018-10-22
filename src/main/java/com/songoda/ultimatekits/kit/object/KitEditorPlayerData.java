package com.songoda.ultimatekits.kit.object;

import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class KitEditorPlayerData {

    private Kit kit;
    private EditorType editorType = EditorType.NOTIN;
    private ItemStack[] inventory = new ItemStack[0];
    private boolean showInventory = false;

    public Kit getKit() {
        return kit;
    }

    public void setKit(Kit kit) {
        this.kit = kit;
    }

    public EditorType getEditorType() {
        return editorType;
    }

    public void setEditorType(EditorType editorType) {
        this.editorType = editorType;
    }

    public boolean isInInventory() {
        return showInventory;
    }

    public void setInInventory(boolean showInventory) {
        this.showInventory = showInventory;
    }

    public ItemStack[] getInventory() {
        return Arrays.copyOf(inventory, inventory.length);
    }

    public void setInventory(ItemStack[] inventory) {
        this.inventory = inventory;
    }

    public enum EditorType {OVERVIEW, GENERAL, SELLING, GUI, COMMAND, MONEY, DELAY, TITLE, PRICE, LINK, NOTIN}
}
