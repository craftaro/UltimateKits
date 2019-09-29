package com.songoda.ultimatekits.gui;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.gui.AnvilGui;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.utils.ItemUtils;
import com.songoda.core.utils.TextUtils;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.kit.KitAnimation;
import com.songoda.ultimatekits.kit.KitItem;
import com.songoda.ultimatekits.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KitEditorGui extends Gui {

    private UltimateKits plugin;
    private Kit kit;
    private Player player;

    private ItemStack[] inventoryItems;

    private boolean isInFuction = false;
    private boolean isInInventory = false;

    public KitEditorGui(UltimateKits plugin, Player player, Kit kit, Gui back) {
        super(back);
        this.plugin = plugin;
        this.kit = kit;
        this.player = player;

        setDefaultItem(null);
        setRows(6);
        setTitle(plugin.getLocale().getMessage("interface.kiteditor.title")
                .processPlaceholder("name", kit.getShowableName())
                .getMessage());

        saveItemsInstance();
        getInvItems();

        setOnClose((event) -> {
            this.saveKit(player, inventory, false);
            if (!isInInventory && this.inventoryItems.length != 0) {
                player.getInventory().setContents(this.inventoryItems);
                player.updateInventory();
            }

            CompatibleSound.ENTITY_VILLAGER_YES.play(player);
        });

        ItemStack glass1 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_1.getMaterial());
        ItemStack glass2 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_2.getMaterial());
        ItemStack glass3 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_3.getMaterial());

        GuiUtils.mirrorFill(this, 0, 0, true, true, glass2);
        GuiUtils.mirrorFill(this, 0, 1, true, true, glass2);
        GuiUtils.mirrorFill(this, 1, 0, true, true, glass2);
        GuiUtils.mirrorFill(this, 0, 2, true, true, glass3);
        GuiUtils.mirrorFill(this, 0, 3, false, true, glass1);

        // exit button
        setButton(0, 8, GuiUtils.createButtonItem(Settings.EXIT_ICON.getMaterial(CompatibleMaterial.OAK_DOOR),
                plugin.getLocale().getMessage("interface.button.exit").getMessage()),
                ClickType.LEFT,
                event -> exit());

        // back button
        if (parent != null)
            setButton(0, 0, GuiUtils.createButtonItem(ItemUtils.getCustomHead("3ebf907494a935e955bfcadab81beafb90fb9be49c7026ba97d798d5f1a23"),
                    plugin.getLocale().getMessage("interface.button.back").getMessage()),
                    ClickType.LEFT,
                    event -> event.player.closeInventory());

        // info icon
        setItem(0, 4, GuiUtils.createButtonItem(CompatibleMaterial.CHEST,
                plugin.getLocale().newMessage("&5&l" + kit.getName()).getMessage(),
                plugin.getLocale().newMessage("&&fPermissions:").getMessage(),
                plugin.getLocale().newMessage("&&7ultimatekits.kit." + kit.getName().toLowerCase()).getMessage()
        ));

        paint();
    }

    public void paint() {
        int num = 10;
        for (ItemStack itemStack : kit.getReadableContents(player, false, true, true)) {
            if (num == 17 || num == 36)
                num++;

            KitItem item = new KitItem(itemStack);

            ItemStack is = item.getMoveableItem();

            ItemMeta meta;

            if (is.hasItemMeta()) meta = is.getItemMeta();
            else meta = Bukkit.getItemFactory().getItemMeta(is.getType());

            List<String> itemLore;

            if (meta.hasLore()) itemLore = meta.getLore();
            else itemLore = new ArrayList<>();
            itemLore.add(TextUtils.convertToInvisibleLoreString("----"));
            itemLore.add(ChatColor.GRAY.toString() + plugin.getLocale().getMessage("general.type.chance") + ": " + ChatColor.GOLD.toString() + item.getChance() + "%");
            if (isInFuction) {
                itemLore.addAll(Arrays.asList(plugin.getLocale().getMessage("interface.kiteditor.itemfunctionlore")
                        .processPlaceholder("item", item.getDisplayItem() == null ? "" : item.getDisplayItem().name())
                        .processPlaceholder("name", item.getDisplayName())
                        .processPlaceholder("lore", item.getDisplayLore())
                        .getMessage().split("|")));
            }
            meta.setLore(itemLore);
            is.setItemMeta(meta);

            if (is.getAmount() > 64) {
                int overflow = is.getAmount() % 64;
                int stackamt = (int) ((long) (is.getAmount() / 64));
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
            } else {
                inventory.setItem(num, is);
                num++;
            }
        }
        updateInvButton();
    }

    private void updateInvButton() {
        ItemStack item = GuiUtils.createButtonItem(CompatibleMaterial.PAPER,
                plugin.getLocale().getMessage("interface.kiteditor.itemediting").getMessage(),
                plugin.getLocale().getMessage("interface.kiteditor.itemeditinglore").getMessage());

        if (!isInFuction) {
            setUnlockedRange(1, 1, 1, 7);
            setUnlockedRange(2, 0, 3, 8);
            setUnlockedRange(4, 1, 4, 7);
        } else {
            unlockedCells.clear();
            item = GuiUtils.createButtonItem(CompatibleMaterial.PAPER,
                    plugin.getLocale().getMessage("interface.kiteditor.itemmoving").getMessage(),
                    plugin.getLocale().getMessage("interface.kiteditor.itemmovinglore").getMessage());
        }

        setButton(48, item,
                (event) -> {
                    isInFuction = !isInFuction;
                    saveKit(player, inventory, true);
                    paint();
                });

        ItemStack item2 = GuiUtils.createButtonItem(CompatibleMaterial.ITEM_FRAME,
                plugin.getLocale().getMessage("interface.kiteditor.switchtokitfunctions").getMessage(),
                plugin.getLocale().getMessage("interface.kiteditor.switchtokitfunctionslore").getMessage().split("|"));

        setAcceptsItems(!isInInventory);

        setButton(50, item2,
                (event) -> {
                    if (!isInInventory) {
                        player.getInventory().setContents(inventoryItems);
                        isInInventory = true;
                        player.updateInventory();
                    } else {
                        saveItemsInstance();
                        getInvItems();
                    }
                    updateInvButton();
                });
    }

    private void saveItemsInstance() {
        inventoryItems = player.getInventory().getContents().clone();
        player.getInventory().clear();
    }

    private void getInvItems() {
        isInInventory = false;

        setButton(9, GuiUtils.createButtonItem(CompatibleMaterial.REDSTONE_TORCH,
                plugin.getLocale().getMessage("interface.kiteditor.generaloptions").getMessage(),
                plugin.getLocale().getMessage("interface.kiteditor.generaloptionslore").getMessage().split("|")),
                (event) -> guiManager.showGUI(player, new KitGeneralOptionsGui(plugin, player, kit, this)));

        setButton(10, GuiUtils.createButtonItem(CompatibleMaterial.EMERALD,
                plugin.getLocale().getMessage("interface.kiteditor.sellingoptions").getMessage(),
                plugin.getLocale().getMessage("interface.kiteditor.sellingoptionslore").getMessage().split("|")),
                (event) -> guiManager.showGUI(player, new KitSellingOptionsGui(plugin, player, kit, this)));

        setButton(12, GuiUtils.createButtonItem(CompatibleMaterial.ITEM_FRAME,
                plugin.getLocale().getMessage("interface.kiteditor.guioptions").getMessage(),
                plugin.getLocale().getMessage("interface.kiteditor.guioptionslore").getMessage().split("|")),
                (event) -> guiManager.showGUI(player, new KitGuiOptionsGui(plugin, player, kit, parent)));

        setButton(13, GuiUtils.createButtonItem(CompatibleMaterial.PAPER,
                plugin.getLocale().getMessage("interface.kiteditor.addcommand").getMessage(),
                plugin.getLocale().getMessage("interface.kiteditor.addcommandlore").getMessage().split("|")),
                (event) -> {
                    // TODO: this should be a chat listener, not anvil
                    AnvilGui gui = new AnvilGui(player, this);
                    gui.setTitle(plugin.getLocale().getMessage("interface.kiteditor.addcommandpromp").getMessage());
                    gui.setAction(aevent -> {
                        String msg = gui.getInputText();

                        ItemStack parseStack = new ItemStack(Material.PAPER, 1);
                        ItemMeta meta = parseStack.getItemMeta();

                        ArrayList<String> lore = new ArrayList<>();

                        int index = 0;
                        while (index < msg.length()) {
                            lore.add(ChatColor.GREEN + "/" + msg.substring(index, Math.min(index + 30, msg.length())));
                            index += 30;
                        }
                        meta.setLore(lore);
                        meta.setDisplayName(plugin.getLocale().getMessage("general.type.command").getMessage());
                        parseStack.setItemMeta(meta);

                        plugin.getLocale().newMessage(plugin.getLocale().getMessage("interface.kiteditor.addcommandok")
                                .processPlaceholder("command", msg).getMessage())
                                .sendPrefixedMessage(player);

                        this.inventory.addItem(parseStack);

                    });
                    guiManager.showGUI(event.player, gui);
                });

        setButton(14, GuiUtils.createButtonItem(CompatibleMaterial.SUNFLOWER,
                plugin.getLocale().getMessage("interface.kiteditor.addeconomy").getMessage(),
                plugin.getLocale().getMessage("interface.kiteditor.addeconomylore").getMessage().split("|")),
                (event) -> {
                    AnvilGui gui = new AnvilGui(player, this);
                    gui.setTitle(plugin.getLocale().getMessage("interface.kiteditor.addeconomyprompt").getMessage());
                    gui.setAction(aevent -> {
                        String msg = gui.getInputText();

                        ItemStack parseStack = new ItemStack(Material.PAPER, 1);
                        ItemMeta meta = parseStack.getItemMeta();

                        ArrayList<String> lore = new ArrayList<>();

                        int index = 0;
                        while (index < msg.length()) {
                            lore.add(ChatColor.GREEN + "$" + msg.substring(index, Math.min(index + 30, msg.length())));
                            index += 30;
                        }
                        meta.setLore(lore);
                        meta.setDisplayName(plugin.getLocale().getMessage("general.type.money").getMessage());
                        parseStack.setItemMeta(meta);

                        plugin.getLocale().getMessage("interface.kiteditor.addeconomyok")
                                .sendPrefixedMessage(player);

                        this.inventory.addItem(parseStack);

                    });
                    guiManager.showGUI(event.player, gui);
                });

        setButton(17, GuiUtils.createButtonItem(CompatibleMaterial.CHEST,
                plugin.getLocale().getMessage("interface.kiteditor.animation").getMessage(),
                plugin.getLocale().getMessage("interface.kiteditor.animationlore").getMessage().split("|")),
                (event) -> {
                    if (kit.getKitAnimation() == KitAnimation.NONE) {
                        kit.setKitAnimation(KitAnimation.ROULETTE);
                    } else {
                        kit.setKitAnimation(KitAnimation.NONE);
                    }
                    getInvItems();
                });
    }


    public void saveKit(Player player, Inventory i, boolean muteSave) {
        ItemStack[] items = i.getContents();
        for (int num = 0; num < items.length; ++num) {
            if (num < 10 || num == 17 || num == 36) {
                items[num] = null;
            }
        }

        items = Arrays.copyOf(items, items.length - 10);

        kit.saveKit(Arrays.asList(items));
        plugin.getLocale().getMessage("interface.kiteditor.saved")
                .processPlaceholder("kit", kit.getShowableName())
                .sendPrefixedMessage(player);
    }

    public void replaceItem(Action action, Player player, ItemStack itemStack, int slot) {
        if (itemStack.getItemMeta().hasLore()) {
            ItemMeta meta = itemStack.getItemMeta();
            List<String> newLore = new ArrayList<>();
            for (String line : meta.getLore()) {
                if (TextUtils.convertFromInvisibleString(line).equals("----")) break;
                newLore.add(line);
            }
            meta.setLore(newLore);
            itemStack.setItemMeta(meta);
        }

        KitItem item = new KitItem(itemStack);

        switch (action) {
            case CHANCE:
                item.setChance(item.getChance() == 100 ? 5 : (item.getChance() + 5));
                inventory.setItem(slot, item.getMoveableItem());
                saveKit(player, inventory, true);
                break;
            case DISPLAY_ITEM: {
                AnvilGui gui = new AnvilGui(player, this);
                gui.setTitle("Enter a Material");
                gui.setAction(event -> {
                    Material material = CompatibleMaterial.getMaterial(gui.getInputText()).getMaterial();
                    if (material == null) {
                        player.sendMessage(gui.getInputText() + " &8is not a valid material.");
                    } else {
                        KitItem newItem = new KitItem(itemStack);
                        newItem.setDisplayItem(material);
                        inventory.setItem(slot, newItem.getMoveableItem());
                    }
                });
                guiManager.showGUI(player, gui);
            }
            break;
            case DISPLAY_NAME: {
                AnvilGui gui = new AnvilGui(player, this);
                gui.setTitle("Enter a name");
                gui.setAction(event -> {
                    KitItem newItem = new KitItem(itemStack);
                    newItem.setDisplayName(gui.getInputText());
                    setItem(slot, newItem.getMoveableItem());
                });
                guiManager.showGUI(player, gui);
            }
            break;
            case DISPLAY_LORE: {
                AnvilGui gui = new AnvilGui(player, this);
                gui.setTitle("Enter lore");
                gui.setAction(event -> {
                    KitItem newItem = new KitItem(itemStack);
                    newItem.setDisplayLore(gui.getInputText());

                    setItem(slot, newItem.getMoveableItem());
                });
                guiManager.showGUI(player, gui);
            }
            break;
        }
    }

    public enum Action {NONE, CHANCE, DISPLAY_ITEM, DISPLAY_NAME, DISPLAY_LORE}
}
