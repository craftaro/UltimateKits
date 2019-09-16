package com.songoda.ultimatekits.gui;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.utils.TextUtils;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.settings.Settings;
import com.songoda.ultimatekits.utils.Methods;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.Random;

public class ConfirmBuyGui extends Gui {

    static final Random rand = new Random();

    public ConfirmBuyGui(UltimateKits plugin, Player player, Kit kit, Gui back) {
        super(back);
        setRows(3);

        double cost = kit.getPrice();
        if (Settings.KITS_FREE_WITH_PERMS.getBoolean() && kit.hasPermission(player)) {
            cost = 0;
        }

        setTitle(plugin.getLocale().getMessage("interface.yesno.title")
                .processPlaceholder("price", cost)
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

        // Kit information
        setItem(0, 4, GuiUtils.createButtonItem(kit.getDisplayItem() != null ? kit.getDisplayItem() : CompatibleMaterial.DIAMOND_HELMET,
                ChatColor.RED + TextUtils.formatText(kit.getName().toLowerCase(), true),
                ChatColor.GREEN + Settings.CURRENCY_SYMBOL.getString() + Methods.formatEconomy(cost)));

        // confirm button
        setButton(1, 2, GuiUtils.createButtonItem(Settings.BUY_ICON.getMaterial(CompatibleMaterial.EMERALD),
                plugin.getLocale().getMessage("interface.yesno.yes").getMessage()),
                event -> {
                    kit.processPurchaseUse(event.player);
                    event.player.closeInventory();
                });

        // cancel button
        setButton(1, 6, GuiUtils.createButtonItem(Settings.EXIT_ICON.getMaterial(CompatibleMaterial.OAK_DOOR),
                plugin.getLocale().getMessage("interface.yesno.no").getMessage()),
                event -> {
                    plugin.getLocale().getMessage("event.purchase.cancelled").sendPrefixedMessage(event.player);
                    event.player.closeInventory();
                });

    }

}
