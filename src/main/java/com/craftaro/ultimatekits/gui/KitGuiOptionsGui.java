package com.craftaro.ultimatekits.gui;

import com.craftaro.core.gui.AnvilGui;
import com.craftaro.core.gui.Gui;
import com.craftaro.core.gui.GuiUtils;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.core.utils.ItemUtils;
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
                event -> guiManager.showGUI(player, new KitEditorGui(plugin, player, kit, back)));
        paint();
    }

    private void paint() {
        // set hologram title
        setButton(1, 2, GuiUtils.createButtonItem(XMaterial.NAME_TAG,
                plugin.getLocale().getMessage("interface.kitguioptions.holo").getMessage(),
                plugin.getLocale().getMessage("interface.kitguioptions.hololore")
                        .processPlaceholder("onoff",
                                kit.getTitle() != null ? plugin.getLocale().getMessage("interface.kitguioptions.holoon").processPlaceholder("title", kit.getTitle()).getMessage()
                                        : plugin.getLocale().getMessage("interface.kitguioptions.holooff").getMessage()
                        ).getMessage().split("\\|")),
                ClickType.LEFT,
                event -> {
                    AnvilGui gui = new AnvilGui(event.player, this);
                    gui.setTitle(plugin.getLocale().getMessage("interface.kitguioptions.holoprompt").getMessage());
                    gui.setAction(evnt -> {
                        final String msg = gui.getInputText().trim();
                        kit.setTitle(msg);
                        plugin.getLocale().getMessage("interface.kitguioptions.holoset")
                                .processPlaceholder("title", msg)
                                .processPlaceholder("kit", kit.getName())
                                .sendPrefixedMessage(player);

                        plugin.updateHologram(kit);
                        evnt.player.closeInventory();
                        paint();
                        plugin.saveKits(false);
                    });
                    guiManager.showGUI(event.player, gui);
                });
        setAction(1, 2, ClickType.RIGHT, event -> {
            kit.setTitle(null);
            plugin.updateHologram(kit);
            paint();
            plugin.saveKits(false);
        });

        setButton(1, 4, GuiUtils.createButtonItem(kit.getDisplayItem() != null ? kit.getDisplayItem() : XMaterial.BEACON.parseItem(),
                plugin.getLocale().getMessage("interface.kitguioptions.item").getMessage(),
                plugin.getLocale().getMessage("interface.kitguioptions.itemlore")
                        .processPlaceholder("onoff",
                                kit.getDisplayItem() != null ? plugin.getLocale().getMessage("interface.kitguioptions.itemon")
                                        .processPlaceholder("item", kit.getDisplayItem().toString()).getMessage()
                                        : plugin.getLocale().getMessage("interface.kitguioptions.itemoff").getMessage()
                        ).getMessage().split("\\|")),
                ClickType.LEFT,
                event -> {
                    ItemStack is = player.getItemInHand();
                    if (is.getType() == Material.AIR) {
                        plugin.getLocale().getMessage("interface.kitguioptions.itemnoitem").sendPrefixedMessage(player);
                    } else {
                        kit.setDisplayItem(is);
                        plugin.getLocale().getMessage("interface.kitguioptions.itemset").processPlaceholder("item", kit.getName()).sendPrefixedMessage(player);
                        paint();
                    }
                    plugin.saveKits(false);
                });
        setAction(1, 4, ClickType.RIGHT, event -> {
            kit.setDisplayItem(null);
            plugin.getLocale().getMessage("interface.kitguioptions.itemremoved").processPlaceholder("kit", kit.getName()).sendPrefixedMessage(player);
            paint();
        });

        setButton(1, 6, GuiUtils.createButtonItem(XMaterial.COAL,
                plugin.getLocale().getMessage("interface.kitguioptions.hide").getMessage(),
                plugin.getLocale().getMessage("interface.kitguioptions.hidelore")
                        .processPlaceholder("onoff", plugin.getLocale().getMessage(
                                kit.isHidden() ? "interface.kitguioptions.hideon" : "interface.kitguioptions.hideoff").getMessage()
                        ).getMessage().split("\\|")),
                ClickType.LEFT,
                event -> {
                    kit.setHidden(!kit.isHidden());
                    paint();
                    plugin.saveKits(false);
                });
    }
}
