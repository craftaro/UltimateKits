package com.songoda.ultimatekits.gui;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.ultimatekits.Lang;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.utils.Methods;
import com.songoda.ultimatekits.utils.gui.AbstractGUI;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class GUIConfirmBuy extends AbstractGUI {

    private Kit kit;
    private Player player;
    private UltimateKits plugin;

    public GUIConfirmBuy(UltimateKits plugin, Player player, Kit kit) {
        super(player);
        this.kit = kit;
        this.player = player;
        this.plugin = plugin;
        init(Lang.GUI_TITLE_YESNO.getConfigValue(kit.getPrice()), 27);
    }

    @Override
    protected void constructGUI() {
        double cost = kit.getPrice();
        if (kit.hasPermission(player) && plugin.getConfig().getBoolean("Main.Allow Players To Receive Kits For Free If They Have Permission")) {
            cost = 0;
        }

        String title = Arconix.pl().getApi().format().formatTitle("&c" + StringUtils.capitalize(kit.getName().toLowerCase()));
        ItemStack item = new ItemStack(Material.DIAMOND_HELMET);
        if (kit.getDisplayItem() != null) item = new ItemStack(kit.getDisplayItem());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(title);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(Arconix.pl().getApi().format().formatText("&a$" + Arconix.pl().getApi().format().formatEconomy(cost)));

        int nu = 0;
        while (nu != 27) {
            inventory.setItem(nu, Methods.getGlass());
            nu++;
        }

        inventory.setItem(0, Methods.getBackgroundGlass(true));
        inventory.setItem(1, Methods.getBackgroundGlass(true));
        inventory.setItem(2, Methods.getBackgroundGlass(false));
        inventory.setItem(6, Methods.getBackgroundGlass(false));
        inventory.setItem(7, Methods.getBackgroundGlass(true));
        inventory.setItem(8, Methods.getBackgroundGlass(true));
        inventory.setItem(9, Methods.getBackgroundGlass(true));
        inventory.setItem(10, Methods.getBackgroundGlass(false));
        inventory.setItem(16, Methods.getBackgroundGlass(false));
        inventory.setItem(17, Methods.getBackgroundGlass(true));
        inventory.setItem(18, Methods.getBackgroundGlass(true));
        inventory.setItem(19, Methods.getBackgroundGlass(true));
        inventory.setItem(20, Methods.getBackgroundGlass(false));
        inventory.setItem(24, Methods.getBackgroundGlass(false));
        inventory.setItem(25, Methods.getBackgroundGlass(true));
        inventory.setItem(26, Methods.getBackgroundGlass(true));

        ItemStack item2 = new ItemStack(Material.valueOf(plugin.getConfig().getString("Interfaces.Buy Icon")), 1);
        ItemMeta itemmeta2 = item2.getItemMeta();
        itemmeta2.setDisplayName(Lang.YES_GUI.getConfigValue());
        item2.setItemMeta(itemmeta2);

        ItemStack item3 = new ItemStack(Material.valueOf(plugin.getConfig().getString("Interfaces.Exit Icon")), 1);
        ItemMeta itemmeta3 = item3.getItemMeta();
        itemmeta3.setDisplayName(Lang.NO_GUI.getConfigValue());
        item3.setItemMeta(itemmeta3);

        inventory.setItem(4, item);
        inventory.setItem(11, item2);
        inventory.setItem(15, item3);
    }

    @Override
    protected void registerClickables() {
        registerClickable(11, ((player1, inventory1, cursor, slot, type) -> {
            kit.buyWithEconomy(player);
            player.closeInventory();
        }));

        registerClickable(15, ((player1, inventory1, cursor, slot, type) -> {
            player.sendMessage(Arconix.pl().getApi().format().formatText(plugin.getReferences().getPrefix() + Lang.BUYCANCELLED.getConfigValue()));
            player.closeInventory();
        }));
    }

    @Override
    protected void registerOnCloses() {

    }

}
