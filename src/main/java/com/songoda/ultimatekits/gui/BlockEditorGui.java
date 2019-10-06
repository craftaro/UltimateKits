package com.songoda.ultimatekits.gui;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.KitBlockData;
import com.songoda.ultimatekits.kit.KitType;
import com.songoda.ultimatekits.settings.Settings;
import com.songoda.ultimatekits.utils.Methods;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.ClickType;
import java.util.Arrays;
import java.util.List;

public class BlockEditorGui extends Gui {

    private final KitBlockData kitBlockData;

    public BlockEditorGui(UltimateKits plugin, KitBlockData kitBlockData) {
        this.kitBlockData = kitBlockData;
        setRows(3);
        setTitle(plugin.getLocale().getMessage("interface.kitblock.title")
                .processPlaceholder("kit", kitBlockData.getKit().getShowableName())
                .getMessage());

        // fill glass borders
        Methods.fillGlass(this);

        // exit button
        setButton(0, 8, GuiUtils.createButtonItem(Settings.EXIT_ICON.getMaterial(CompatibleMaterial.OAK_DOOR),
                plugin.getLocale().getMessage("interface.button.exit").getMessage()),
                ClickType.LEFT,
                event -> event.player.closeInventory());

        // kit type
        setButton(1, 2, GuiUtils.createButtonItem(CompatibleMaterial.COMPARATOR,
                plugin.getLocale().getMessage("interface.kitblock.switchtype").getMessage(),
                kitTypeLore(plugin)),
                ClickType.LEFT,
                event -> {
                    plugin.removeHologram(kitBlockData);

                    if (kitBlockData.getType() == KitType.PREVIEW) {
                        kitBlockData.setType(KitType.CRATE);
                    } else if (kitBlockData.getType() == KitType.CRATE) {
                        kitBlockData.setType(KitType.CLAIM);
                    } else if (kitBlockData.getType() == KitType.CLAIM) {
                        kitBlockData.setType(KitType.PREVIEW);
                    }

                    plugin.updateHologram(kitBlockData);
                    updateItemLore(event.slot, kitTypeLore(plugin));

                    plugin.getKitFile().delaySave();
                });

        // decor options
        setButton(1, 4, GuiUtils.createButtonItem(CompatibleMaterial.POPPY,
                plugin.getLocale().getMessage("interface.kitblock.decor").getMessage(),
                plugin.getLocale().getMessage("interface.kitblock.decorlore").getMessage().split("\\|")),
                ClickType.LEFT,
                event -> event.manager.showGUI(event.player, new KitDecorOptionsGui(plugin, kitBlockData, this)));

        // edit
        setButton(1, 6, GuiUtils.createButtonItem(CompatibleMaterial.DIAMOND_PICKAXE,
                plugin.getLocale().getMessage("interface.kitblock.edit").getMessage(),
                plugin.getLocale().getMessage("interface.kitblock.editlore").getMessage().split("\\|")),
                ClickType.LEFT,
                event -> {
                    guiManager.showGUI(event.player, new KitEditorGui(UltimateKits.getInstance(), event.player, kitBlockData.getKit(), this));
                });

    }

    List<String> kitTypeLore(UltimateKits plugin) {
        String[] type = plugin.getLocale().getMessage("interface.kitblock.switchtypelore").getMessage().split("\\|");
        return Arrays.asList(
                type[0],
                (kitBlockData.getType() == KitType.PREVIEW ? ChatColor.GOLD : ChatColor.GRAY) + (type.length > 1 ? type[1] : "Preview"),
                (kitBlockData.getType() == KitType.CRATE ? ChatColor.GOLD : ChatColor.GRAY) + (type.length > 2 ? type[2] : "Crate"),
                (kitBlockData.getType() == KitType.CLAIM ? ChatColor.GOLD : ChatColor.GRAY) + (type.length > 3 ? type[3] : "Claim"));
    }

}
