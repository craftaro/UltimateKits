package com.songoda.ultimatekits.gui;

import com.songoda.ultimatekits.Lang;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.KitBlockData;
import com.songoda.ultimatekits.kit.KitType;
import com.songoda.ultimatekits.utils.Methods;
import com.songoda.ultimatekits.utils.gui.AbstractGUI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class GUIBlockEditor extends AbstractGUI {

    private KitBlockData kitBlockData;

    public GUIBlockEditor(UltimateKits plugin, Player player, Location location) {
        super(player);
        kitBlockData = plugin.getKitManager().getKit(location);

        init("&8This contains &a" + kitBlockData.getKit().getShowableName(), 27);
    }

    @Override
    protected void constructGUI() {
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
                Lang.EXIT.getConfigValue());

        ArrayList<String> lore = new ArrayList<>();
        lore.add(Methods.formatText("&7Click to swap this kit blocks function."));
        lore.add("");

        if (kitBlockData.getType() == KitType.PREVIEW) {
            lore.add(Methods.formatText("&6Preview"));
            lore.add(Methods.formatText("&7Crate"));
            lore.add(Methods.formatText("&7Claim"));
        } else if (kitBlockData.getType() == KitType.CRATE) {
            lore.add(Methods.formatText("&7Preview"));
            lore.add(Methods.formatText("&6Crate"));
            lore.add(Methods.formatText("&7Claim"));
        } else if (kitBlockData.getType() == KitType.CLAIM) {
            lore.add(Methods.formatText("&7Preview"));
            lore.add(Methods.formatText("&7Crate"));
            lore.add(Methods.formatText("&6Claim"));
        }

        createButton(11, Material.COMPARATOR, "&5&lSwitch kit type", lore);

        createButton(13, Material.POPPY, "&9&lDecor Options",
                "&7Click to edit the decoration",
                "&7options for this kit.");

        createButton(15, Material.DIAMOND_PICKAXE, "&a&lEdit kit",
                "&7Click to edit the kit",
                "&7contained in this block.");
    }

    @Override
    protected void registerClickables() {
        registerClickable(8, (player, inventory, cursor, slot, type) -> player.closeInventory());

        registerClickable(11, (player, inventory, cursor, slot, type) -> {
            UltimateKits instance = UltimateKits.getInstance();

            if (kitBlockData.getType() == KitType.PREVIEW) kitBlockData.setType(KitType.CRATE);
            else if (kitBlockData.getType() == KitType.CRATE) kitBlockData.setType(KitType.CLAIM);
            else if (kitBlockData.getType() == KitType.CLAIM) kitBlockData.setType(KitType.PREVIEW);

            instance.saveConfig();
            instance.getHologram().update(kitBlockData);
            constructGUI();
        });
        registerClickable(13, (player, inventory, cursor, slot, type) -> {
            new GUIDecorOptions(UltimateKits.getInstance(), player, kitBlockData.getLocation());
        });
        registerClickable(15, (player, inventory, cursor, slot, type) -> {
            new GUIKitEditor(UltimateKits.getInstance(), player, kitBlockData.getKit(), this, null, 0);
        });
    }

    @Override
    protected void registerOnCloses() {

    }

}
