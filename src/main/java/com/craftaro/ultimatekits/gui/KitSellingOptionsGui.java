package com.craftaro.ultimatekits.gui;

import com.craftaro.core.gui.AnvilGui;
import com.craftaro.core.gui.Gui;
import com.craftaro.core.gui.GuiUtils;
import com.craftaro.core.hooks.EconomyManager;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.core.utils.ItemUtils;
import com.craftaro.ultimatekits.UltimateKits;
import com.craftaro.ultimatekits.kit.Kit;
import com.craftaro.ultimatekits.settings.Settings;
import com.craftaro.ultimatekits.utils.Methods;
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
        setButton(0, 8, GuiUtils.createButtonItem(Settings.EXIT_ICON.getMaterial(XMaterial.OAK_DOOR),
                        plugin.getLocale().getMessage("interface.button.exit").getMessage()),
                ClickType.LEFT,
                event -> exit());

        // back button
        setButton(0, 0, GuiUtils.createButtonItem(ItemUtils.getCustomHead("3ebf907494a935e955bfcadab81beafb90fb9be49c7026ba97d798d5f1a23"),
                        plugin.getLocale().getMessage("interface.button.back").getMessage()),
                ClickType.LEFT,
                event -> this.guiManager.showGUI(player, new KitEditorGui(plugin, player, kit, back)));
        paint();
    }

    private void paint() {
        // remove sale
        setButton(1, 2, GuiUtils.createButtonItem(XMaterial.BARRIER,
                        this.plugin.getLocale().getMessage("interface.kitsell.nosell").getMessage(),
                        this.plugin.getLocale().getMessage("interface.kitsell.noselllore")
                                .processPlaceholder("onoff", this.plugin.getLocale().getMessage(
                                        this.kit.getPrice() != 0 || this.kit.getLink() != null ? "interface.kitsell.nosellon" : "interface.kitsell.noselloff").getMessage()
                                ).getMessage().split("\\|")),
                event -> {
                    this.kit.setPrice(0);
                    this.kit.setLink(null);
                    paint();
                });

        // kit link
        setButton(1, 4, GuiUtils.createButtonItem(XMaterial.PAPER,
                        this.plugin.getLocale().getMessage("interface.kitsell.link").getMessage(),
                        this.plugin.getLocale().getMessage("interface.kitsell.linklore")
                                .processPlaceholder("onoff",
                                        this.kit.getLink() != null ? this.plugin.getLocale().getMessage("interface.kitsell.linkon").processPlaceholder("kit", this.kit.getLink()).getMessage()
                                                : this.plugin.getLocale().getMessage("interface.kitsell.linkoff").getMessage()
                                ).getMessage().split("\\|")),
                event -> {
                    AnvilGui gui = new AnvilGui(event.player, this);
                    gui.setTitle(this.plugin.getLocale().getMessage("interface.kitsell.linkprompt").getMessage());
                    gui.setAction(aevent -> {
                        final String msg = gui.getInputText().trim();
                        if (this.kit.getPrice() != 0) {
                            this.kit.setPrice(0);
                            this.plugin.getLocale().getMessage("interface.kitsell.linknoeco").sendPrefixedMessage(this.player);
                        }
                        this.kit.setLink(msg);
                        this.plugin.updateHologram(this.kit);
                        aevent.player.closeInventory();
                        paint();
                        this.plugin.saveKits(false);
                    });
                    this.guiManager.showGUI(event.player, gui);
                });

        // kit price
        setButton(1, 6, GuiUtils.createButtonItem(XMaterial.SUNFLOWER,
                        this.plugin.getLocale().getMessage("interface.kitsell.price").getMessage(),
                        this.plugin.getLocale().getMessage("interface.kitsell.pricelore")
                                .processPlaceholder("onoff",
                                        this.kit.getPrice() != 0 ? this.plugin.getLocale().getMessage("interface.kitsell.priceon")
                                                .processPlaceholder("price", this.kit.getPrice()).getMessage()
                                                : this.plugin.getLocale().getMessage("interface.kitsell.priceoff").getMessage()
                                ).getMessage().split("\\|")),
                event -> {
                    if (!EconomyManager.isEnabled()) {
                        this.plugin.getLocale().getMessage("interface.kitsell.pricenoeco").sendPrefixedMessage(event.player);
                        return;
                    }
                    AnvilGui gui = new AnvilGui(event.player, this);
                    gui.setTitle(this.plugin.getLocale().getMessage("interface.kitsell.priceprompt").getMessage());
                    gui.setAction(aevent -> {
                        final String msg = gui.getInputText().trim();
                        double d = 0;
                        try {
                            d = Double.parseDouble(msg);
                        } catch (NumberFormatException e) {
                        }
                        if (d <= 0) {
                            this.plugin.getLocale().getMessage("interface.kitsell.pricenonumber").processPlaceholder("input", msg).sendPrefixedMessage(this.player);
                        } else {
                            if (this.kit.getLink() != null) {
                                this.kit.setLink(null);
                                this.plugin.getLocale().getMessage("interface.kitsell.pricenolink").sendPrefixedMessage(this.player);
                            }
                            this.kit.setPrice(d);
                            this.plugin.updateHologram(this.kit);
                            aevent.player.closeInventory();
                            paint();
                            this.plugin.saveKits(false);
                        }
                    });
                    this.guiManager.showGUI(event.player, gui);
                });
    }
}
