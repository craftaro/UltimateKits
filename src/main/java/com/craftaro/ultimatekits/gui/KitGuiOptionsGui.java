package com.craftaro.ultimatekits.gui;

import com.craftaro.core.gui.AnvilGui;
import com.craftaro.core.gui.Gui;
import com.craftaro.core.gui.GuiUtils;
import com.craftaro.core.utils.ItemUtils;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.ultimatekits.UltimateKits;
import com.craftaro.ultimatekits.kit.Kit;
import com.craftaro.ultimatekits.settings.Settings;
import com.craftaro.ultimatekits.utils.Methods;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class KitGuiOptionsGui extends Gui {
    private final UltimateKits plugin;
    private final Kit kit;
    private final Player player;

    public KitGuiOptionsGui(UltimateKits plugin, Player player, Kit kit, Gui back) {
        super(3);
        this.plugin = plugin;
        this.kit = kit;
        this.player = player;
        setTitle(plugin.getLocale().getMessage("interface.kitblock.title")
                .processPlaceholder("kit", kit.getName())
                .getMessage());
        setAcceptsItems(true); // display item takes an item

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
        // set hologram title
        setButton(1, 2, GuiUtils.createButtonItem(XMaterial.NAME_TAG,
                        this.plugin.getLocale().getMessage("interface.kitguioptions.holo").toText(),
                        this.plugin.getLocale().getMessage("interface.kitguioptions.hololore")
                                .processPlaceholder("onoff",
                                        this.kit.getTitle() != null ? this.plugin.getLocale().getMessage("interface.kitguioptions.holoon").processPlaceholder("title", this.kit.getTitle()).getMessage()
                                                : this.plugin.getLocale().getMessage("interface.kitguioptions.holooff").getMessage()
                                ).toText().split("\\|")),
                ClickType.LEFT,
                event -> {
                    AnvilGui gui = new AnvilGui(event.player, this);
                    gui.setTitle(this.plugin.getLocale().getMessage("interface.kitguioptions.holoprompt").getMessage());
                    gui.setAction(evnt -> {
                        final String msg = gui.getInputText().trim();
                        this.kit.setTitle(msg);
                        this.plugin.getLocale().getMessage("interface.kitguioptions.holoset")
                                .processPlaceholder("title", msg)
                                .processPlaceholder("kit", this.kit.getName())
                                .sendPrefixedMessage(this.player);

                        this.plugin.updateHologram(this.kit);
                        evnt.player.closeInventory();
                        paint();
                        this.plugin.saveKits(false);
                    });
                    this.guiManager.showGUI(event.player, gui);
                });
        setAction(1, 2, ClickType.RIGHT, event -> {
            this.kit.setTitle(null);
            this.plugin.updateHologram(this.kit);
            paint();
            this.plugin.saveKits(false);
        });

        setButton(1, 4, GuiUtils.createButtonItem(this.kit.getDisplayItem() != null ? this.kit.getDisplayItem() : XMaterial.BEACON.parseItem(),
                        this.plugin.getLocale().getMessage("interface.kitguioptions.item").toText(),
                        this.plugin.getLocale().getMessage("interface.kitguioptions.itemlore")
                                .processPlaceholder("onoff",
                                        this.kit.getDisplayItem() != null ? this.plugin.getLocale().getMessage("interface.kitguioptions.itemon")
                                                .processPlaceholder("item", this.kit.getDisplayItem().toString()).getMessage()
                                                : this.plugin.getLocale().getMessage("interface.kitguioptions.itemoff").getMessage()
                                ).toText().split("\\|")),
                ClickType.LEFT,
                event -> {
                    ItemStack is = this.player.getItemInHand();
                    if (is.getType() == Material.AIR) {
                        this.plugin.getLocale().getMessage("interface.kitguioptions.itemnoitem").sendPrefixedMessage(this.player);
                    } else {
                        this.kit.setDisplayItem(is);
                        this.plugin.getLocale().getMessage("interface.kitguioptions.itemset").processPlaceholder("item", this.kit.getName()).sendPrefixedMessage(this.player);
                        paint();
                    }
                    this.plugin.saveKits(false);
                });
        setAction(1, 4, ClickType.RIGHT, event -> {
            this.kit.setDisplayItem(null);
            this.plugin.getLocale().getMessage("interface.kitguioptions.itemremoved").processPlaceholder("kit", this.kit.getName()).sendPrefixedMessage(this.player);
            paint();
        });

        setButton(1, 6, GuiUtils.createButtonItem(XMaterial.COAL,
                        this.plugin.getLocale().getMessage("interface.kitguioptions.hide").toText(),
                        this.plugin.getLocale().getMessage("interface.kitguioptions.hidelore")
                                .processPlaceholder("onoff", this.plugin.getLocale().getMessage(
                                        this.kit.isHidden() ? "interface.kitguioptions.hideon" : "interface.kitguioptions.hideoff").getMessage()
                                ).toText().split("\\|")),
                ClickType.LEFT,
                event -> {
                    this.kit.setHidden(!this.kit.isHidden());
                    paint();
                    this.plugin.saveKits(false);
                });
    }
}
