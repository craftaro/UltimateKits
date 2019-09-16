package com.songoda.ultimatekits.gui;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.KitBlockData;
import com.songoda.ultimatekits.kit.KitType;
import com.songoda.ultimatekits.settings.Settings;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class BlockEditorGui extends Gui {

    private final KitBlockData kitBlockData;
    static final Random rand = new Random();

    public BlockEditorGui(UltimateKits plugin, KitBlockData kitBlockData) {
        this.kitBlockData = kitBlockData;
        setRows(3);
        setTitle(plugin.getLocale().getMessage("interface.kitblock.title")
                .processPlaceholder("kit", kitBlockData.getKit().getShowableName())
                .getMessage());

        // fill center with glass
        if (Settings.RAINBOW.getBoolean()) {
            for (int col = 3; col < 6; ++col) {
                for (int row = 0; row < rows; ++row) {
                    setItem(row, col, GuiUtils.getBorderItem(CompatibleMaterial.getGlassPaneColor(rand.nextInt(16))));
                }
            }
        } else {
            ItemStack topBottom = GuiUtils.getBorderItem(Settings.GLASS_TYPE_2.getMaterial(CompatibleMaterial.GRAY_STAINED_GLASS_PANE));
            for (int col = 3; col < 6; ++col) {
                for (int row = 0; row < rows; ++row) {
                    setItem(row, col, topBottom);
                }
            }
        }

        // decorate the edges
        ItemStack glass2 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_2.getMaterial(CompatibleMaterial.BLUE_STAINED_GLASS_PANE));
        ItemStack glass3 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_3.getMaterial(CompatibleMaterial.LIGHT_BLUE_STAINED_GLASS_PANE));

        // edges will be type 3
        setDefaultItem(glass3);

        // decorate corners with type 2
        GuiUtils.mirrorFill(this, 0, 0, true, true, glass2);
        GuiUtils.mirrorFill(this, 1, 0, true, true, glass2);
        GuiUtils.mirrorFill(this, 0, 1, true, true, glass2);

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
                plugin.getLocale().getMessage("interface.kitblock.decorlore").getMessage().split("|")),
                ClickType.LEFT,
                event -> event.manager.showGUI(event.player, new DecorOptionsGui(plugin, kitBlockData, this)));

        // edit
        setButton(1, 6, GuiUtils.createButtonItem(CompatibleMaterial.DIAMOND_PICKAXE,
                plugin.getLocale().getMessage("interface.kitblock.edit").getMessage(),
                plugin.getLocale().getMessage("interface.kitblock.editlore").getMessage().split("|")),
                ClickType.LEFT,
                event -> {
                    new GUIKitEditor(UltimateKits.getInstance(), player, kitBlockData.getKit(), this, null, 0);
                });

    }

    List<String> kitTypeLore(UltimateKits plugin) {
        String[] type = plugin.getLocale().getMessage("interface.kitblock.switchtypelore").getMessage().split("|");
        return Arrays.asList(
                type[0],
                (kitBlockData.getType() == KitType.PREVIEW ? ChatColor.GOLD : ChatColor.GRAY) + (type.length > 1 ? type[1] : "Preview"),
                (kitBlockData.getType() == KitType.CRATE ? ChatColor.GOLD : ChatColor.GRAY) + (type.length > 2 ? type[2] : "Crate"),
                (kitBlockData.getType() == KitType.CLAIM ? ChatColor.GOLD : ChatColor.GRAY) + (type.length > 3 ? type[3] : "Claim"));
    }

}
