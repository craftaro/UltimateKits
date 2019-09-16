package com.songoda.ultimatekits.gui;

import com.songoda.core.gui.Gui;
import com.songoda.core.utils.ItemUtils;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.utils.Methods;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;

public class GUIGUIOptions extends Gui {

    private Kit kit;
    private Player player;
    private UltimateKits plugin;

    public GUIGUIOptions(UltimateKits plugin, Player player, Gui back, Kit kit) {
        super(back);
        this.kit = kit;
        this.player = player;
        this.plugin = plugin;
        init("&8GUI Options for &a" + kit.getShowableName() + "&8.", 27);
    }

    @Override
    protected void constructGUI() {
        Methods.fillGlass(inventory);

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

        createButton(8, Material.valueOf(UltimateKits.getInstance().getConfig().getString("Interfaces.Exit Icon")),
                UltimateKits.getInstance().getLocale().getMessage("interface.button.exit").getMessage());

        ItemStack back = ItemUtils.getCustomHead("3ebf907494a935e955bfcadab81beafb90fb9be49c7026ba97d798d5f1a23");
        SkullMeta skull2Meta = (SkullMeta) back.getItemMeta();
        back.setDurability((short) 3);
        skull2Meta.setDisplayName(UltimateKits.getInstance().getLocale().getMessage("interface.button.back").getMessage());
        back.setItemMeta(skull2Meta);

        inventory.setItem(0, back);

        ArrayList<String> lore = new ArrayList<>();
        if (kit.getTitle() != null)
            lore.add(Methods.formatText("&7Currently: &a" + kit.getTitle() + "&7."));
        else
            lore.add(Methods.formatText("&7Currently: &cNot set&7."));
        lore.add(Methods.formatText(""));
        lore.add(Methods.formatText("&7Left-Click: &9to set"));
        lore.add(Methods.formatText("&9the kit title for holograms"));
        lore.add(Methods.formatText("&9and the kit / kit selector GUIs."));
        lore.add(Methods.formatText(""));
        lore.add(Methods.formatText("&7Right-Click: &9to reset."));

        createButton(11, Material.NAME_TAG, "&9&lSet Title", lore);

        lore = new ArrayList<>();
        if (kit.getDisplayItem() != null) {
            lore.add(Methods.formatText("&7Currently set to: &a" + kit.getDisplayItem().toString() + "&7."));
        } else {
            lore.add(Methods.formatText("&7Currently &cDisabled&7."));
        }
        lore.add("");
        lore.add(Methods.formatText("&7Left-Click to: &9Set a"));
        lore.add(Methods.formatText("&9display item for this kit"));
        lore.add(Methods.formatText("&9to the item in your hand."));
        lore.add("");
        lore.add(Methods.formatText("&7Right-Click to: &9Remove the item."));

        createButton(13, Material.BEACON, "&9&lSet DisplayItem", lore);

        lore = new ArrayList<>();
        if (kit.isHidden()) {
            lore.add(Methods.formatText("&7Currently: &cHidden&7."));
        } else {
            lore.add(Methods.formatText("&7Currently: &aVisible&7."));
        }
        lore.add("");
        lore.add(Methods.formatText("&7A hidden kit will not"));
        lore.add(Methods.formatText("&7show up in the /kit gui."));
        lore.add(Methods.formatText("&7This is usually optimal for"));
        lore.add(Methods.formatText("&7preventing players from seeing"));
        lore.add(Methods.formatText("&7non obtainable kit or starter kit."));

        createButton(15, Material.COAL, "&9&lHide kit", lore);
    }

    @Override
    protected void registerClickables() {
        registerClickable(0, (player, inventory, cursor, slot, type) -> back.init(back.getSetTitle(), back.getInventory().getSize()));

        registerClickable(8, (player, inventory, cursor, slot, type) -> player.closeInventory());

        registerClickable(11, ((player1, inventory1, cursor, slot, type) -> {
            if (type.isLeftClick()) {
                AbstractAnvilGUI gui = new AbstractAnvilGUI(player, event -> {
                    String msg = event.getName();
                    kit.setTitle(msg);
                    plugin.saveConfig();
                    plugin.getLocale().newMessage("&8Title &5" + msg + "&8 added to Kit &a" + kit.getShowableName() + "&8.")
                            .sendPrefixedMessage(player);
                    if (plugin.getHologram() != null)
                        plugin.getHologram().update(kit);
                });

                gui.setOnClose((player2, inventory3) -> init(setTitle, inventory.getSize()));

                ItemStack item = new ItemStack(Material.NAME_TAG);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName("Enter Title");
                item.setItemMeta(meta);

                gui.setSlot(AbstractAnvilGUI.AnvilSlot.INPUT_LEFT, item);
                gui.open();
            } else {
                kit.setTitle("");
                if (plugin.getHologram() != null)
                    plugin.getHologram().update(kit);
                constructGUI();
            }
        }));

        registerClickable(13, ((player1, inventory1, cursor, slot, type) -> {
            if (type.isLeftClick()) {
                ItemStack is = player.getItemInHand();
                if (is.getType() == Material.AIR) {
                    plugin.getLocale().newMessage("&8You must be holding an item to use this function.").sendPrefixedMessage(player);
                    return;
                }
                kit.setDisplayItem(is.getType());
                plugin.getLocale().newMessage("&8Custom Item Display set for kit &a" + kit.getShowableName() + "&8.").sendPrefixedMessage(player);
            } else {
                kit.setDisplayItem(null);
                plugin.getLocale().newMessage("&8Custom Item Display removed from kit &a" + kit.getShowableName() + "&8.").sendPrefixedMessage(player);
            }
            constructGUI();
        }));

        registerClickable(15, ((player1, inventory1, cursor, slot, type) -> {
            if (kit.isHidden()) {
                kit.setHidden(false);
            } else {
                kit.setHidden(true);
            }
            constructGUI();
        }));
    }

    @Override
    protected void registerOnCloses() {

    }

}
