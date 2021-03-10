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

public class KitSellingOptionsGui extends Gui {

    private final UltimateKits plugin;
    private final Player player;
    private final Kit kit;

    public KitSellingOptionsGui(UltimateKits plugin, Player player, Kit kit, Gui back) {
        super(3);
        this.plugin = plugin;
        this.player = player;
        this.kit = kit;
        setTitle(plugin.getLocale().getMessage("interface.kitblock.title")
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
                event -> guiManager.showGUI(player, new KitEditorGui(plugin, player, kit, back)));
        paint();
    }

    private void paint() {
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
                    paint();
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
                        final String msg = gui.getInputText().trim();
                        if (kit.getPrice() != 0) {
                            kit.setPrice(0);
                            plugin.getLocale().getMessage("interface.kitsell.linknoeco").sendPrefixedMessage(player);
                        }
                        kit.setLink(msg);
                        plugin.updateHologram(kit);
                        aevent.player.closeInventory();
                        paint();
                        plugin.saveKits(false);
                    });
                    guiManager.showGUI(event.player, gui);
                });

        // kit price
        setButton(1, 6, GuiUtils.createButtonItem(CompatibleMaterial.SUNFLOWER,
                plugin.getLocale().getMessage("interface.kitsell.price").getMessage(),
                plugin.getLocale().getMessage("interface.kitsell.pricelore")
                        .processPlaceholder("onoff",
                                kit.getPrice() != 0 ? plugin.getLocale().getMessage("interface.kitsell.priceon")
                                        .processPlaceholder("price", kit.getPrice()).getMessage()
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
                        final String msg = gui.getInputText().trim();
                        double d = 0;
                        try {
                            d = Double.parseDouble(msg);
                        } catch (NumberFormatException e) {
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
                            paint();
                            plugin.saveKits(false);
                        }
                    });
                    guiManager.showGUI(event.player, gui);
                });
    }
}
