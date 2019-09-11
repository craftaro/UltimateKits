package com.songoda.ultimatekits.gui;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.utils.ItemUtils;
import com.songoda.core.utils.TextUtils;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.kit.KitAnimation;
import com.songoda.ultimatekits.kit.KitItem;
import com.songoda.ultimatekits.utils.Methods;
import com.songoda.ultimatekits.utils.gui.AbstractAnvilGUI;
import com.songoda.ultimatekits.utils.gui.AbstractGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GUIKitEditor extends AbstractGUI {

    private UltimateKits plugin;
    private Kit kit;
    private AbstractGUI back;

    private ItemStack[] inventoryItems;

    private boolean isInFuction = false;
    private boolean isInInventory = false;



    private ItemStack toReplace;
    private int slot;

    private String title;

    public GUIKitEditor(UltimateKits plugin, Player player, Kit kit, AbstractGUI back, ItemStack toReplace, int slot) {
        super(player);
        this.plugin = plugin;
        this.kit = kit;
        this.back = back;

        String name = kit.getShowableName();
        title = "&8You are editing kit: &9" + name + "&8.";

        this.toReplace = toReplace;
        this.slot = slot;

        init(title, 54);
        saveItemsInstance();
        getInvItems();
    }

    @Override
    protected void constructGUI() {
        inventory.clear();
        player.updateInventory();

        createButton(8, Material.valueOf(UltimateKits.getInstance().getConfig().getString("Interfaces.Exit Icon")),
                UltimateKits.getInstance().getLocale().getMessage("interface.button.exit").getMessage());

        ItemStack back = ItemUtils.getCustomHead("3ebf907494a935e955bfcadab81beafb90fb9be49c7026ba97d798d5f1a23");
        SkullMeta skull2Meta = (SkullMeta) back.getItemMeta();
        back.setDurability((short) 3);
        skull2Meta.setDisplayName(UltimateKits.getInstance().getLocale().getMessage("interface.button.back").getMessage());
        back.setItemMeta(skull2Meta);

        ItemStack it = new ItemStack(Material.CHEST, 1);
        ItemMeta itmeta = it.getItemMeta();
        itmeta.setDisplayName(Methods.formatText("&5&l" + kit.getName()));
        ArrayList<String> lore = new ArrayList<>();
        lore.add(Methods.formatText("&fPermissions:"));
        lore.add(Methods.formatText("&7ultimatekits.kit." + kit.getName().toLowerCase()));
        itmeta.setLore(lore);
        it.setItemMeta(itmeta);

        ItemStack glass = new ItemStack(plugin.isServerVersionAtLeast(ServerVersion.V1_13) ? Material.GRAY_STAINED_GLASS_PANE : Material.valueOf("STAINED_GLASS_PANE"), 1);
        ItemMeta glassmeta = glass.getItemMeta();
        glassmeta.setDisplayName(Methods.formatText("&" + kit.getName().replaceAll(".(?!$)", "$0&")));
        glass.setItemMeta(glassmeta);

        if (this.back != null)
            inventory.setItem(0, back);
        inventory.setItem(4, it);

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
            itemLore.add(TextUtils.convertToInvisibleLoreString("----"));
            itemLore.add(Methods.formatText("&7" + plugin.getLocale().getMessage("general.type.chance") + ": &6" + item.getChance() + "%"));
            if (isInFuction) {
                itemLore.add(Methods.formatText("&7Display Item: &6" + (item.getDisplayItem() == null ? "" : item.getDisplayItem().name())));
                itemLore.add(Methods.formatText("&7Display Name: &6" + Methods.formatText(item.getDisplayName())));
                itemLore.add(Methods.formatText("&7Display Lore: &6" + Methods.formatText(item.getDisplayLore())));
            }
            itemLore.add("");
            if (isInFuction) {
                itemLore.add(Methods.formatText("&7Left-Click: &6To set a display item."));
                itemLore.add(Methods.formatText("&7Middle-Click: &6To set a display name."));
                itemLore.add(Methods.formatText("&7Right-Click: &6To set display lore."));
                itemLore.add(Methods.formatText("&7Shift-Click: &6To set chance."));
                itemLore.add("");
                itemLore.add(Methods.formatText("&7Display options only show up on display."));
                itemLore.add(Methods.formatText("&7This can be useful if you want to explain"));
                itemLore.add(Methods.formatText("&7What an item does without putting it in the"));
                itemLore.add(Methods.formatText("&7permanent lore."));
                itemLore.add("");
                itemLore.add(Methods.formatText("&6Leave function mode to move items."));
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
        if (toReplace != null && slot == 0) {
            if (num == 17 || num == 36)
                num++;
            inventory.setItem(num, toReplace);
            toReplace = null;
        }

        inventory.setItem(3, Methods.getGlass());
        inventory.setItem(5, Methods.getGlass());

        inventory.setItem(48, Methods.getGlass());
        inventory.setItem(50, Methods.getGlass());

        if (this.back == null)
            inventory.setItem(0, Methods.getBackgroundGlass(true));
        inventory.setItem(1, Methods.getBackgroundGlass(true));
        inventory.setItem(9, Methods.getBackgroundGlass(true));

        inventory.setItem(7, Methods.getBackgroundGlass(true));
        inventory.setItem(17, Methods.getBackgroundGlass(true));

        inventory.setItem(54 - 18, Methods.getBackgroundGlass(true));
        inventory.setItem(54 - 9, Methods.getBackgroundGlass(true));
        inventory.setItem(54 - 8, Methods.getBackgroundGlass(true));

        inventory.setItem(54 - 10, Methods.getBackgroundGlass(true));
        inventory.setItem(54 - 2, Methods.getBackgroundGlass(true));
        inventory.setItem(54 - 1, Methods.getBackgroundGlass(true));

        inventory.setItem(2, Methods.getBackgroundGlass(false));
        inventory.setItem(6, Methods.getBackgroundGlass(false));
        inventory.setItem(54 - 7, Methods.getBackgroundGlass(false));
        inventory.setItem(54 - 3, Methods.getBackgroundGlass(false));

        updateInvButton();

    }


    private void updateInvButton() {
        if (!isInFuction) {
            createButton(48, Material.PAPER, "&6Switch To Item Editing",
                    "&7Click to enable",
                    "&7item editing.");
            addDraggable(new Range(10, 43, null, true), true);
            addDraggable(new Range(17, 17, null, true), false);
            addDraggable(new Range(36, 36, null, true), false);
            registerClickables();
        } else {
            createButton(48, Material.PAPER, "&6Switch To Item Moving",
                    "&7Click to switch back",
                    "&7to item moving.");
            removeDraggable();
            registerClickables();
        }

        if (!isInInventory) {
            createButton(50, Material.ITEM_FRAME, "&6Switch To Your Inventory",
                    "&7Click to switch to",
                    "&7your inventory.");
            cancelBottom = true;
            registerClickables();
        } else {
            createButton(50, Material.ITEM_FRAME, "&6Switch To Kit Functions",
                    "&7Click to switch back",
                    "&7to the kit functions.");
            cancelBottom = false;
            registerClickables();
        }
    }

    private void saveItemsInstance() {
        inventoryItems = player.getInventory().getContents().clone();
        player.getInventory().clear();
    }

    private void getInvItems() {
        isInInventory = false;

        createButton(9, player.getInventory(), plugin.isServerVersionAtLeast(ServerVersion.V1_13) ? Material.REDSTONE_TORCH : Material.valueOf("REDSTONE_TORCH_ON"), "&6General Options",
                "&7Click to edit adjust",
                "&7general options.");

        createButton(10, player.getInventory(), Material.EMERALD, "&9Selling Options",
                "&7Click to edit adjust",
                "&7selling options.");

        createButton(12, player.getInventory(), Material.ITEM_FRAME, "&5GUI Options",
                "&7Click to edit GUI options",
                "&7for this kit.");

        createButton(13, player.getInventory(), Material.PAPER, "&fAdd Command",
                "&7Click to add a command",
                "&7to this kit.");

        createButton(14, player.getInventory(), plugin.isServerVersionAtLeast(ServerVersion.V1_13) ? Material.SUNFLOWER : Material.valueOf("DOUBLE_PLANT"), "&6Add Economy",
                "&7Click to add money",
                "&7to this kit.");

        createButton(17, player.getInventory(), Material.CHEST, "&6Kit Animation",
                "&7Currently: &6" + kit.getKitAnimation().name());
    }


    public void saveKit(Player player, Inventory i, boolean muteSave) {
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
        plugin.getLocale().newMessage("&8Changes to &a" + kit.getShowableName() + " &8saved successfully.")
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
                toReplace = item.getMoveableItem();
                this.slot = slot;
                saveKit(player, inventory, true);
                constructGUI();
                break;
            case DISPLAY_ITEM: {
                saveKit(player, this.inventory, true);
                AbstractAnvilGUI gui = new AbstractAnvilGUI(player, event -> {
                    String msg = event.getName();
                    ItemStack toReplace = null;
                    try {
                        Material material = Material.valueOf(msg.trim().toUpperCase());

                        KitItem item2 = new KitItem(itemStack);
                        item2.setDisplayItem(material);
                        toReplace = item2.getMoveableItem();
                    } catch (Exception e) {
                        player.sendMessage(Methods.formatText("&a" + msg + " &8is not a valid material."));
                    }
                    this.slot = slot;
                    this.toReplace = toReplace;
                });

                gui.setOnClose((player1, inventory1) -> init(title, 54));

                ItemStack item2 = new ItemStack(Material.NAME_TAG);
                ItemMeta meta2 = item2.getItemMeta();
                meta2.setDisplayName("Enter a Material");
                item2.setItemMeta(meta2);

                gui.setSlot(AbstractAnvilGUI.AnvilSlot.INPUT_LEFT, item2);
                gui.open();
            }
            break;
            case DISPLAY_NAME: {
                saveKit(player, this.inventory, true);
                AbstractAnvilGUI gui = new AbstractAnvilGUI(player, event -> {
                    String msg = event.getName();
                    KitItem item2 = new KitItem(itemStack);
                    item2.setDisplayName(msg);
                    this.toReplace = item2.getMoveableItem();
                    this.slot = slot;
                });

                gui.setOnClose((player1, inventory1) -> {
                    kit.getContents().forEach((item1) -> {
                    });
                    init(title, 54);
                });

                ItemStack item2 = new ItemStack(Material.NAME_TAG);
                ItemMeta meta2 = item2.getItemMeta();
                meta2.setDisplayName("Enter a name");
                item2.setItemMeta(meta2);

                gui.setSlot(AbstractAnvilGUI.AnvilSlot.INPUT_LEFT, item2);
                gui.open();
            }
            break;
            case DISPLAY_LORE: {
                saveKit(player, this.inventory, true);
                AbstractAnvilGUI gui = new AbstractAnvilGUI(player, event -> {
                    String msg = event.getName();
                    KitItem item2 = new KitItem(itemStack);
                    item2.setDisplayLore(msg);

                    this.toReplace = item2.getMoveableItem();

                    this.slot = slot;
                });

                gui.setOnClose((player1, inventory1) -> init(title, 54));

                ItemStack item2 = new ItemStack(Material.NAME_TAG);
                ItemMeta meta2 = item2.getItemMeta();
                meta2.setDisplayName("Enter lore");
                item2.setItemMeta(meta2);

                gui.setSlot(AbstractAnvilGUI.AnvilSlot.INPUT_LEFT, item2);
                gui.open();
            }
            break;
        }
    }

    @Override
    protected void registerClickables() {
        resetClickables();

        if (!isInInventory) {
            registerClickable(9, true, ((player, inventory, cursor, slot, type) ->
                    new GUIGeneralOptions(plugin, player, this, kit)));
            registerClickable(12, true, ((player, inventory, cursor, slot, type) ->
                    new GUIGUIOptions(plugin, player, this, kit)));
            registerClickable(10, true, ((player, inventory, cursor, slot, type) ->
                    new GUISellingOptions(plugin, player, this, kit)));
            registerClickable(17, true, (player, inventory, cursor, slot, type) -> {
                if (kit.getKitAnimation() == KitAnimation.NONE) {
                    kit.setKitAnimation(KitAnimation.ROULETTE);
                } else {
                    kit.setKitAnimation(KitAnimation.NONE);
                }
                getInvItems();
            });
            registerClickable(14, true, ((player, inventory, cursor, slot, type) -> {
                saveKit(player, this.inventory, true);
                AbstractAnvilGUI gui = new AbstractAnvilGUI(player, event -> {
                    String msg = event.getName();
                    ItemStack parseStack2 = new ItemStack(Material.PAPER, 1);
                    ItemMeta meta2 = parseStack2.getItemMeta();

                    ArrayList<String> lore2 = new ArrayList<>();

                    int index2 = 0;
                    while (index2 < msg.length()) {
                        lore2.add("§a$" + msg.substring(index2, Math.min(index2 + 30, msg.length())));
                        index2 += 30;
                    }
                    meta2.setLore(lore2);
                    meta2.setDisplayName(plugin.getLocale().getMessage("general.type.money").getMessage());
                    parseStack2.setItemMeta(meta2);

                    plugin.getLocale().newMessage("&8Money &5$" + msg + "&8 has been added to your kit.")
                            .sendPrefixedMessage(player);

                    this.slot = 0;
                    this.toReplace = parseStack2;
                });

                gui.setOnClose((player1, inventory1) -> init(title, 54));

                ItemStack item = new ItemStack(Material.DIAMOND);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName("Enter Price (No $)");
                item.setItemMeta(meta);

                gui.setSlot(AbstractAnvilGUI.AnvilSlot.INPUT_LEFT, item);
                gui.open();
            }));

            registerClickable(13, true, ((player, inventory, cursor, slot, type) -> {
                saveKit(player, this.inventory, true);
                AbstractAnvilGUI gui = new AbstractAnvilGUI(player, event -> {
                    String msg = event.getName();
                    ItemStack parseStack = new ItemStack(Material.PAPER, 1);
                    ItemMeta meta = parseStack.getItemMeta();

                    ArrayList<String> lore = new ArrayList<>();

                    int index = 0;
                    while (index < msg.length()) {
                        lore.add(ChatColor.COLOR_CHAR + "a/" + msg.substring(index, Math.min(index + 30, msg.length())));
                        index += 30;
                    }
                    meta.setLore(lore);
                    meta.setDisplayName(plugin.getLocale().getMessage("general.type.command").getMessage());
                    parseStack.setItemMeta(meta);

                    plugin.getLocale().newMessage("&8Command &5" + msg + "&8 has been added to your kit.")
                            .sendPrefixedMessage(player);

                    this.slot = 0;
                    this.toReplace = parseStack;
                });

                gui.setOnClose((player1, inventory1) -> init(title, 54));

                ItemStack item = new ItemStack(Material.PAPER);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName("Enter Command (No /)");
                item.setItemMeta(meta);

                gui.setSlot(AbstractAnvilGUI.AnvilSlot.INPUT_LEFT, item);
                gui.open();
            }));
        }

        if (isInFuction) {
            registerClickable(10, 43, ClickType.SHIFT_LEFT, (player, inventory, cursor, slot, type) -> {
                if (inventory.getItem(slot) == null) return;
                replaceItem(Action.CHANCE, player, inventory.getItem(slot), slot);
            });
            registerClickable(10, 43, ClickType.LEFT, (player, inventory, cursor, slot, type) -> {
                if (inventory.getItem(slot) == null) return;
                replaceItem(Action.DISPLAY_ITEM, player, inventory.getItem(slot), slot);
            });
            registerClickable(10, 43, ClickType.MIDDLE, (player, inventory, cursor, slot, type) -> {
                if (inventory.getItem(slot) == null) return;
                replaceItem(Action.DISPLAY_NAME, player, inventory.getItem(slot), slot);
            });
            registerClickable(10, 43, ClickType.RIGHT, (player, inventory, cursor, slot, type) -> {
                if (inventory.getItem(slot) == null) return;
                replaceItem(Action.DISPLAY_LORE, player, inventory.getItem(slot), slot);
            });
        }

        registerClickable(8, (player, inventory, cursor, slot, type) -> player.closeInventory());

        registerClickable(0, ((player, inventory, cursor, slot, type) -> {
            if (back == null) return;
            back.init(back.getSetTitle(), back.getInventory().getSize());
        }));

        registerClickable(48, ((player1, inventory, cursor, slot1, type) -> {
            isInFuction = !isInFuction;
            saveKit(player1, inventory,true);
            constructGUI();
        }));

        registerClickable(50, (player, inventory, cursor, slot, type) -> {
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

    @Override
    protected void registerOnCloses() {
        registerOnClose((player1, inventory1) -> {
            this.saveKit(player1, inventory1, false);
            if (!isInInventory && this.inventoryItems.length != 0) {
                player.getInventory().setContents(this.inventoryItems);
                player.updateInventory();
            }

            CompatibleSound.ENTITY_VILLAGER_YES.play(player);
        });
    }

    public enum Action {NONE, CHANCE, DISPLAY_ITEM, DISPLAY_NAME, DISPLAY_LORE}
}
