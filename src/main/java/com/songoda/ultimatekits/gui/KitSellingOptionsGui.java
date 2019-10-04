package com.songoda.ultimatekits.gui;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.gui.AnvilGui;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.hooks.EconomyManager;
import com.songoda.core.utils.ItemUtils;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.settings.Settings;
import com.songoda.ultimatekits.utils.Methods;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import java.util.Random;

public class KitSellingOptionsGui extends Gui {

    static final Random rand = new Random();

    public KitSellingOptionsGui(UltimateKits plugin, Player player, Kit kit, Gui back) {
        super(back);
        setRows(3);
        setTitle(plugin.getLocale().getMessage("interface.kitblock.title")
                .processPlaceholder("kit", kit.getShowableName())
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

        // remove sale
        setButton(1, 2, GuiUtils.createButtonItem(CompatibleMaterial.BARRIER,
                plugin.getLocale().getMessage("interface.kitsell.nosell").getMessage(),
                plugin.getLocale().getMessage("interface.kitsell.noselllore")
                .processPlaceholder("onoff", plugin.getLocale().getMessage(
                                kit.getPrice() != 0 || kit.getLink() != null ? "interface.kitsell.nosellon" : "interface.kitsell.noselloff").getMessage()
                ).getMessage().split("\\|")),
                event -> {
                    kit.setPrice(0);
                    kit.setLink(null);
                    updateItemLore(event.slot, plugin.getLocale().getMessage("interface.kitsell.noselllore")
                            .processPlaceholder("onoff", plugin.getLocale().getMessage(
                                            kit.getPrice() != 0 || kit.getLink() != null ? "interface.kitsell.nosellon" : "interface.kitsell.noselloff").getMessage()
                            ).getMessage().split("|"));
                });

        // kit link
        setButton(1, 4, GuiUtils.createButtonItem(CompatibleMaterial.PAPER,
                plugin.getLocale().getMessage("interface.kitsell.link").getMessage(),
                plugin.getLocale().getMessage("interface.kitsell.linklore")
                .processPlaceholder("onoff",
                        kit.getLink() != null ? plugin.getLocale().getMessage("interface.kitsell.linkon").processPlaceholder("kit", kit.getLink()).getMessage()
                                : plugin.getLocale().getMessage("interface.kitsell.linkoff").getMessage()
                ).getMessage().split("\\|")),
                event -> {
                    AnvilGui gui = new AnvilGui(event.player, this);
                    gui.setTitle(plugin.getLocale().getMessage("interface.kitsell.linkprompt").getMessage());
                    gui.setAction(aevent -> {
                        final String msg = gui.getInputText();
                        if (kit.getPrice() != 0) {
                            kit.setPrice(0);
                            plugin.getLocale().getMessage("interface.kitsell.linknoeco").sendPrefixedMessage(player);
                        }
                        kit.setLink(msg);
                        plugin.updateHologram(kit);

                        updateItemLore(event.slot, plugin.getLocale().getMessage("interface.kitsell.linklore")
                                .processPlaceholder("onoff",
                                        kit.getLink() != null ? plugin.getLocale().getMessage("interface.kitsell.linkon").processPlaceholder("kit", kit.getLink()).getMessage()
                                                : plugin.getLocale().getMessage("interface.kitsell.linkoff").getMessage()
                                ).getMessage().split("\\|"));

                        aevent.player.closeInventory();
                    });
                    guiManager.showGUI(event.player, gui);
                });

        // kit price
        setButton(1, 6, GuiUtils.createButtonItem(CompatibleMaterial.SUNFLOWER,
                plugin.getLocale().getMessage("interface.kitsell.price").getMessage(),
                plugin.getLocale().getMessage("interface.kitsell.pricelore")
                .processPlaceholder("onoff",
                        kit.getLink() != null ? plugin.getLocale().getMessage("interface.kitsell.priceon").processPlaceholder("kit", kit.getLink()).getMessage()
                                : plugin.getLocale().getMessage("interface.kitsell.priceoff").getMessage()
                ).getMessage().split("\\|")),
                event -> {
                    if (!EconomyManager.isEnabled()) {
                        plugin.getLocale().getMessage("interface.kitsell.pricenoeco").sendPrefixedMessage(event.player);
                        return;
                    }
                    AnvilGui gui = new AnvilGui(event.player, this);
                    gui.setTitle(plugin.getLocale().getMessage("interface.kitsell.priceprompt").getMessage());
                    gui.setAction(aevent -> {
                        final String msg = gui.getInputText();
                        final String num = msg != null ? msg.replaceAll("[^0-9\\.]", "") : "";
                        double d = -1;
                        if (!num.isEmpty()) {
                            try {
                                d = Double.parseDouble(num);
                            } catch (NumberFormatException e) {
                            }
                        }
                        if (d <= 0) {
                            plugin.getLocale().getMessage("interface.kitsell.pricenonumber").processPlaceholder("input", msg).sendPrefixedMessage(player);
                        } else {
                            if (kit.getLink() != null) {
                                kit.setLink(null);
                                plugin.getLocale().getMessage("interface.kitsell.pricenolink").sendPrefixedMessage(player);
                            }
                            kit.setPrice(d);
                            plugin.updateHologram(kit);
                            aevent.player.closeInventory();
                        }
                    });
                    guiManager.showGUI(event.player, gui);
                });
    }
}
