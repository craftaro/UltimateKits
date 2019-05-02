package com.songoda.ultimatekits.gui;

import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.utils.Methods;
import com.songoda.ultimatekits.utils.ServerVersion;
import com.songoda.ultimatekits.utils.gui.AbstractAnvilGUI;
import com.songoda.ultimatekits.utils.gui.AbstractGUI;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class GUIGeneralOptions extends AbstractGUI {

    private Kit kit;
    private Player player;
    private UltimateKits plugin;
    private AbstractGUI back;

    public GUIGeneralOptions(UltimateKits plugin, Player player, AbstractGUI back, Kit kit) {
        super(player);
        this.kit = kit;
        this.player = player;
        this.plugin = plugin;
        this.back = back;
        init("&8General Options for &a" + kit.getShowableName() + "&8.", 27);
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
                UltimateKits.getInstance().getLocale().getMessage("interface.button.exit"));

        ItemStack head = new ItemStack(plugin.isServerVersionAtLeast(ServerVersion.V1_13) ? Material.PLAYER_HEAD : Material.valueOf("SKULL_ITEM"), 1, (byte) 3);
        ItemStack back = Methods.addTexture(head, "http://textures.minecraft.net/texture/3ebf907494a935e955bfcadab81beafb90fb9be49c7026ba97d798d5f1a23");
        SkullMeta skull2Meta = (SkullMeta) back.getItemMeta();
        back.setDurability((short) 3);
        skull2Meta.setDisplayName(UltimateKits.getInstance().getLocale().getMessage("interface.button.back"));
        back.setItemMeta(skull2Meta);

        inventory.setItem(0, back);

        createButton(11, plugin.getInstance().isServerVersionAtLeast(ServerVersion.V1_13) ? Material.CLOCK : Material.valueOf("WATCH"), "&9&lChange Delay",
                "&7Currently set to: &a" + kit.getDelay() + "&7.",
                "",
                "&7Use this to alter this kit delay.",
                "",
                "&7Use &6-1 &7to make this kit single",
                "&7use only.");

        createButton(15, Material.TNT, "&c&lDestroy Kit",
                "",
                "&7Click this to destroy this kit.");
    }

    @Override
    protected void registerClickables() {
        registerClickable(0, (player, inventory, cursor, slot, type) -> back.init(back.getSetTitle(), back.getInventory().getSize()));

        registerClickable(8, (player, inventory, cursor, slot, type) -> player.closeInventory());

        registerClickable(15, ((player1, inventory1, cursor, slot, type) -> {
            String name = kit.getName();
            AbstractAnvilGUI gui = new AbstractAnvilGUI(player, event -> {
                String msg = event.getName();

                if (msg.trim().equalsIgnoreCase(kit.getName())) {
                    plugin.getKitManager().removeKit(kit);
                    if (plugin.getHologram() != null)
                        plugin.getHologram().update(kit);
                    player.sendMessage(plugin.getReferences().getPrefix() + Methods.formatText("&cKit destroyed successfully."));
                } else {
                    player.sendMessage(plugin.getReferences().getPrefix() + Methods.formatText("&cKit was not Destroyed."));
                }
            });

            gui.setOnClose((player2, inventory3) -> {
                if (plugin.getKitManager().getKit(name) != null) {
                    init(setTitle, inventory.getSize());
                }
            });

            ItemStack item = new ItemStack(Material.BARRIER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("Enter \"" + kit.getName() + "\"");
            item.setItemMeta(meta);

            gui.setSlot(AbstractAnvilGUI.AnvilSlot.INPUT_LEFT, item);
            gui.open();

            gui.setCloseSound(plugin.isServerVersionAtLeast(ServerVersion.V1_13) ? Sound.ENTITY_GENERIC_EXPLODE : Sound.valueOf("EXPLODE"));
        }));

        registerClickable(11, ((player1, inventory1, cursor, slot, type) -> {
            AbstractAnvilGUI gui = new AbstractAnvilGUI(player, event -> {
                String msg = event.getName();

                if (!Methods.isNumeric(msg)) {
                    player.sendMessage(Methods.formatText("&a" + msg + " &8is not a number. Please do not include a &a$&8."));
                } else {
                    kit.setDelay(Integer.parseInt(msg));
                }
            });

            gui.setOnClose((player2, inventory3) -> init(setTitle, inventory.getSize()));

            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("Delay In Seconds");
            item.setItemMeta(meta);

            gui.setSlot(AbstractAnvilGUI.AnvilSlot.INPUT_LEFT, item);
            gui.open();
        }));
    }

    @Override
    protected void registerOnCloses() {

    }

}
