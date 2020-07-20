package com.songoda.ultimatekits.gui;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.gui.AnvilGui;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.utils.ItemUtils;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.category.Category;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.settings.Settings;
import com.songoda.ultimatekits.utils.Methods;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class KitGeneralOptionsGui extends Gui {

    public KitGeneralOptionsGui(UltimateKits plugin, Player player, Kit kit, Gui back) {
        super(back);
        setRows(3);
        setTitle(plugin.getLocale().getMessage("interface.kitoptions.title")
                .processPlaceholder("kit", kit.getName())
                .getMessage());

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

        // edit delay
        setButton(1, 2, GuiUtils.createButtonItem(CompatibleMaterial.CLOCK,
                plugin.getLocale().getMessage("interface.kitoptions.delay").getMessage(),
                plugin.getLocale().getMessage("interface.kitoptions.delaylore")
                        .processPlaceholder("delay", kit.getDelay()).getMessage().split("\\|")),
                event -> {
                    AnvilGui gui = new AnvilGui(event.player, this);
                    gui.setTitle(plugin.getLocale().getMessage("interface.kitoptions.delayprompt").getMessage());
                    gui.setAction(aevent -> {
                        final String msg = gui.getInputText().trim();
                        try {
                            kit.setDelay(Integer.parseInt(msg));
                            updateItemLore(event.slot, plugin.getLocale().getMessage("interface.kitoptions.delaylore")
                                    .processPlaceholder("delay", kit.getDelay()).getMessage().split("\\|"));
                            aevent.player.closeInventory();
                            return;
                        } catch (NumberFormatException e) {
                        }
                        plugin.getLocale().getMessage("interface.kitoptions.delaynonumber").processPlaceholder("input", msg).sendPrefixedMessage(player);
                    });
                    guiManager.showGUI(event.player, gui);
                });

        // edit category
        setButton(1, 4, GuiUtils.createButtonItem(CompatibleMaterial.BOOK,
                plugin.getLocale().getMessage("interface.kitoptions.category").getMessage(),
                plugin.getLocale().getMessage("interface.kitoptions.categorylore")
                        .processPlaceholder("category", kit.getCategory() == null ? "none" : kit.getCategory().getName()).getMessage().split("\\|")),
                event -> {
                    if (event.clickType == ClickType.LEFT) {
                        AnvilGui gui = new AnvilGui(event.player, this);
                        gui.setTitle(plugin.getLocale().getMessage("interface.kitoptions.categoryprompt").getMessage());
                        gui.setAction(aevent -> {
                            final String msg = gui.getInputText().trim();
                            Category category = plugin.getCategoryManager().getCategoryByName(msg);
                            if (category != null) {
                                kit.setCategory(category);
                                updateItemLore(event.slot, plugin.getLocale().getMessage("interface.kitoptions.categorylore")
                                        .processPlaceholder("category", kit.getCategory() == null ? "none" : kit.getCategory().getName()).getMessage().split("\\|"));
                                aevent.player.closeInventory();
                                return;
                            }
                            plugin.getLocale().getMessage("interface.kitoptions.notacategory").processPlaceholder("input", msg).sendPrefixedMessage(player);
                        });
                        guiManager.showGUI(event.player, gui);
                    } else if (event.clickType == ClickType.RIGHT) {
                        kit.setCategory(null);
                        updateItemLore(event.slot, plugin.getLocale().getMessage("interface.kitoptions.categorylore")
                                        .processPlaceholder("category", kit.getCategory() == null ? "none" : kit.getCategory().getName()).getMessage().split("\\|"));
                    }
                });

        // delete
        setButton(1, 6, GuiUtils.createButtonItem(CompatibleMaterial.TNT,
                plugin.getLocale().getMessage("interface.kitoptions.destroy").getMessage(),
                plugin.getLocale().getMessage("interface.kitoptions.destroylore").getMessage().split("\\|")),
                event -> {
                    AnvilGui gui = new AnvilGui(event.player);
                    gui.setTitle(plugin.getLocale().getMessage("interface.kitoptions.destroyprompt").processPlaceholder("kit", kit.getKey()).getMessage());
                    gui.setAction(aevent -> {
                        final String msg = gui.getInputText();
                        if (msg != null && msg.trim().equalsIgnoreCase(kit.getKey())) {
                            plugin.getKitManager().removeKit(kit);
                            plugin.updateHologram(kit);
                            plugin.getLocale().getMessage("interface.kitoptions.destroyok").sendPrefixedMessage(player);
                        } else {
                            plugin.getLocale().getMessage("interface.kitoptions.destroycancel").sendPrefixedMessage(player);
                        }
                        aevent.player.closeInventory();
                    });
                    guiManager.showGUI(event.player, gui);
                });

    }

}
