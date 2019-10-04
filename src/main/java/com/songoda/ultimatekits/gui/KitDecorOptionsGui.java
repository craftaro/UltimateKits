package com.songoda.ultimatekits.gui;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.utils.ItemUtils;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.kit.KitBlockData;
import com.songoda.ultimatekits.settings.Settings;
import com.songoda.ultimatekits.utils.Methods;
import org.bukkit.event.inventory.ClickType;
import java.util.Random;

public class KitDecorOptionsGui extends Gui {

    static final Random rand = new Random();

    public KitDecorOptionsGui(UltimateKits plugin, KitBlockData kitBlockData, Gui parent) {
        super(parent);
        setRows(3);
        setTitle(plugin.getLocale().getMessage("interface.kitdecor.title")
                .processPlaceholder("kit", kitBlockData.getKit().getShowableName())
                .getMessage());

        Kit kit = kitBlockData.getKit();

        // fill glass borders
        Methods.fillGlass(this);

        // exit button
        setButton(0, 8, GuiUtils.createButtonItem(Settings.EXIT_ICON.getMaterial(CompatibleMaterial.OAK_DOOR),
                plugin.getLocale().getMessage("interface.button.exit").getMessage()),
                ClickType.LEFT,
                event -> exit());

        // back button
        setButton(0, 0, GuiUtils.createButtonItem(ItemUtils.getCustomHead("3ebf907494a935e955bfcadab81beafb90fb9be49c7026ba97d798d5f1a23"),
                plugin.getLocale().getMessage("interface.button.back").getMessage()),
                ClickType.LEFT,
                event -> event.player.closeInventory());

        final String enableLore = plugin.getLocale().getMessage("interface.kitdecor.settingon").getMessage();
        final String disableLore = plugin.getLocale().getMessage("interface.kitdecor.settingoff").getMessage();

        // Hologram
        setButton(1, 1, GuiUtils.createButtonItem(CompatibleMaterial.NAME_TAG,
                plugin.getLocale().getMessage("interface.kitdecor.hologram").getMessage(),
                kitBlockData.showHologram() ? enableLore : disableLore),
                event -> {
                    kitBlockData.setShowHologram(!kitBlockData.showHologram());
                    plugin.updateHologram(kitBlockData);
                    updateItemLore(event.slot, kitBlockData.showHologram() ? enableLore : disableLore);
                });

        // Particle effects
        setButton(1, 3, GuiUtils.createButtonItem(CompatibleMaterial.POTION,
                plugin.getLocale().getMessage("interface.kitdecor.particle").getMessage(),
                kitBlockData.hasParticles() ? enableLore : disableLore),
                event -> {
                    kitBlockData.setHasParticles(!kitBlockData.hasParticles());
                    updateItemLore(event.slot, kitBlockData.hasParticles() ? enableLore : disableLore);
                });

        // Item Display
        setButton(1, 5, GuiUtils.createButtonItem(CompatibleMaterial.DIAMOND,
                plugin.getLocale().getMessage("interface.kitdecor.display").getMessage(),
                kitBlockData.isDisplayingItems() ? enableLore : disableLore),
                event -> {
                    kitBlockData.setDisplayingItems(!kitBlockData.isDisplayingItems());
                    updateItemLore(event.slot, kitBlockData.isDisplayingItems() ? enableLore : disableLore);
                });

        // Item Display Override
        setButton(1, 7, GuiUtils.createButtonItem(kit.getDisplayItem() != null ? kit.getDisplayItem() : CompatibleMaterial.BEACON,
                plugin.getLocale().getMessage("interface.kitdecor.displayone").getMessage(),
                plugin.getLocale().getMessage("interface.kitdecor.displayonelore")
                .processPlaceholder("enabled", kitBlockData.isItemOverride() ? enableLore : disableLore)
                .getMessage().split("\\|")),
                event -> {
                    kitBlockData.setItemOverride(!kitBlockData.isItemOverride());
                    updateItemLore(event.slot, kitBlockData.isItemOverride() ? enableLore : disableLore);
                });

    }

}
