package com.songoda.ultimatekits.gui;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.gui.AnvilGui;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.utils.ItemUtils;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.settings.Settings;
import com.songoda.ultimatekits.utils.Methods;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class KitGuiOptionsGui extends Gui {

    public KitGuiOptionsGui(UltimateKits plugin, Player player, Kit kit, Gui back) {
        super(back);
        setRows(3);
        setTitle(plugin.getLocale().getMessage("interface.kitblock.title")
                .processPlaceholder("kit", kit.getShowableName())
                .getMessage());
        setAcceptsItems(true); // display item takes an item

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

        // set hologram title
        setButton(1, 2, GuiUtils.createButtonItem(CompatibleMaterial.NAME_TAG,
                plugin.getLocale().getMessage("interface.kitguioptions.holo").getMessage(),
                plugin.getLocale().getMessage("interface.kitguioptions.hololore")
                .processPlaceholder("onoff",
                        kit.getLink() != null ? plugin.getLocale().getMessage("interface.kitguioptions.holoon").processPlaceholder("title", kit.getTitle()).getMessage()
                                : plugin.getLocale().getMessage("interface.kitguioptions.holooff").getMessage()
                ).getMessage().split("\\|")),
                ClickType.LEFT,
                event -> {
                    AnvilGui gui = new AnvilGui(event.player, this);
                    gui.setTitle(plugin.getLocale().getMessage("interface.kitguioptions.holoprompt").getMessage());
                    gui.setAction(aevent -> {
                        final String msg = gui.getInputText();
                        kit.setTitle(msg);
                        plugin.getLocale().getMessage("interface.kitguioptions.holoset")
                        .processPlaceholder("title", msg)
                        .processPlaceholder("kit", kit.getShowableName())
                        .sendPrefixedMessage(player);

                        plugin.updateHologram(kit);

                        updateItemLore(event.slot, plugin.getLocale().getMessage("interface.kitguioptions.hololore")
                                .processPlaceholder("onoff",
                                        kit.getLink() != null ? plugin.getLocale().getMessage("interface.kitguioptions.holoon").processPlaceholder("title", kit.getTitle()).getMessage()
                                                : plugin.getLocale().getMessage("interface.kitguioptions.holooff").getMessage()
                                ).getMessage().split("\\|"));

                        aevent.player.closeInventory();
                    });
                    guiManager.showGUI(event.player, gui);
                });
        setAction(1, 2, ClickType.RIGHT, event -> {
            kit.setTitle("");
            plugin.updateHologram(kit);

            updateItemLore(event.slot, plugin.getLocale().getMessage("interface.kitguioptions.hololore")
                    .processPlaceholder("onoff",
                            kit.getLink() != null ? plugin.getLocale().getMessage("interface.kitguioptions.holoon").processPlaceholder("title", kit.getTitle()).getMessage()
                                    : plugin.getLocale().getMessage("interface.kitguioptions.holooff").getMessage()
                    ).getMessage().split("\\|"));
        });

        setButton(1, 4, GuiUtils.createButtonItem(kit.getDisplayItem() != null ? kit.getDisplayItem() : CompatibleMaterial.BEACON,
                plugin.getLocale().getMessage("interface.kitguioptions.item").getMessage(),
                plugin.getLocale().getMessage("interface.kitguioptions.itemlore")
                .processPlaceholder("onoff",
                        kit.getLink() != null ? plugin.getLocale().getMessage("interface.kitguioptions.itemon").processPlaceholder("item", kit.getDisplayItem().toString()).getMessage()
                                : plugin.getLocale().getMessage("interface.kitguioptions.itemoff").getMessage()
                ).getMessage().split("\\|")),
                ClickType.LEFT,
                event -> {
                    ItemStack is = player.getItemInHand();
                    if (is.getType() == Material.AIR) {
                        plugin.getLocale().newMessage("interface.kitguioptions.itemnoitem").sendPrefixedMessage(player);
                    } else {
                        kit.setDisplayItem(is);
                        plugin.getLocale().newMessage("interface.kitguioptions.itemset").processPlaceholder("item", kit.getShowableName()).sendPrefixedMessage(player);
                        updateItem(event.slot, kit.getDisplayItem() != null ? kit.getDisplayItem() : CompatibleMaterial.BEACON,
                                plugin.getLocale().getMessage("interface.kitguioptions.item").getMessage(),
                                plugin.getLocale().getMessage("interface.kitguioptions.itemlore")
                                .processPlaceholder("onoff",
                                        kit.getLink() != null ? plugin.getLocale().getMessage("interface.kitguioptions.itemon").processPlaceholder("item", kit.getDisplayItem().toString()).getMessage()
                                                : plugin.getLocale().getMessage("interface.kitguioptions.itemoff").getMessage()
                                ).getMessage().split("\\|"));
                    }
                });
        setAction(1, 4, ClickType.RIGHT, event -> {
            kit.setDisplayItem(null);
            plugin.getLocale().newMessage("interface.kitguioptions.itemremoved").processPlaceholder("kit", kit.getShowableName()).sendPrefixedMessage(player);
            updateItem(event.slot, kit.getDisplayItem() != null ? kit.getDisplayItem() : CompatibleMaterial.BEACON,
                    plugin.getLocale().getMessage("interface.kitguioptions.item").getMessage(),
                    plugin.getLocale().getMessage("interface.kitguioptions.itemlore")
                    .processPlaceholder("onoff",
                            kit.getLink() != null ? plugin.getLocale().getMessage("interface.kitguioptions.itemon").processPlaceholder("item", kit.getDisplayItem().toString()).getMessage()
                                    : plugin.getLocale().getMessage("interface.kitguioptions.itemoff").getMessage()
                    ).getMessage().split("\\|"));
        });

        setButton(1, 4, GuiUtils.createButtonItem(kit.getDisplayItem() != null ? kit.getDisplayItem() : CompatibleMaterial.BEACON,
                plugin.getLocale().getMessage("interface.kitguioptions.hide").getMessage(),
                plugin.getLocale().getMessage("interface.kitguioptions.hidelore")
                .processPlaceholder("onoff", plugin.getLocale().getMessage(
                                kit.isHidden() ? "interface.kitguioptions.hideon" : "interface.kitguioptions.hideoff").getMessage()
                ).getMessage().split("\\|")),
                ClickType.LEFT,
                event -> {
                    kit.setHidden(!kit.isHidden());

                    updateItemLore(event.slot, plugin.getLocale().getMessage("interface.kitguioptions.hidelore")
                            .processPlaceholder("onoff", plugin.getLocale().getMessage(
                                            kit.isHidden() ? "interface.kitguioptions.hideon" : "interface.kitguioptions.hideoff").getMessage()
                            ).getMessage().split("\\|"));
                });
    }
}
