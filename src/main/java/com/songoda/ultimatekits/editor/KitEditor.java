package com.songoda.ultimatekits.editor;

import com.songoda.arconix.api.methods.formatting.TextComponent;
import com.songoda.arconix.plugin.Arconix;
import com.songoda.ultimatekits.Lang;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.kit.KitAnimation;
import com.songoda.ultimatekits.kit.KitItem;
import com.songoda.ultimatekits.utils.Debugger;
import com.songoda.ultimatekits.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

/**
 * Created by songoda on 3/2/2017.
 */
public class KitEditor {

    private final Map<UUID, KitEditorPlayerData> editorPlayerData = new HashMap<>();
    private UltimateKits instance;

    public KitEditor(UltimateKits instance) {
        this.instance = instance;
    }

    public void openOverview(Kit kit, Player player, boolean backb, ItemStack toReplace, int slot) {
        try {
            KitEditorPlayerData playerData = getDataFor(player);
            playerData.setToReplace(null);
            playerData.setToReplaceSlot(0);

            //assign kit to object.
            playerData.setKit(kit);

            player.updateInventory();
            String name = kit.getShowableName();
            Inventory i = Bukkit.createInventory(null, 54, Arconix.pl().getApi().format().formatTitle("&8You are editing kit: &9" + name + "&8."));

            ItemStack exit = new ItemStack(Material.valueOf(instance.getConfig().getString("Interfaces.Exit Icon")), 1);
            ItemMeta exitmeta = exit.getItemMeta();
            exitmeta.setDisplayName(Lang.EXIT.getConfigValue());
            exit.setItemMeta(exitmeta);

            ItemStack head2 = new ItemStack(Material.PLAYER_HEAD, 1, (byte) 3);
            ItemStack back;
            back = Arconix.pl().getApi().getGUI().addTexture(head2, "http://textures.minecraft.net/texture/3ebf907494a935e955bfcadab81beafb90fb9be49c7026ba97d798d5f1a23");
            SkullMeta skull2Meta = (SkullMeta) back.getItemMeta();
            back.setDurability((short) 3);
            skull2Meta.setDisplayName(Lang.BACK.getConfigValue());
            back.setItemMeta(skull2Meta);


            ItemStack it = new ItemStack(Material.CHEST, 1);
            ItemMeta itmeta = it.getItemMeta();
            itmeta.setDisplayName(Arconix.pl().getApi().format().formatText("&5&l" + playerData.getKit().getName()));
            ArrayList<String> lore = new ArrayList<>();
            lore.add(Arconix.pl().getApi().format().formatText("&fPermissions:"));
            lore.add(Arconix.pl().getApi().format().formatText("&7ultimatekits.kit." + playerData.getKit().getName().toLowerCase()));
            itmeta.setLore(lore);
            it.setItemMeta(itmeta);

            ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
            ItemMeta glassmeta = glass.getItemMeta();
            glassmeta.setDisplayName(Arconix.pl().getApi().format().formatText("&" + playerData.getKit().getName().replaceAll(".(?!$)", "$0&")));
            glass.setItemMeta(glassmeta);

            if (backb)
                i.setItem(0, back);
            i.setItem(4, it);
            i.setItem(8, exit);

            int num = 10;
            List<ItemStack> list = kit.getReadableContents(player, false, true, true);
            for (ItemStack iss : list) {
                if (num == 17 || num == 36)
                    num++;

                if (num == slot && toReplace != null) {
                    iss = toReplace;
                }

                KitItem item = new KitItem(iss);

                ItemStack is = item.getMoveableItem();

                ItemMeta meta;

                if (is.hasItemMeta()) meta = is.getItemMeta();
                else meta = Bukkit.getItemFactory().getItemMeta(is.getType());

                List<String> itemLore;

                if (meta.hasLore()) itemLore = meta.getLore();
                else itemLore = new ArrayList<>();
                itemLore.add(TextComponent.convertToInvisibleString("----"));
                itemLore.add(TextComponent.formatText("&7Chance: &6" + item.getChance() + "%"));
                if (playerData.isInFuction()) {
                    itemLore.add(TextComponent.formatText("&7Display Item: &6" + (item.getDisplayItem() == null ? "null" : item.getDisplayItem().name())));
                    itemLore.add(TextComponent.formatText("&7Display Name: &6" + TextComponent.formatText(item.getDisplayName())));
                    itemLore.add(TextComponent.formatText("&7Display Lore: &6" + TextComponent.formatText(item.getDisplayLore())));
                }
                itemLore.add("");
                if (playerData.isInFuction()) {
                    itemLore.add(TextComponent.formatText("&7Left-Click: &6To set a display item."));
                    itemLore.add(TextComponent.formatText("&7Middle-Click: &6To set a display name."));
                    itemLore.add(TextComponent.formatText("&7Right-Click: &6To set display lore."));
                    itemLore.add(TextComponent.formatText("&7Shift-Click: &6To set chance."));
                    itemLore.add("");
                    itemLore.add(TextComponent.formatText("&7Display options only show up on display."));
                    itemLore.add(TextComponent.formatText("&7This can be useful if you want to explain"));
                    itemLore.add(TextComponent.formatText("&7What an item does without putting it in the"));
                    itemLore.add(TextComponent.formatText("&7permanent lore."));
                    itemLore.add("");
                    itemLore.add(TextComponent.formatText("&6Leave function mode to move items."));
                }
                meta.setLore(itemLore);
                is.setItemMeta(meta);

                if (is.getAmount() > 64) {
                    int overflow = is.getAmount() % 64;
                    int stackamt = (int) ((long) (is.getAmount() / 64));
                    int num3 = 0;
                    while (num3 != stackamt) {
                        is.setAmount(64);
                        i.setItem(num, is);
                        num++;
                        num3++;
                    }
                    if (overflow != 0) {
                        is.setAmount(overflow);
                        i.setItem(num, is);
                        num++;
                    }
                } else {
                    i.setItem(num, is);
                    num++;
                }
            }
            if (toReplace != null && slot == 0)
                i.setItem(num, toReplace);

            i.setItem(3, Methods.getGlass());
            i.setItem(5, Methods.getGlass());

            i.setItem(48, Methods.getGlass());
            i.setItem(50, Methods.getGlass());

            if (!backb)
                i.setItem(0, Methods.getBackgroundGlass(true));
            i.setItem(1, Methods.getBackgroundGlass(true));
            i.setItem(9, Methods.getBackgroundGlass(true));

            i.setItem(7, Methods.getBackgroundGlass(true));
            i.setItem(17, Methods.getBackgroundGlass(true));

            i.setItem(54 - 18, Methods.getBackgroundGlass(true));
            i.setItem(54 - 9, Methods.getBackgroundGlass(true));
            i.setItem(54 - 8, Methods.getBackgroundGlass(true));

            i.setItem(54 - 10, Methods.getBackgroundGlass(true));
            i.setItem(54 - 2, Methods.getBackgroundGlass(true));
            i.setItem(54 - 1, Methods.getBackgroundGlass(true));

            i.setItem(2, Methods.getBackgroundGlass(false));
            i.setItem(6, Methods.getBackgroundGlass(false));
            i.setItem(54 - 7, Methods.getBackgroundGlass(false));
            i.setItem(54 - 3, Methods.getBackgroundGlass(false));


            player.openInventory(i);
            playerData.setEditorType(KitEditorPlayerData.EditorType.OVERVIEW);

            getInvItems(player, playerData);
            updateInvButton(i, playerData);
        } catch (Exception e) {
            Debugger.runReport(e);
        }
    }

    public void replaceItem(Action action, Player player, ItemStack itemStack, int slot) {
        KitEditorPlayerData playerData = getDataFor(player);
        playerData.setToReplace(itemStack);
        playerData.setToReplaceSlot(slot);

        if (itemStack.getItemMeta().hasLore()) {
            ItemMeta meta = itemStack.getItemMeta();
            List<String> newLore = new ArrayList<>();
            for (String line : meta.getLore()) {
                if (line.equals(TextComponent.convertToInvisibleString("----"))) break;
                newLore.add(line);
            }
            meta.setLore(newLore);
            itemStack.setItemMeta(meta);
        }

        KitItem item = new KitItem(itemStack);

        switch (action) {
            case CHANCE:
                item.setChance(item.getChance() == 100 ? 5 : (item.getChance() + 5));
                playerData.setMuteSave(true);
                openOverview(getDataFor(player).getKit(), player, false, item.getMoveableItem(), slot);
                break;
            case DISPLAY_ITEM:
                editDisplayItem(player);
                break;
            case DISPLAY_NAME:
                editDisplayName(player);
                break;
            case DISPLAY_LORE:
                editDisplayLore(player);
                break;
        }
    }

    public void updateInvButton(Inventory i, KitEditorPlayerData playerData) {
        ItemStack alli = new ItemStack(Material.PAPER, 1);
        ItemMeta allmeta = alli.getItemMeta();
        if (!playerData.isInFuction()) {
            allmeta.setDisplayName(Arconix.pl().getApi().format().formatText("&6Switch To Item Editing"));
            List<String> lore = new ArrayList<>();
            lore.add(Arconix.pl().getApi().format().formatText("&7Click to enable"));
            lore.add(Arconix.pl().getApi().format().formatText("&7item editing."));
            allmeta.setLore(lore);
        } else {
            allmeta.setDisplayName(Arconix.pl().getApi().format().formatText("&6Switch To Item Moving"));
            List<String> lore = new ArrayList<>();
            lore.add(Arconix.pl().getApi().format().formatText("&7Click to switch back"));
            lore.add(Arconix.pl().getApi().format().formatText("&7to item moving."));
            allmeta.setLore(lore);
        }
        alli.setItemMeta(allmeta);
        i.setItem(48, alli);

        alli = new ItemStack(Material.ITEM_FRAME, 1);
        allmeta = alli.getItemMeta();
        if (!playerData.isInInventory()) {
            allmeta.setDisplayName(Arconix.pl().getApi().format().formatText("&6Switch To Your Inventory"));
            List<String> lore = new ArrayList<>();
            lore.add(Arconix.pl().getApi().format().formatText("&7Click to switch to"));
            lore.add(Arconix.pl().getApi().format().formatText("&7your inventory."));
            allmeta.setLore(lore);
        } else {
            allmeta.setDisplayName(Arconix.pl().getApi().format().formatText("&6Switch To Kit Functions"));
            List<String> lore = new ArrayList<>();
            lore.add(Arconix.pl().getApi().format().formatText("&7Click to switch back"));
            lore.add(Arconix.pl().getApi().format().formatText("&7to the kit functions."));
            allmeta.setLore(lore);
        }
        alli.setItemMeta(allmeta);
        i.setItem(50, alli);
    }

    public void getInvItems(Player player, KitEditorPlayerData playerData) {

        playerData.setInventory(player.getInventory().getContents().clone());
        playerData.setInInventory(false);
        player.getInventory().clear();

        ItemStack alli = new ItemStack(Material.REDSTONE_TORCH, 1);
        ItemMeta allmeta = alli.getItemMeta();
        allmeta.setDisplayName(Arconix.pl().getApi().format().formatText("&6General Options"));
        List<String> lore = new ArrayList<>();
        lore.add(Arconix.pl().getApi().format().formatText("&7Click to edit adjust"));
        lore.add(Arconix.pl().getApi().format().formatText("&7general options."));
        allmeta.setLore(lore);
        alli.setItemMeta(allmeta);

        player.getInventory().setItem(9, alli);

        alli = new ItemStack(Material.EMERALD, 1);
        allmeta = alli.getItemMeta();
        allmeta.setDisplayName(Arconix.pl().getApi().format().formatText("&9Selling Options"));
        lore = new ArrayList<>();
        lore.add(Arconix.pl().getApi().format().formatText("&7Click to edit adjust"));
        lore.add(Arconix.pl().getApi().format().formatText("&7selling options."));
        allmeta.setLore(lore);
        alli.setItemMeta(allmeta);

        player.getInventory().setItem(10, alli);

        alli = new ItemStack(Material.ITEM_FRAME, 1);
        allmeta = alli.getItemMeta();
        allmeta.setDisplayName(Arconix.pl().getApi().format().formatText("&5GUI Options"));
        lore = new ArrayList<>();
        lore.add(Arconix.pl().getApi().format().formatText("&7Click to edit GUI options"));
        lore.add(Arconix.pl().getApi().format().formatText("&7for this kit."));
        allmeta.setLore(lore);
        alli.setItemMeta(allmeta);

        player.getInventory().setItem(12, alli);

        alli = new ItemStack(Material.PAPER, 1);
        allmeta = alli.getItemMeta();
        allmeta.setDisplayName(Arconix.pl().getApi().format().formatText("&fAdd Command"));
        lore = new ArrayList<>();
        lore.add(Arconix.pl().getApi().format().formatText("&7Click to add a command"));
        lore.add(Arconix.pl().getApi().format().formatText("&7to this kit."));
        allmeta.setLore(lore);
        alli.setItemMeta(allmeta);

        player.getInventory().setItem(13, alli);

        alli = new ItemStack(Material.SUNFLOWER, 1);
        allmeta = alli.getItemMeta();
        allmeta.setDisplayName(Arconix.pl().getApi().format().formatText("&6Add Economy"));
        lore = new ArrayList<>();
        lore.add(Arconix.pl().getApi().format().formatText("&7Click to add money"));
        lore.add(Arconix.pl().getApi().format().formatText("&7to this kit."));
        allmeta.setLore(lore);
        alli.setItemMeta(allmeta);

        player.getInventory().setItem(14, alli);

        alli = new ItemStack(Material.CHEST, 1);
        allmeta = alli.getItemMeta();
        allmeta.setDisplayName(Arconix.pl().getApi().format().formatText("&6Kit Animation"));
        lore = new ArrayList<>();
        lore.add(Arconix.pl().getApi().format().formatText("&7Currently: &6" + playerData.getKit().getKitAnimation().name()));
        allmeta.setLore(lore);
        alli.setItemMeta(allmeta);

        player.getInventory().setItem(17, alli);
    }

    public void selling(Player player) {
        try {
            KitEditorPlayerData playerData = getDataFor(player);
            Kit kit = playerData.getKit();

            Inventory i = Bukkit.createInventory(null, 27, Arconix.pl().getApi().format().formatTitle("&8Selling Options for &a" + kit.getShowableName() + "&8."));

            Methods.fillGlass(i);

            i.setItem(0, Methods.getBackgroundGlass(true));
            i.setItem(1, Methods.getBackgroundGlass(true));
            i.setItem(2, Methods.getBackgroundGlass(false));
            i.setItem(6, Methods.getBackgroundGlass(false));
            i.setItem(7, Methods.getBackgroundGlass(true));
            i.setItem(8, Methods.getBackgroundGlass(true));
            i.setItem(9, Methods.getBackgroundGlass(true));
            i.setItem(10, Methods.getBackgroundGlass(false));
            i.setItem(16, Methods.getBackgroundGlass(false));
            i.setItem(17, Methods.getBackgroundGlass(true));
            i.setItem(18, Methods.getBackgroundGlass(true));
            i.setItem(19, Methods.getBackgroundGlass(true));
            i.setItem(20, Methods.getBackgroundGlass(false));
            i.setItem(24, Methods.getBackgroundGlass(false));
            i.setItem(25, Methods.getBackgroundGlass(true));
            i.setItem(26, Methods.getBackgroundGlass(true));

            ItemStack exit = new ItemStack(Material.valueOf(instance.getConfig().getString("Interfaces.Exit Icon")), 1);
            ItemMeta exitmeta = exit.getItemMeta();
            exitmeta.setDisplayName(Lang.EXIT.getConfigValue());
            exit.setItemMeta(exitmeta);

            ItemStack head2 = new ItemStack(Material.PLAYER_HEAD, 1, (byte) 3);
            ItemStack back = head2;
            back = Arconix.pl().getApi().getGUI().addTexture(head2, "http://textures.minecraft.net/texture/3ebf907494a935e955bfcadab81beafb90fb9be49c7026ba97d798d5f1a23");
            SkullMeta skull2Meta = (SkullMeta) back.getItemMeta();
            back.setDurability((short) 3);
            skull2Meta.setDisplayName(Lang.BACK.getConfigValue());
            back.setItemMeta(skull2Meta);

            i.setItem(0, back);
            i.setItem(8, exit);

            ItemStack alli = new ItemStack(Material.DIAMOND_HELMET);
            ItemMeta allmeta = alli.getItemMeta();
            allmeta.setDisplayName(Arconix.pl().getApi().format().formatText("&c&lSet not for sale"));
            ArrayList<String> lore = new ArrayList<>();

            if (kit.getPrice() != 0 ||
                    kit.getLink() != null)
                lore.add(Arconix.pl().getApi().format().formatText("&7Currently &aFor Sale&7."));
            else
                lore.add(Arconix.pl().getApi().format().formatText("&7Currently &cNot For Sale&7."));
            lore.add(Arconix.pl().getApi().format().formatText(""));
            lore.add(Arconix.pl().getApi().format().formatText("&7Clicking this option will"));
            lore.add(Arconix.pl().getApi().format().formatText("&7remove this kit from sale."));
            allmeta.setLore(lore);
            alli.setItemMeta(allmeta);

            i.setItem(11, alli);

            alli = new ItemStack(Material.DIAMOND_HELMET);
            allmeta = alli.getItemMeta();
            allmeta.setDisplayName(Arconix.pl().getApi().format().formatText("&a&lSet kit link"));
            lore = new ArrayList<>();
            if (kit.getLink() != null)
                lore.add(Arconix.pl().getApi().format().formatText("&7Currently: &a" + kit.getLink() + "&7."));
            else
                lore.add(Arconix.pl().getApi().format().formatText("&7Currently: &cNot set&7."));
            lore.add(Arconix.pl().getApi().format().formatText(""));
            lore.add(Arconix.pl().getApi().format().formatText("&7Clicking this option will"));
            lore.add(Arconix.pl().getApi().format().formatText("&7allow you to set a link"));
            lore.add(Arconix.pl().getApi().format().formatText("&7that players will receive"));
            lore.add(Arconix.pl().getApi().format().formatText("&7when attempting to purchase"));
            lore.add(Arconix.pl().getApi().format().formatText("&7this kit."));
            allmeta.setLore(lore);
            alli.setItemMeta(allmeta);

            i.setItem(13, alli);

            alli = new ItemStack(Material.DIAMOND_HELMET);
            allmeta = alli.getItemMeta();
            allmeta.setDisplayName(Arconix.pl().getApi().format().formatText("&a&lSet kit link"));
            lore = new ArrayList<>();
            if (kit.getLink() != null)
                lore.add(Arconix.pl().getApi().format().formatText("&7Currently: &a" + kit.getLink() + "&7."));
            else
                lore.add(Arconix.pl().getApi().format().formatText("&7Currently: &cNot set&7."));
            lore.add(Arconix.pl().getApi().format().formatText(""));
            lore.add(Arconix.pl().getApi().format().formatText("&7Clicking this option will"));
            lore.add(Arconix.pl().getApi().format().formatText("&7allow you to set a link"));
            lore.add(Arconix.pl().getApi().format().formatText("&7that players will receive"));
            lore.add(Arconix.pl().getApi().format().formatText("&7when attempting to purchase"));
            lore.add(Arconix.pl().getApi().format().formatText("&7this kit."));
            allmeta.setLore(lore);
            alli.setItemMeta(allmeta);

            i.setItem(13, alli);

            alli = new ItemStack(Material.DIAMOND_HELMET);
            allmeta = alli.getItemMeta();
            allmeta.setDisplayName(Arconix.pl().getApi().format().formatText("&a&lSet kit price"));
            lore = new ArrayList<>();
            if (kit.getPrice() != 0)
                lore.add(Arconix.pl().getApi().format().formatText("&7Currently: &a$" + Arconix.pl().getApi().format().formatEconomy(kit.getPrice()) + "&7."));
            else
                lore.add(Arconix.pl().getApi().format().formatText("&7Currently: &cNot set&7."));
            lore.add(Arconix.pl().getApi().format().formatText(""));
            lore.add(Arconix.pl().getApi().format().formatText("&7Clicking this option will"));
            lore.add(Arconix.pl().getApi().format().formatText("&7allow you to set a price"));
            lore.add(Arconix.pl().getApi().format().formatText("&7that players will be able to"));
            lore.add(Arconix.pl().getApi().format().formatText("&7purchase this kit for"));
            lore.add(Arconix.pl().getApi().format().formatText("&7requires &aVault&7."));
            allmeta.setLore(lore);
            alli.setItemMeta(allmeta);

            i.setItem(15, alli);

            player.openInventory(i);
            playerData.setEditorType(KitEditorPlayerData.EditorType.SELLING);
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public void gui(Player player) {
        try {
            KitEditorPlayerData playerData = getDataFor(player);
            Kit kit = playerData.getKit();

            Inventory i = Bukkit.createInventory(null, 27, Arconix.pl().getApi().format().formatTitle("&8GUI Options for &a" + kit.getShowableName() + "&8."));

            Methods.fillGlass(i);

            i.setItem(0, Methods.getBackgroundGlass(true));
            i.setItem(1, Methods.getBackgroundGlass(true));
            i.setItem(2, Methods.getBackgroundGlass(false));
            i.setItem(6, Methods.getBackgroundGlass(false));
            i.setItem(7, Methods.getBackgroundGlass(true));
            i.setItem(8, Methods.getBackgroundGlass(true));
            i.setItem(9, Methods.getBackgroundGlass(true));
            i.setItem(10, Methods.getBackgroundGlass(false));
            i.setItem(16, Methods.getBackgroundGlass(false));
            i.setItem(17, Methods.getBackgroundGlass(true));
            i.setItem(18, Methods.getBackgroundGlass(true));
            i.setItem(19, Methods.getBackgroundGlass(true));
            i.setItem(20, Methods.getBackgroundGlass(false));
            i.setItem(24, Methods.getBackgroundGlass(false));
            i.setItem(25, Methods.getBackgroundGlass(true));
            i.setItem(26, Methods.getBackgroundGlass(true));

            ItemStack exit = new ItemStack(Material.valueOf(instance.getConfig().getString("Interfaces.Exit Icon")), 1);
            ItemMeta exitmeta = exit.getItemMeta();
            exitmeta.setDisplayName(Lang.EXIT.getConfigValue());
            exit.setItemMeta(exitmeta);

            ItemStack head2 = new ItemStack(Material.PLAYER_HEAD, 1, (byte) 3);
            ItemStack back = head2;
            back = Arconix.pl().getApi().getGUI().addTexture(head2, "http://textures.minecraft.net/texture/3ebf907494a935e955bfcadab81beafb90fb9be49c7026ba97d798d5f1a23");
            SkullMeta skull2Meta = (SkullMeta) back.getItemMeta();
            back.setDurability((short) 3);
            skull2Meta.setDisplayName(Lang.BACK.getConfigValue());
            back.setItemMeta(skull2Meta);

            i.setItem(0, back);
            i.setItem(8, exit);

            ItemStack alli = new ItemStack(Material.DIAMOND_HELMET);
            ItemMeta allmeta = alli.getItemMeta();
            allmeta.setDisplayName(Arconix.pl().getApi().format().formatText("&9&lSet Title"));
            ArrayList<String> lore = new ArrayList<>();
            if (kit.getTitle() != null)
                lore.add(Arconix.pl().getApi().format().formatText("&7Currently: &a" + kit.getTitle() + "&7."));
            else
                lore.add(Arconix.pl().getApi().format().formatText("&7Currently: &cNot set&7."));
            lore.add(Arconix.pl().getApi().format().formatText(""));
            lore.add(Arconix.pl().getApi().format().formatText("&7Left-Click: &9to set"));
            lore.add(Arconix.pl().getApi().format().formatText("&9the kit title for holograms"));
            lore.add(Arconix.pl().getApi().format().formatText("&9and the kit / kit GUIs."));
            lore.add(Arconix.pl().getApi().format().formatText(""));
            lore.add(Arconix.pl().getApi().format().formatText("&7Right-Click: &9to reset."));
            allmeta.setLore(lore);

            allmeta.setLore(lore);
            alli.setItemMeta(allmeta);

            i.setItem(11, alli);

            alli = new ItemStack(Material.BEACON);
            if (kit.getDisplayItem() != null) {
                alli.setType(kit.getDisplayItem());
            }
            allmeta = alli.getItemMeta();
            allmeta.setDisplayName(Arconix.pl().getApi().format().formatText("&9&lSet DisplayItem"));
            lore = new ArrayList<>();
            if (kit.getDisplayItem() != null) {
                lore.add(Arconix.pl().getApi().format().formatText("&7Currently set to: &a" + kit.getDisplayItem().toString() + "&7."));
            } else {
                lore.add(Arconix.pl().getApi().format().formatText("&7Currently &cDisabled&7."));
            }
            lore.add("");
            lore.add(Arconix.pl().getApi().format().formatText("&7Right-Click to: &9Set a"));
            lore.add(Arconix.pl().getApi().format().formatText("&9display item for this kit"));
            lore.add(Arconix.pl().getApi().format().formatText("&9to the item in your hand."));
            lore.add("");
            lore.add(Arconix.pl().getApi().format().formatText("&7Left-Click to: &9Remove the item."));
            allmeta.setLore(lore);
            alli.setItemMeta(allmeta);

            i.setItem(13, alli);

            alli = new ItemStack(Material.COAL);
            allmeta = alli.getItemMeta();
            allmeta.setDisplayName(Arconix.pl().getApi().format().formatText("&9&lHide kit"));
            lore = new ArrayList<>();
            if (kit.isHidden()) {
                lore.add(Arconix.pl().getApi().format().formatText("&7Currently: &cHidden&7."));
            } else {
                lore.add(Arconix.pl().getApi().format().formatText("&7Currently: &aVisible&7."));
            }
            lore.add("");
            lore.add(Arconix.pl().getApi().format().formatText("&7A hidden kit will not"));
            lore.add(Arconix.pl().getApi().format().formatText("&7show up in the /kit gui."));
            lore.add(Arconix.pl().getApi().format().formatText("&7This is usually optimal for"));
            lore.add(Arconix.pl().getApi().format().formatText("&7preventing players from seeing"));
            lore.add(Arconix.pl().getApi().format().formatText("&7non obtainable kit or starter kit."));
            allmeta.setLore(lore);
            alli.setItemMeta(allmeta);

            i.setItem(15, alli);

            player.openInventory(i);
            playerData.setEditorType(KitEditorPlayerData.EditorType.GUI);
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public void general(Player player) {
        try {
            KitEditorPlayerData playerData = getDataFor(player);
            Kit kit = playerData.getKit();

            Inventory i = Bukkit.createInventory(null, 27, Arconix.pl().getApi().format().formatTitle("&8General Options for &a" + kit.getShowableName() + "&8."));

            Methods.fillGlass(i);

            i.setItem(0, Methods.getBackgroundGlass(true));
            i.setItem(1, Methods.getBackgroundGlass(true));
            i.setItem(2, Methods.getBackgroundGlass(false));
            i.setItem(6, Methods.getBackgroundGlass(false));
            i.setItem(7, Methods.getBackgroundGlass(true));
            i.setItem(8, Methods.getBackgroundGlass(true));
            i.setItem(9, Methods.getBackgroundGlass(true));
            i.setItem(10, Methods.getBackgroundGlass(false));
            i.setItem(16, Methods.getBackgroundGlass(false));
            i.setItem(17, Methods.getBackgroundGlass(true));
            i.setItem(18, Methods.getBackgroundGlass(true));
            i.setItem(19, Methods.getBackgroundGlass(true));
            i.setItem(20, Methods.getBackgroundGlass(false));
            i.setItem(24, Methods.getBackgroundGlass(false));
            i.setItem(25, Methods.getBackgroundGlass(true));
            i.setItem(26, Methods.getBackgroundGlass(true));

            ItemStack exit = new ItemStack(Material.valueOf(instance.getConfig().getString("Interfaces.Exit Icon")), 1);
            ItemMeta exitmeta = exit.getItemMeta();
            exitmeta.setDisplayName(Lang.EXIT.getConfigValue());
            exit.setItemMeta(exitmeta);

            ItemStack head2 = new ItemStack(Material.PLAYER_HEAD, 1, (byte) 3);
            ItemStack back = head2;
            back = Arconix.pl().getApi().getGUI().addTexture(head2, "http://textures.minecraft.net/texture/3ebf907494a935e955bfcadab81beafb90fb9be49c7026ba97d798d5f1a23");
            SkullMeta skull2Meta = (SkullMeta) back.getItemMeta();
            back.setDurability((short) 3);
            skull2Meta.setDisplayName(Lang.BACK.getConfigValue());
            back.setItemMeta(skull2Meta);

            i.setItem(0, back);
            i.setItem(8, exit);

            ItemStack alli = new ItemStack(Material.DIAMOND_HELMET);
            ItemMeta allmeta = alli.getItemMeta();
            allmeta.setDisplayName(Arconix.pl().getApi().format().formatText("&9&lSet Title"));
            ArrayList<String> lore = new ArrayList<>();
            if (kit.getTitle() != null)
                lore.add(Arconix.pl().getApi().format().formatText("&7Currently: &a" + kit.getDelay() + "&7."));
            else
                lore.add(Arconix.pl().getApi().format().formatText("&7Currently: &cNot set&7."));
            lore.add(Arconix.pl().getApi().format().formatText(""));
            lore.add(Arconix.pl().getApi().format().formatText("&7Left-Click: &9to set"));
            lore.add(Arconix.pl().getApi().format().formatText("&9the kit title for holograms"));
            lore.add(Arconix.pl().getApi().format().formatText("&9and the kit / kit GUIs."));
            lore.add(Arconix.pl().getApi().format().formatText(""));
            lore.add(Arconix.pl().getApi().format().formatText("&7Right-Click: &9to reset."));
            allmeta.setLore(lore);

            allmeta.setLore(lore);
            alli.setItemMeta(allmeta);

            /*i.setItem(11, alli);*/

            alli = new ItemStack(Material.CLOCK);
            allmeta = alli.getItemMeta();
            allmeta.setDisplayName(Arconix.pl().getApi().format().formatText("&9&lChange Delay"));
            lore = new ArrayList<>();
            lore.add(Arconix.pl().getApi().format().formatText("&7Currently set to: &a" + kit.getDelay() + "&7."));
            lore.add("");
            lore.add(Arconix.pl().getApi().format().formatText("&7Use this to alter this kit delay."));
            lore.add("");
            lore.add(Arconix.pl().getApi().format().formatText("&7Use &6-1 &7to make this kit single"));
            lore.add(Arconix.pl().getApi().format().formatText("&7use only."));
            allmeta.setLore(lore);
            alli.setItemMeta(allmeta);

            i.setItem(13, alli);

            alli = new ItemStack(Material.TNT);
            allmeta = alli.getItemMeta();
            allmeta.setDisplayName(Arconix.pl().getApi().format().formatText("&c&lDestroy Kit"));
            lore = new ArrayList<>();
            lore.add("");
            lore.add(Arconix.pl().getApi().format().formatText("&7Click this to destroy this kit."));
            allmeta.setLore(lore);
            alli.setItemMeta(allmeta);

            i.setItem(15, alli);

            player.openInventory(i);
            playerData.setEditorType(KitEditorPlayerData.EditorType.GENERAL);
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public void setKitsDisplayItem(Player player, boolean type) {
        try {
            KitEditorPlayerData playerData = getDataFor(player);
            Kit kit = playerData.getKit();
            if (type) {
                ItemStack is = player.getItemInHand();
                if (is == null || is.getType() == Material.AIR) {
                    player.sendMessage(Arconix.pl().getApi().format().formatText(instance.getReferences().getPrefix() + "&8You must be holding an item to use this function."));
                    return;
                }
                kit.setDisplayItem(is.getType());
                player.sendMessage(Arconix.pl().getApi().format().formatText(instance.getReferences().getPrefix() + "&8Custom Item Display set for kit &a" + kit.getShowableName() + "&8."));
            } else {
                kit.setDisplayItem(null);
                player.sendMessage(Arconix.pl().getApi().format().formatText(instance.getReferences().getPrefix() + "&8Custom Item Display removed from kit &a" + kit.getShowableName() + "&8."));
            }
            gui(player);
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public void createCommand(Player player) {
        try {
            KitEditorPlayerData playerData = getDataFor(player);

            Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
                if (playerData.getEditorType() == KitEditorPlayerData.EditorType.COMMAND) {
                    player.sendMessage(Arconix.pl().getApi().format().formatText(instance.getReferences().getPrefix() + "Editing Timed out."));
                    playerData.setEditorType(KitEditorPlayerData.EditorType.NOTIN);
                }
            }, 500L);
            player.closeInventory();

            playerData.setEditorType(KitEditorPlayerData.EditorType.COMMAND);

            player.sendMessage("");
            player.sendMessage(Arconix.pl().getApi().format().formatText("Please type a command. Example: &aeco give {player} 1000"));
            player.sendMessage(Arconix.pl().getApi().format().formatText("do not include a &a/"));
            player.sendMessage("");
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public void changeAnimation(Player player) {
        KitEditorPlayerData playerData = getDataFor(player);

        Kit kit = playerData.getKit();
        if (kit.getKitAnimation() == KitAnimation.NONE) {
            kit.setKitAnimation(KitAnimation.ROULETTE);
        } else {
            kit.setKitAnimation(KitAnimation.NONE);
        }

        getInvItems(player, playerData);

    }

    public void createMoney(Player player) {
        try {
            KitEditorPlayerData playerData = getDataFor(player);

            Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
                if (playerData.getEditorType() == KitEditorPlayerData.EditorType.MONEY) {
                    player.sendMessage(Arconix.pl().getApi().format().formatText(instance.getReferences().getPrefix() + "Editing Timed out."));
                    playerData.setEditorType(KitEditorPlayerData.EditorType.NOTIN);
                }
            }, 500L);
            player.closeInventory();

            playerData.setEditorType(KitEditorPlayerData.EditorType.MONEY);

            player.sendMessage("");
            player.sendMessage(Arconix.pl().getApi().format().formatText("Please type a dollar amount. Example: &a10000"));
            player.sendMessage(Arconix.pl().getApi().format().formatText("do not include a &a$"));
            player.sendMessage("");
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public void saveKit(Player player, Inventory i) {
        try {
            KitEditorPlayerData playerData = getDataFor(player);
            Kit kit = playerData.getKit();

            ItemStack[] items = i.getContents();
            int num = 0;
            for (ItemStack item : items) {
                if (num < 10 || num == 17 || num == 36) {
                    items[num] = null;
                }
                num++;
            }

            items = Arrays.copyOf(items, items.length - 10);

            kit.saveKit(Arrays.asList(items));
            if (!playerData.isMuteSave())
                player.sendMessage(Arconix.pl().getApi().format().formatText(instance.getReferences().getPrefix() + "&8Changes to &a" + kit.getShowableName() + " &8saved successfully."));

            playerData.setMuteSave(false);
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public void hide(Player player) {
        Kit kit = getDataFor(player).getKit();
        try {
            if (kit.isHidden()) {
                kit.setHidden(false);
            } else {
                kit.setHidden(true);
            }
            gui(player);
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public void setNoSale(Player player) {
        try {
            Kit kit = getDataFor(player).getKit();
            kit.setPrice(0);
            kit.setLink(null);
            instance.getHologramHandler().updateHolograms();
            selling(player);
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public void setDelay(Player player) {
        try {
            KitEditorPlayerData playerData = getDataFor(player);

            player.closeInventory();
            Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
                if (playerData.getEditorType() == KitEditorPlayerData.EditorType.DELAY) {
                    player.sendMessage(Arconix.pl().getApi().format().formatText(instance.getReferences().getPrefix() + "Editing Timed out."));
                    playerData.setEditorType(KitEditorPlayerData.EditorType.NOTIN);
                }
            }, 200L);

            playerData.setEditorType(KitEditorPlayerData.EditorType.DELAY);

            player.sendMessage("");
            player.sendMessage(Arconix.pl().getApi().format().formatText("Type a delay in seconds for this kit. Example: 10"));
            player.sendMessage("");
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public void setTitle(Player player, boolean type) {
        try {
            KitEditorPlayerData playerData = getDataFor(player);
            Kit kit = playerData.getKit();

            if (type) {
                player.closeInventory();
                Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
                    if (playerData.getEditorType() == KitEditorPlayerData.EditorType.TITLE) {
                        player.sendMessage(Arconix.pl().getApi().format().formatText(instance.getReferences().getPrefix() + "Editing Timed out."));
                        playerData.setEditorType(KitEditorPlayerData.EditorType.NOTIN);
                    }
                }, 200L);

                playerData.setEditorType(KitEditorPlayerData.EditorType.TITLE);

                player.sendMessage("");
                player.sendMessage(Arconix.pl().getApi().format().formatText("Type a title for the GUI. Example: &aThe Cool Kids Kit"));
                player.sendMessage("");
            } else {
                instance.getConfig().set("data.kit." + kit.getName() + ".title", null);
                instance.saveConfig();
                instance.getHologramHandler().updateHolograms();
                gui(player);
            }
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public void editPrice(Player player) {
        try {
            KitEditorPlayerData playerData = getDataFor(player);

            Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
                if (playerData.getEditorType() == KitEditorPlayerData.EditorType.PRICE) {
                    player.sendMessage(Arconix.pl().getApi().format().formatText(instance.getReferences().getPrefix() + "Editing Timed out."));
                    playerData.setEditorType(KitEditorPlayerData.EditorType.NOTIN);
                }
            }, 200L);
            player.closeInventory();

            playerData.setEditorType(KitEditorPlayerData.EditorType.PRICE);

            player.sendMessage("");
            player.sendMessage(Arconix.pl().getApi().format().formatText("Please type a price. Example: &a50000"));
            player.sendMessage(Arconix.pl().getApi().format().formatText("&cUse only numbers."));
            player.sendMessage("");
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public void editLink(Player player) {
        try {
            KitEditorPlayerData playerData = getDataFor(player);

            Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
                if (playerData.getEditorType() == KitEditorPlayerData.EditorType.LINK) {
                    player.sendMessage(Arconix.pl().getApi().format().formatText(instance.getReferences().getPrefix() + "Editing Timed out."));
                    playerData.setEditorType(KitEditorPlayerData.EditorType.NOTIN);
                }
            }, 200L);
            player.closeInventory();

            playerData.setEditorType(KitEditorPlayerData.EditorType.LINK);

            player.sendMessage("");
            player.sendMessage(Arconix.pl().getApi().format().formatText("Please type a link. Example: &ahttp://buy.viscernity.com/"));
            player.sendMessage("");
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public void editDisplayItem(Player player) {
        try {
            KitEditorPlayerData playerData = getDataFor(player);

            Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
                if (playerData.getEditorType() == KitEditorPlayerData.EditorType.LINK) {
                    player.sendMessage(Arconix.pl().getApi().format().formatText(instance.getReferences().getPrefix() + "Editing Timed out."));
                    playerData.setEditorType(KitEditorPlayerData.EditorType.NOTIN);
                }
            }, 200L);
            player.closeInventory();

            playerData.setEditorType(KitEditorPlayerData.EditorType.DISPLAY_ITEM);

            player.sendMessage("");
            player.sendMessage(Arconix.pl().getApi().format().formatText("Please type a material. Example: &aStone"));
            player.sendMessage("");
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public void editDisplayName(Player player) {
        try {
            KitEditorPlayerData playerData = getDataFor(player);

            Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
                if (playerData.getEditorType() == KitEditorPlayerData.EditorType.LINK) {
                    player.sendMessage(Arconix.pl().getApi().format().formatText(instance.getReferences().getPrefix() + "Editing Timed out."));
                    playerData.setEditorType(KitEditorPlayerData.EditorType.NOTIN);
                }
            }, 200L);
            player.closeInventory();

            playerData.setEditorType(KitEditorPlayerData.EditorType.DISPLAY_NAME);

            player.sendMessage("");
            player.sendMessage(Arconix.pl().getApi().format().formatText("Please type a name. Example: &aCool Item"));
            player.sendMessage("");
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public void editDisplayLore(Player player) {
        try {
            KitEditorPlayerData playerData = getDataFor(player);

            Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
                if (playerData.getEditorType() == KitEditorPlayerData.EditorType.LINK) {
                    player.sendMessage(Arconix.pl().getApi().format().formatText(instance.getReferences().getPrefix() + "Editing Timed out."));
                    playerData.setEditorType(KitEditorPlayerData.EditorType.NOTIN);
                }
            }, 200L);
            player.closeInventory();

            playerData.setEditorType(KitEditorPlayerData.EditorType.DISPLAY_LORE);

            player.sendMessage("");
            player.sendMessage(Arconix.pl().getApi().format().formatText("Please type in lore. Example: &aCool Lore"));
            player.sendMessage("");
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public KitEditorPlayerData getDataFor(Player player) {
        return editorPlayerData.computeIfAbsent(player.getUniqueId(), uuid -> new KitEditorPlayerData());
    }

    public void removeFromInstance(Player player) {
        editorPlayerData.remove(player);
    }

    public enum Action {NONE, CHANCE, DISPLAY_ITEM, DISPLAY_NAME, DISPLAY_LORE}
}
