package com.songoda.ultimatekits.gui;

import com.songoda.arconix.api.methods.formatting.TextComponent;
import com.songoda.arconix.plugin.Arconix;
import com.songoda.ultimatekits.Lang;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.utils.Methods;
import com.songoda.ultimatekits.utils.gui.AbstractAnvilGUI;
import com.songoda.ultimatekits.utils.gui.AbstractGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;

public class GUIGUIOptions extends AbstractGUI {

    private Kit kit;
    private Player player;
    private UltimateKits plugin;
    private AbstractGUI back;

    public GUIGUIOptions(UltimateKits plugin, Player player, AbstractGUI back, Kit kit) {
        super(player);
        this.kit = kit;
        this.player = player;
        this.plugin = plugin;
        this.back = back;
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
                Lang.EXIT.getConfigValue());

        ItemStack head2 = new ItemStack(Material.PLAYER_HEAD, 1, (byte) 3);
        ItemStack back = head2;
        back = Arconix.pl().getApi().getGUI().addTexture(head2, "http://textures.minecraft.net/texture/3ebf907494a935e955bfcadab81beafb90fb9be49c7026ba97d798d5f1a23");
        SkullMeta skull2Meta = (SkullMeta) back.getItemMeta();
        back.setDurability((short) 3);
        skull2Meta.setDisplayName(Lang.BACK.getConfigValue());
        back.setItemMeta(skull2Meta);

        inventory.setItem(0, back);

        ArrayList<String> lore = new ArrayList<>();
        if (kit.getTitle() != null)
            lore.add(TextComponent.formatText("&7Currently: &a" + kit.getTitle() + "&7."));
        else
            lore.add(TextComponent.formatText("&7Currently: &cNot set&7."));
        lore.add(TextComponent.formatText(""));
        lore.add(TextComponent.formatText("&7Left-Click: &9to set"));
        lore.add(TextComponent.formatText("&9the kit title for holograms"));
        lore.add(TextComponent.formatText("&9and the kit / kit GUIs."));
        lore.add(TextComponent.formatText(""));
        lore.add(TextComponent.formatText("&7Right-Click: &9to reset."));

        createButton(11, Material.NAME_TAG, "&9&lSet Title", lore);

        lore = new ArrayList<>();
        if (kit.getDisplayItem() != null) {
            lore.add(TextComponent.formatText("&7Currently set to: &a" + kit.getDisplayItem().toString() + "&7."));
        } else {
            lore.add(TextComponent.formatText("&7Currently &cDisabled&7."));
        }
        lore.add("");
        lore.add(TextComponent.formatText("&7Left-Click to: &9Set a"));
        lore.add(TextComponent.formatText("&9display item for this kit"));
        lore.add(TextComponent.formatText("&9to the item in your hand."));
        lore.add("");
        lore.add(TextComponent.formatText("&7Right-Click to: &9Remove the item."));

        createButton(13, Material.BEACON, "&9&lSet DisplayItem", lore);

        lore = new ArrayList<>();
        if (kit.isHidden()) {
            lore.add(TextComponent.formatText("&7Currently: &cHidden&7."));
        } else {
            lore.add(TextComponent.formatText("&7Currently: &aVisible&7."));
        }
        lore.add("");
        lore.add(TextComponent.formatText("&7A hidden kit will not"));
        lore.add(TextComponent.formatText("&7show up in the /kit gui."));
        lore.add(TextComponent.formatText("&7This is usually optimal for"));
        lore.add(TextComponent.formatText("&7preventing players from seeing"));
        lore.add(TextComponent.formatText("&7non obtainable kit or starter kit."));

        createButton(15, Material.COAL, "&9&lHide kit", lore);
    }

    @Override
    protected void registerClickables() {
        registerClickable(0, (player, inventory, cursor, slot, type) -> back.init(back.getInventory().getTitle(), back.getInventory().getSize()));

        registerClickable(8, (player, inventory, cursor, slot, type) -> player.closeInventory());

        registerClickable(11, ((player1, inventory1, cursor, slot, type) -> {
            if (type.isLeftClick()) {
                AbstractAnvilGUI gui = new AbstractAnvilGUI(player, event -> {
                    String msg = event.getName();
                    kit.setTitle(msg);
                    plugin.saveConfig();
                    player.sendMessage(TextComponent.formatText(plugin.getReferences().getPrefix() + "&8Title &5" + msg + "&8 added to Kit &a" + kit.getShowableName() + "&8."));
                    plugin.getHologramHandler().updateHolograms();
                });

                gui.setOnClose((player2, inventory3) -> init(inventory.getTitle(), inventory.getSize()));

                ItemStack item = new ItemStack(Material.NAME_TAG);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName("Enter Title");
                item.setItemMeta(meta);

                gui.setSlot(AbstractAnvilGUI.AnvilSlot.INPUT_LEFT, item);
                gui.open();
            } else {
                kit.setTitle("");
                plugin.getHologramHandler().updateHolograms();
                constructGUI();
            }
        }));

        registerClickable(13, ((player1, inventory1, cursor, slot, type) -> {
            if (type.isLeftClick()) {
                ItemStack is = player.getItemInHand();
                if (is == null || is.getType() == Material.AIR) {
                    player.sendMessage(TextComponent.formatText(plugin.getReferences().getPrefix() + "&8You must be holding an item to use this function."));
                    return;
                }
                kit.setDisplayItem(is.getType());
                player.sendMessage(TextComponent.formatText(plugin.getReferences().getPrefix() + "&8Custom Item Display set for kit &a" + kit.getShowableName() + "&8."));
            } else {
                kit.setDisplayItem(null);
                player.sendMessage(TextComponent.formatText(plugin.getReferences().getPrefix() + "&8Custom Item Display removed from kit &a" + kit.getShowableName() + "&8."));
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
