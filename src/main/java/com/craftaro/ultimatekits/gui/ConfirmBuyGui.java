package com.craftaro.ultimatekits.gui;

import com.craftaro.core.gui.Gui;
import com.craftaro.core.gui.GuiUtils;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.core.utils.TextUtils;
import com.craftaro.ultimatekits.UltimateKits;
import com.craftaro.ultimatekits.kit.Kit;
import com.craftaro.ultimatekits.settings.Settings;
import com.craftaro.ultimatekits.utils.Methods;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ConfirmBuyGui extends Gui {

    public ConfirmBuyGui(UltimateKits plugin, Player player, Kit kit, Gui back) {
        super(back);
        setRows(3);

        double cost = kit.getPrice();
        if (kit.hasPermissionToClaim(player))
            cost = 0;

        setTitle(plugin.getLocale().getMessage("interface.yesno.title")
                .processPlaceholder("price", cost)
                .getMessage());

        // fill glass borders
        Methods.fillGlass(this);

        // Kit information
        setItem(0, 4, GuiUtils.createButtonItem(kit.getDisplayItem() != null ? kit.getDisplayItem() : XMaterial.DIAMOND_HELMET.parseItem(),
                ChatColor.RED + TextUtils.formatText(kit.getKey().toLowerCase(), true),
                ChatColor.GREEN + Settings.CURRENCY_SYMBOL.getString() + Methods.formatEconomy(cost)));

        // confirm button
        setButton(1, 2, GuiUtils.createButtonItem(Settings.BUY_ICON.getMaterial(XMaterial.EMERALD),
                plugin.getLocale().getMessage("interface.yesno.yes").getMessage()),
                event -> {
                    kit.processPurchaseUse(event.player);
                    exit();
                });

        // cancel button
        setButton(1, 6, GuiUtils.createButtonItem(Settings.EXIT_ICON.getMaterial(XMaterial.OAK_DOOR),
                plugin.getLocale().getMessage("interface.yesno.no").getMessage()),
                event -> {
                    plugin.getLocale().getMessage("event.purchase.cancelled").sendPrefixedMessage(event.player);
                    event.player.closeInventory();
                });

    }

}
