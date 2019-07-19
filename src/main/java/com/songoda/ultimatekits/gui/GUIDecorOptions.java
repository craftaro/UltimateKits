package com.songoda.ultimatekits.gui;

import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.kit.KitBlockData;
import com.songoda.ultimatekits.utils.Methods;
import com.songoda.ultimatekits.utils.ServerVersion;
import com.songoda.ultimatekits.utils.gui.AbstractGUI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class GUIDecorOptions extends AbstractGUI {

    private UltimateKits plugin;

    private KitBlockData kitBlockData;

    public GUIDecorOptions(UltimateKits plugin, Player player, Location location) {
        super(player);
        this.plugin = plugin;
        kitBlockData = plugin.getKitManager().getKit(location);
        init("&8Editing decor for &a" + kitBlockData.getKit().getShowableName() + "&8.", 27);
    }

    @Override
    protected void constructGUI() {
        Kit kit = kitBlockData.getKit();

        Methods.fillGlass(inventory);

        inventory.setItem(0, Methods.getBackgroundGlass(true));
        inventory.setItem(1, Methods.getBackgroundGlass(true));
        inventory.setItem(2, Methods.getBackgroundGlass(false));
        inventory.setItem(6, Methods.getBackgroundGlass(false));
        inventory.setItem(7, Methods.getBackgroundGlass(true));
        inventory.setItem(8, Methods.getBackgroundGlass(true));
        inventory.setItem(9, Methods.getBackgroundGlass(true));
        inventory.setItem(10, Methods.getBackgroundGlass(false));
        inventory.setItem(16, Methods.getBackgroundGlass(false));
        inventory.setItem(17, Methods.getBackgroundGlass(true));
        inventory.setItem(18, Methods.getBackgroundGlass(true));
        inventory.setItem(19, Methods.getBackgroundGlass(true));
        inventory.setItem(20, Methods.getBackgroundGlass(false));
        inventory.setItem(24, Methods.getBackgroundGlass(false));
        inventory.setItem(25, Methods.getBackgroundGlass(true));
        inventory.setItem(26, Methods.getBackgroundGlass(true));

        createButton(8, Material.valueOf(UltimateKits.getInstance().getConfig().getString("Interfaces.Exit Icon")),
                UltimateKits.getInstance().getLocale().getMessage("interface.button.exit").getMessage());

        ItemStack head = new ItemStack(plugin.isServerVersionAtLeast(ServerVersion.V1_13) ? Material.PLAYER_HEAD : Material.valueOf("SKULL_ITEM"), 1, (byte) 3);
        ItemStack back = Methods.addTexture(head, "http://textures.minecraft.net/texture/3ebf907494a935e955bfcadab81beafb90fb9be49c7026ba97d798d5f1a23");
        inventory.setItem(0, back);

        createButton(0, back, UltimateKits.getInstance().getLocale().getMessage("interface.button.back").getMessage());

        ArrayList<String> lore = new ArrayList<>();
        if (kitBlockData.showHologram()) {
            lore.add(Methods.formatText("&7Currently: &aEnabled&7."));
        } else {
            lore.add(Methods.formatText("&7Currently &cDisabled&7."));
        }

        createButton(10, Material.NAME_TAG, "&9&lToggle Holograms", lore);

        lore = new ArrayList<>();
        if (kitBlockData.hasParticles()) {
            lore.add(Methods.formatText("&7Currently: &aEnabled&7."));
        } else {
            lore.add(Methods.formatText("&7Currently &cDisabled&7."));
        }

        createButton(12, Material.POTION, "&9&lToggle Particles", lore);

        lore = new ArrayList<>();
        if (kitBlockData.isDisplayingItems()) {
            lore.add(Methods.formatText("&7Currently: &aEnabled&7."));
        } else {
            lore.add(Methods.formatText("&7Currently &cDisabled&7."));
        }

        createButton(14, Material.DIAMOND, "&9&lToggle DisplayItems", lore);

        Material material = Material.BEACON;
        if (kit.getDisplayItem() != null) {
            material = kit.getDisplayItem();
        }

        lore = new ArrayList<>();
        if (kitBlockData.isItemOverride()) {
            lore.add(Methods.formatText("&7Currently: &aEnabled&7."));
        } else {
            lore.add(Methods.formatText("&7Currently &cDisabled&7."));
        }
        lore.add("");
        lore.add(Methods.formatText("&7Enabling this option will "));
        lore.add(Methods.formatText("&7override the DisplayItems"));
        lore.add(Methods.formatText("&7above your kit to the single"));
        lore.add(Methods.formatText("&7DisplayItem set in this kit"));
        lore.add(Methods.formatText("&7GUI options."));

        createButton(16, material, "&9&lToggle DisplayItem Override", lore);
    }

    @Override
    protected void registerClickables() {
        registerClickable(0, (player, inventory, cursor, slot, type) -> new GUIBlockEditor(UltimateKits.getInstance(), player, kitBlockData.getLocation()));
        registerClickable(8, (player, inventory, cursor, slot, type) -> player.closeInventory());

        registerClickable(10, (player, inventory, cursor, slot, type) -> {
            if (plugin.getHologram() == null) return;

            if (kitBlockData.showHologram()) {
                kitBlockData.setShowHologram(false);
            } else {
                kitBlockData.setShowHologram(true);
            }
            UltimateKits.getInstance().getHologram().update(kitBlockData);
            constructGUI();
        });

        registerClickable(12, (player, inventory, cursor, slot, type) -> {
            if (kitBlockData.hasParticles()) {
                kitBlockData.setHasParticles(false);
            } else {
                kitBlockData.setHasParticles(true);
            }
            constructGUI();
        });

        registerClickable(14, (player, inventory, cursor, slot, type) -> {
            boolean isHolo = plugin.getHologram() != null && kitBlockData.showHologram();

            if (isHolo) {
                UltimateKits.getInstance().getHologram().remove(kitBlockData);
            }
            if (kitBlockData.isDisplayingItems()) {
                kitBlockData.setDisplayingItems(false);
            } else {
                kitBlockData.setDisplayingItems(true);
            }
            if (isHolo) {
                UltimateKits.getInstance().getHologram().add(kitBlockData);
            }
            constructGUI();
        });

        registerClickable(16, (player, inventory, cursor, slot, type) -> {
            if (kitBlockData.isItemOverride()) {
                kitBlockData.setItemOverride(false);
            } else {
                kitBlockData.setItemOverride(true);
            }
            constructGUI();
        });
    }

    @Override
    protected void registerOnCloses() {

    }

}
