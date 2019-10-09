package com.songoda.ultimatekits.gui;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.utils.ItemUtils;
import com.songoda.core.utils.TextUtils;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.settings.Settings;
import com.songoda.ultimatekits.utils.Methods;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class PreviewKitGui extends Gui {

    private final Kit kit;
    private final Player player;
    private final UltimateKits plugin;
    private final boolean buyable;
    private final List<ItemStack> list;
    private final boolean useGlassBorder = !Settings.DO_NOT_USE_GLASS_BORDERS.getBoolean();
    static final Random rand = new Random();

    public PreviewKitGui(UltimateKits plugin, Player player, Kit kit, Gui back) {
        super(back);
        this.kit = kit;
        this.player = player;
        this.plugin = plugin;
        this.list = kit.getReadableContents(player, true, true, false);
        this.buyable = (kit.getLink() != null || kit.getPrice() != 0);

        setTitle(plugin.getLocale().getMessage("interface.preview.title")
                .processPlaceholder("kit", kit.getTitle() != null ? TextUtils.formatText(kit.getTitle(), true) : kit.getShowableName()).getMessage());

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

        int min = 0;
        if (!useGlassBorder) {
            min = 1;
            if (!buyable) {
                ++min;
            }
        }

        if (amt <= 7) {
            setRows(3 - min);
        } else if (amt <= 15) {
            setRows(4 - min);
        } else if (amt <= 23) {
            setRows(5 - min);
        } else {
            setRows(6 - min);
        }

        if (!useGlassBorder) {
            setDefaultItem(AIR);
        } else {

            // fill glass borders
            Methods.fillGlass(this);

            // exit button is only visible with a glass border
            setButton(0, 8, GuiUtils.createButtonItem(Settings.EXIT_ICON.getMaterial(CompatibleMaterial.OAK_DOOR),
                    plugin.getLocale().getMessage("interface.button.exit").getMessage()),
                    event -> exit());

            if (back != null) {
                setButton(0, 0, GuiUtils.createButtonItem(ItemUtils.getCustomHead("3ebf907494a935e955bfcadab81beafb90fb9be49c7026ba97d798d5f1a23"),
                        plugin.getLocale().getMessage("interface.button.back").getMessage()),
                        event -> event.player.closeInventory());
            }
        }
        // purchase button
        if (buyable) {
            setButton(rows - 1, 4, GuiUtils.createButtonItem(Settings.BUY_ICON.getMaterial(CompatibleMaterial.EMERALD),
                    plugin.getLocale().getMessage("interface.button.buynow").getMessage(),
                    getBuyLore()),
                    event -> {
                        exit();
                        kit.buy(event.player, event.manager);
                    });
        }

        // display the kit items here
        Iterator<ItemStack> items = list.iterator();
        int startRow = useGlassBorder ? 1 : 0;
        int endRow = useGlassBorder ? rows - 2 : rows - 1;
        int startCol = useGlassBorder ? 1 : 0;
        int endCol = useGlassBorder ? 7 : 8;
        for (int row = startRow; row <= endRow; ++row) {
            for (int col = startCol; col <= endCol; ++col) {
                ItemStack item;
                if (!items.hasNext()) {
                    setItem(row, col, AIR);
                } else if ((item = items.next()) == null
                        || (Settings.DONT_PREVIEW_COMMANDS.getBoolean()
                        && item.getType() == Material.PAPER
                        && item.getItemMeta().hasDisplayName()
                        && item.getItemMeta().getDisplayName().equals(plugin.getLocale().getMessage("general.type.command")))) {
                    setItem(row, col, AIR);
                } else if (item.getAmount() <= 64) {
                    // display item
                    setItem(row, col, getKitItem(item));
                } else {
                    // correct item amounts (up to three slots)
                    int itAmt = item.getAmount(), slots = 0;
                    for (; itAmt > 0 && slots < 3 && row <= endRow; ++row) {
                        for (; itAmt > 0 && slots < 3 && col <= endCol; ++col) {
                            setItem(row, col, getKitItem(item, Math.min(64, itAmt)));
                            itAmt -= 64;
                            ++slots;
                        }
                    }
                }
            }
        }
    }

    ItemStack getKitItem(ItemStack is) {
        ItemMeta meta = is.getItemMeta();
        List<String> newLore = new ArrayList<>();
        if (meta != null && meta.hasLore()) {
            for (String str : meta.getLore()) {
                newLore.add(str.replace("{PLAYER}", player.getName()).replace("<PLAYER>", player.getName()));
            }
            meta.setLore(newLore);
        }
        is.setItemMeta(meta);
        return is;
    }

    ItemStack getKitItem(ItemStack is, int amount) {
        ItemStack is2 = is.clone();
        ItemMeta meta = is2.getItemMeta();
        List<String> newLore = new ArrayList<>();
        if (meta != null && meta.hasLore()) {
            for (String str : meta.getLore()) {
                newLore.add(str.replace("{PLAYER}", player.getName()).replace("<PLAYER>", player.getName()));
            }
        }
        meta.setLore(newLore);
        is2.setItemMeta(meta);
        is2.setAmount(amount);
        return is;
    }

    List<String> getBuyLore() {
        ArrayList<String> lore = new ArrayList<>();
        if (kit.hasPermission(player) && Settings.KITS_FREE_WITH_PERMS.getBoolean()) {
            lore.add(plugin.getLocale().getMessage("interface.button.clickeco")
                    .processPlaceholder("price", "0").getMessage());
            if (player.isOp()) {
                lore.add("");
                lore.add(ChatColor.GRAY + "This is free because");
                lore.add(ChatColor.GRAY + "you have perms for it.");
                lore.add(ChatColor.GRAY + "Everyone else buys");
                lore.add(ChatColor.GRAY + "this for " + ChatColor.GREEN + "$" + Methods.formatEconomy(kit.getPrice()) + ChatColor.GRAY + ".");
            }
        } else {
            lore.add(plugin.getLocale().getMessage("interface.button.clickeco")
                    .processPlaceholder("price", Methods.formatEconomy(kit.getPrice())).getMessage());
        }
        if (kit.getDelay() != 0 && player.isOp()) {
            lore.add("");
            lore.add(ChatColor.GRAY + "You do not have a delay");
            lore.add(ChatColor.GRAY + "because you have perms");
            lore.add(ChatColor.GRAY + "to bypass the delay.");
        }
        return lore;
    }

}
