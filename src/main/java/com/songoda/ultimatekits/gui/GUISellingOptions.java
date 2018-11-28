package com.songoda.ultimatekits.gui;

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

public class GUISellingOptions extends AbstractGUI {

    private Kit kit;
    private Player player;
    private UltimateKits plugin;
    private AbstractGUI back;

    public GUISellingOptions(UltimateKits plugin, Player player, AbstractGUI back, Kit kit) {
        super(player);
        this.kit = kit;
        this.player = player;
        this.plugin = plugin;
        this.back = back;
        init("&8Selling Options for &a" + kit.getShowableName() + "&8.", 27);
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
        if (kit.getPrice() != 0 ||
                kit.getLink() != null)
            lore.add(Arconix.pl().getApi().format().formatText("&7Currently &aFor Sale&7."));
        else
            lore.add(Arconix.pl().getApi().format().formatText("&7Currently &cNot For Sale&7."));
        lore.add(Arconix.pl().getApi().format().formatText(""));
        lore.add(Arconix.pl().getApi().format().formatText("&7Clicking this option will"));
        lore.add(Arconix.pl().getApi().format().formatText("&7remove this kit from sale."));

        createButton(11, Material.BARRIER, "&c&lSet not for sale", lore);

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

        createButton(13, Material.PAPER, "&a&lSet kit link", lore);

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

        createButton(15, Material.SUNFLOWER, "&a&lSet kit price", lore);
    }

    @Override
    protected void registerClickables() {
        registerClickable(0, (player, inventory, cursor, slot, type) -> back.init(back.getInventory().getTitle(), back.getInventory().getSize()));

        registerClickable(8, (player, inventory, cursor, slot, type) -> player.closeInventory());

        registerClickable(15, ((player1, inventory1, cursor, slot, type) -> {
            AbstractAnvilGUI gui = new AbstractAnvilGUI(player, event -> {
                String msg = event.getName();

                if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
                    player.sendMessage(plugin.getReferences().getPrefix() + Arconix.pl().getApi().format().formatText("&8You must have &aVault &8installed to utilize economy.."));
                } else if (!Arconix.pl().getApi().doMath().isNumeric(msg)) {
                    player.sendMessage(Arconix.pl().getApi().format().formatText("&a" + msg + " &8is not a number. Please do not include a &a$&8."));
                } else {

                    if (kit.getLink() != null) {
                        kit.setLink(null);
                        player.sendMessage(Arconix.pl().getApi().format().formatText(plugin.getReferences().getPrefix() + "&8LINK has been removed from this kit. Note you cannot have ECO & LINK set at the same time.."));
                    }
                    Double eco = Double.parseDouble(msg);
                    kit.setPrice(eco);
                    plugin.getHologramHandler().updateHolograms();
                }
            });

            gui.setOnClose((player2, inventory3) -> init(inventory.getTitle(), inventory.getSize()));

            ItemStack item = new ItemStack(Material.SUNFLOWER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("Enter Price (No $)");
            item.setItemMeta(meta);

            gui.setSlot(AbstractAnvilGUI.AnvilSlot.INPUT_LEFT, item);
            gui.open();
        }));

        registerClickable(13, ((player1, inventory1, cursor, slot, type) -> {
            AbstractAnvilGUI gui = new AbstractAnvilGUI(player, event -> {
                String msg = event.getName();

                if (kit.getPrice() != 0) {
                    kit.setPrice(0);
                    player.sendMessage(Arconix.pl().getApi().format().formatText(plugin.getReferences().getPrefix() + "&8ECO has been removed from this kit. Note you cannot have ECO & LINK set at the same time.."));
                }
                kit.setLink(msg);
                plugin.getHologramHandler().updateHolograms();
            });

            gui.setOnClose((player2, inventory3) -> init(inventory.getTitle(), inventory.getSize()));

            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("Enter Link");
            item.setItemMeta(meta);

            gui.setSlot(AbstractAnvilGUI.AnvilSlot.INPUT_LEFT, item);
            gui.open();
        }));

        registerClickable(11, ((player1, inventory1, cursor, slot, type) -> {
            kit.setPrice(0);
            kit.setLink(null);
            constructGUI();
        }));
    }

    @Override
    protected void registerOnCloses() {

    }

}
