package com.songoda.ultimatekits.gui;

import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.utils.Methods;
import com.songoda.ultimatekits.utils.gui.AbstractGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class GUIDisplayKit extends AbstractGUI {

    private Kit kit;
    private Player player;
    private UltimateKits plugin;
    private AbstractGUI back;

    private int max;
    private boolean buyable;
    private List<ItemStack> list;

    public GUIDisplayKit(UltimateKits plugin, AbstractGUI back, Player player, Kit kit) {
        super(player);
        this.kit = kit;
        this.player = player;
        this.plugin = plugin;
        this.back = back;

        String guititle = Methods.formatTitle(plugin.getLocale().getMessage("interface.preview.title", kit.getShowableName()));
        if (kit.getTitle() != null) {
            guititle = plugin.getLocale().getMessage("interface.preview.title", Methods.formatText(kit.getTitle(), true));
        }

        list = kit.getReadableContents(player, true, true, false);

        int amt = 0;
        for (ItemStack is : list) {
            if (is.getAmount() > 64) {
                int overflow = is.getAmount() % 64;
                int stackamt = is.getAmount() / 64;
                int num3 = 0;
                while (num3 != stackamt) {
                    amt++;
                    num3++;
                }
                if (overflow != 0) {
                    amt++;
                }
            } else {
                amt++;
            }
        }

        buyable = false;
        if (kit.getLink() != null || kit.getPrice() != 0) {
            buyable = true;
        }

        int min = 0;
        if (plugin.getConfig().getBoolean("Interfaces.Do Not Use Glass Borders")) {
            min = 9;
            if (!buyable) {
                min = min + 9;
            }
        }

        max = 54 - min;
        if (amt <= 7) {
            max = 27 - min;
        } else if (amt <= 15) {
            max = 36 - min;
        } else if (amt <= 23) {
            max = 45 - min;
        }
        init(guititle, max);
    }

    @Override
    protected void constructGUI() {
        int num = 0;
        if (!plugin.getConfig().getBoolean("Interfaces.Do Not Use Glass Borders")) {
            ItemStack exit = new ItemStack(Material.valueOf(plugin.getConfig().getString("Interfaces.Exit Icon")), 1);
            ItemMeta exitmeta = exit.getItemMeta();
            exitmeta.setDisplayName(UltimateKits.getInstance().getLocale().getMessage("interface.button.exit"));
            exit.setItemMeta(exitmeta);
            while (num != 10) {
                inventory.setItem(num, Methods.getGlass());
                num++;
            }
            int num2 = max - 10;
            while (num2 != max) {
                inventory.setItem(num2, Methods.getGlass());
                num2++;
            }
            inventory.setItem(8, exit);

            inventory.setItem(0, Methods.getBackgroundGlass(true));
            inventory.setItem(1, Methods.getBackgroundGlass(true));
            inventory.setItem(9, Methods.getBackgroundGlass(true));

            inventory.setItem(7, Methods.getBackgroundGlass(true));
            inventory.setItem(17, Methods.getBackgroundGlass(true));

            inventory.setItem(max - 18, Methods.getBackgroundGlass(true));
            inventory.setItem(max - 9, Methods.getBackgroundGlass(true));
            inventory.setItem(max - 8, Methods.getBackgroundGlass(true));

            inventory.setItem(max - 10, Methods.getBackgroundGlass(true));
            inventory.setItem(max - 2, Methods.getBackgroundGlass(true));
            inventory.setItem(max - 1, Methods.getBackgroundGlass(true));

            inventory.setItem(2, Methods.getBackgroundGlass(false));
            inventory.setItem(6, Methods.getBackgroundGlass(false));
            inventory.setItem(max - 7, Methods.getBackgroundGlass(false));
            inventory.setItem(max - 3, Methods.getBackgroundGlass(false));
        }

        if (buyable) {
            ItemStack link = new ItemStack(Material.valueOf(plugin.getConfig().getString("Interfaces.Buy Icon")), 1);
            ItemMeta linkmeta = link.getItemMeta();
            linkmeta.setDisplayName(plugin.getLocale().getMessage("interface.button.buynow"));
            ArrayList<String> lore = new ArrayList<>();
            if (kit.hasPermission(player) && plugin.getConfig().getBoolean("Main.Allow Players To Receive Kits For Free If They Have Permission")) {
                lore.add(plugin.getLocale().getMessage("interface.button.clickeco", "0"));
                if (player.isOp()) {
                    lore.add("");
                    lore.add(Methods.formatText("&7This is free because"));
                    lore.add(Methods.formatText("&7you have perms for it."));
                    lore.add(Methods.formatText("&7Everyone else buys"));
                    lore.add(Methods.formatText("&7this for &a$" + Methods.formatEconomy(kit.getPrice()) + "&7."));
                }
            } else {
                lore.add(plugin.getLocale().getMessage("interface.button.clickeco", Methods.formatEconomy(kit.getPrice())));
            }
            if (kit.getDelay() != 0 && player.isOp()) {
                lore.add("");
                lore.add(Methods.formatText("&7You do not have a delay"));
                lore.add(Methods.formatText("&7because you have perms"));
                lore.add(Methods.formatText("&7to bypass the delay."));
            }
            linkmeta.setLore(lore);
            link.setItemMeta(linkmeta);
            inventory.setItem(max - 5, link);
        }

        for (ItemStack is : list) {
            if (!plugin.getConfig().getBoolean("Interfaces.Do Not Use Glass Borders")) {
                if (num == 17 || num == (max - 18)) num++;
                if (num == 18 && max == 36) num++;
            }
            System.out.println("hit "  + num);

            ItemMeta meta = is.hasItemMeta() ? is.getItemMeta() : Bukkit.getItemFactory().getItemMeta(is.getType());
            ArrayDeque<String> lore;
            if (meta.hasLore()) {
                lore = new ArrayDeque<>(meta.getLore());
            } else {
                lore = new ArrayDeque<>();
            }

            List<String> newLore = new ArrayList<>();
            for (String str : lore) {
                str = str.replace("{PLAYER}", player.getName()).replace("<PLAYER>", player.getName());
                newLore.add(str);
            }

            meta.setLore(newLore);
            is.setItemMeta(meta);

            if (is.getAmount() > 64) {
                int overflow = is.getAmount() % 64;
                int stackamt = is.getAmount() / 64;
                int num3 = 0;
                while (num3 != stackamt) {
                    is.setAmount(64);
                    inventory.setItem(num, is);
                    num++;
                    num3++;
                }
                if (overflow != 0) {
                    is.setAmount(overflow);
                    inventory.setItem(num, is);
                    num++;
                }
                continue;
            }
            if (!plugin.getConfig().getBoolean("Main.Dont Preview Commands In Kits") || is.getType() != Material.PAPER || !is.getItemMeta().hasDisplayName() || !is.getItemMeta().getDisplayName().equals(plugin.getLocale().getMessage("general.type.command"))) {
                inventory.setItem(num, is);
                num++;
            }
        }

        if (back != null && !plugin.getConfig().getBoolean("Interfaces.Do Not Use Glass Borders")) {

            ItemStack head2 = new ItemStack(Material.PLAYER_HEAD, 1, (byte) 3);
            ItemStack skull2 = Methods.addTexture(head2, "http://textures.minecraft.net/texture/3ebf907494a935e955bfcadab81beafb90fb9be49c7026ba97d798d5f1a23");
            SkullMeta skull2Meta = (SkullMeta) skull2.getItemMeta();
            skull2.setDurability((short) 3);
            skull2Meta.setDisplayName(UltimateKits.getInstance().getLocale().getMessage("interface.button.back"));
            skull2.setItemMeta(skull2Meta);
            inventory.setItem(0, skull2);
        }
    }

    @Override
    protected void registerClickables() {
        registerClickable(0, (player, inventory, cursor, slot, type) -> {
            if (back == null) return;
            back.init(back.getInventory().getTitle(), back.getInventory().getSize());
        });

        registerClickable(8, (player, inventory, cursor, slot, type) -> player.closeInventory());

        registerClickable(max - 5, ((player1, inventory1, cursor, slot, type) -> {
            player.closeInventory();
            String kitName = kit.getName();
            Kit kit = plugin.getKitManager().getKit(kitName);
            kit.buy(player);
        }));

    }

    @Override
    protected void registerOnCloses() {

    }

}
