package com.songoda.ultimatekits.editor;

import com.songoda.ultimatekits.kit.Kit;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class KitEditorPlayerData {

    private Kit kit;
    private EditorType editorType = EditorType.NOTIN;
    private ItemStack[] inventory = new ItemStack[0];
    private boolean showInventory = false;
    private boolean showFuctions = false;
    private boolean muteSave = false;

    private int toReplaceSlot = 0;
    private ItemStack toReplace = null;

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

    public boolean isMuteSave() {
        return muteSave;
    }

    public void setMuteSave(boolean muteSave) {
        this.muteSave = muteSave;
    }

    public boolean isInInventory() {
        return showInventory;
    }

    public void setInInventory(boolean showInventory) {
        this.showInventory = showInventory;
    }

    public boolean isInFuction() {
        return showFuctions;
    }

    public void setInFunction(boolean showFuctions) {
        this.showFuctions = showFuctions;
    }

    public ItemStack[] getInventory() {
        return Arrays.copyOf(inventory, inventory.length);
    }

    public void setInventory(ItemStack[] inventory) {
        this.inventory = inventory;
    }

    public int getToReplaceSlot() {
        return toReplaceSlot;
    }

    public void setToReplaceSlot(int toReplaceSlot) {
        this.toReplaceSlot = toReplaceSlot;
    }

    public ItemStack getToReplace() {
        return toReplace;
    }

    public void setToReplace(ItemStack toReplace) {
        this.toReplace = toReplace;
    }

    public enum EditorType {OVERVIEW, GENERAL, SELLING, GUI, COMMAND, MONEY, DELAY, TITLE, PRICE, LINK, NOTIN, DISPLAY_ITEM, DISPLAY_NAME, DISPLAY_LORE}
}
