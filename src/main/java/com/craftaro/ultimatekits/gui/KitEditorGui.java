package com.craftaro.ultimatekits.gui;

import com.craftaro.core.gui.AnvilGui;
import com.craftaro.core.gui.DoubleGui;
import com.craftaro.core.gui.Gui;
import com.craftaro.core.gui.GuiUtils;
import com.craftaro.core.input.ChatPrompt;
import com.craftaro.core.utils.ItemUtils;
import com.craftaro.core.utils.TextUtils;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.ultimatekits.UltimateKits;
import com.craftaro.ultimatekits.kit.Kit;
import com.craftaro.ultimatekits.kit.KitAnimation;
import com.craftaro.ultimatekits.kit.KitItem;
import com.craftaro.ultimatekits.settings.Settings;
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
import java.util.Optional;

public class KitEditorGui extends DoubleGui {
    private final UltimateKits plugin;
    private final Kit kit;
    private final Player player;
    private final Gui back;

    private boolean isInFunction = false;
    private boolean isInInventory = false;

    private ItemStack[] stash;

    public KitEditorGui(UltimateKits plugin, Player player, Kit kit, Gui back) {
        super(6);
        this.plugin = plugin;
        this.kit = kit;
        this.player = player;
        this.back = back;

        setDefaultItem(null);
        setTitle(plugin.getLocale().getMessage("interface.kiteditor.title")
                .processPlaceholder("name", kit.getName())
                .getMessage());

        setOnClose((event) -> {
            restoreItemsInstance();
            saveKit(player, this.inventory, false);
            XSound.ENTITY_VILLAGER_YES.play(player);
        });

        ItemStack glass1 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_1.getMaterial());
        ItemStack glass2 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_2.getMaterial());
        ItemStack glass3 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_3.getMaterial());

        mirrorFill(0, 0, true, true, glass2);
        mirrorFill(0, 1, true, true, glass2);
        mirrorFill(1, 0, true, true, glass2);
        mirrorFill(0, 2, true, true, glass3);
        mirrorFill(0, 3, false, true, glass1);

        // exit button
        setButton(0, 8, GuiUtils.createButtonItem(Settings.EXIT_ICON.getMaterial(XMaterial.OAK_DOOR),
                        plugin.getLocale().getMessage("interface.button.exit").getMessage()),
                ClickType.LEFT,
                event -> player.closeInventory());

        // back button
        if (this.parent != null) {
            setButton(0, 0, GuiUtils.createButtonItem(ItemUtils.getCustomHead("3ebf907494a935e955bfcadab81beafb90fb9be49c7026ba97d798d5f1a23"),
                            plugin.getLocale().getMessage("interface.button.back").getMessage()),
                    ClickType.LEFT,
                    event -> {
                        player.closeInventory();
                        this.guiManager.showGUI(player, back);
                    });
        }

        // info icon
        setItem(0, 4, GuiUtils.createButtonItem(XMaterial.CHEST,
                plugin.getLocale().getMessage("interface.kiteditor.info")
                        .processPlaceholder("kit", kit.getKey())
                        .processPlaceholder("perm", "ultimatekits.claim." + kit.getKey().toLowerCase())
                        .getMessage().split("\\|"))
        );

        saveItemsInstance();
        paint();

        Bukkit.getScheduler().runTaskLater(plugin, this::setInvItems, 3L);
    }

    private void paint() {
        for (int i = 10; i < 44; i++) {
            if (i == 17 || i == 36) {
                continue;
            }
            setItem(i, null);
        }

        int num = 10;
        for (ItemStack itemStack : this.kit.getReadableContents(this.player, false, true, true)) {
            if (num == 17 || num == 36) {
                num++;
            }

            KitItem item = new KitItem(itemStack);
            ItemStack is = getCompiledMeta(item);


            if (!this.isInFunction) {
                setButton(num, is, null);
            } else {
                setButton(num, is,
                        (event) -> {
                            switch (event.clickType) {
                                case SHIFT_LEFT:
                                    replaceItem(Action.CHANCE_UP, this.player, event.clickedItem, event.slot);
                                    break;
                                case SHIFT_RIGHT:
                                    replaceItem(Action.CHANCE_DOWN, this.player, event.clickedItem, event.slot);
                                    break;
                                case LEFT:
                                    replaceItem(Action.DISPLAY_ITEM, this.player, event.clickedItem, event.slot);
                                    break;
                                case MIDDLE:
                                    replaceItem(Action.DISPLAY_NAME, this.player, event.clickedItem, event.slot);
                                    break;
                                case RIGHT:
                                    replaceItem(Action.DISPLAY_LORE, this.player, event.clickedItem, event.slot);
                                    break;
                            }

                        });
            }
            num++;
        }
        updateInvButton();
    }

    private void updateInvButton() {
        ItemStack item;

        if (!this.isInFunction) {
            setUnlockedRange(1, 1, 1, 7);
            setUnlockedRange(2, 0, 3, 8);
            setUnlockedRange(4, 1, 4, 7);

            item = GuiUtils.createButtonItem(XMaterial.PAPER,
                    this.plugin.getLocale().getMessage("interface.kiteditor.itemediting").getMessage(),
                    this.plugin.getLocale().getMessage("interface.kiteditor.itemeditinglore").getMessage().split("\\|"));
        } else {
            this.unlockedCells.clear();

            item = GuiUtils.createButtonItem(XMaterial.PAPER,
                    this.plugin.getLocale().getMessage("interface.kiteditor.itemmoving").getMessage(),
                    this.plugin.getLocale().getMessage("interface.kiteditor.itemmovinglore").getMessage().split("\\|"));
        }

        setButton(48, item,
                (event) -> {
                    this.isInFunction = !this.isInFunction;
                    saveKit(this.player, this.inventory, true);
                    paint();
                });

        ItemStack item2;

        item2 = this.isInInventory ? GuiUtils.createButtonItem(XMaterial.ITEM_FRAME,
                this.plugin.getLocale().getMessage("interface.kiteditor.switchtokitfunctions").getMessage(),
                this.plugin.getLocale().getMessage("interface.kiteditor.switchtokitfunctionslore").getMessage().split("\\|"))
                : GuiUtils.createButtonItem(XMaterial.ITEM_FRAME,
                this.plugin.getLocale().getMessage("interface.kiteditor.switchtoinventory").getMessage(),
                this.plugin.getLocale().getMessage("interface.kiteditor.switchtoinventorylore").getMessage().split("\\|"));

        setButton(50, item2,
                event -> {
                    if (!this.isInInventory) {
                        restoreItemsInstance();
                        setPlayerActionForRange(0, 0, 3, 8, null);
                        setAcceptsItems(true);
                    } else {
                        saveItemsInstance();
                        setInvItems();
                        setAcceptsItems(false);
                    }
                    updateInvButton();
                });
    }

    private void saveItemsInstance() {
        setPlayerUnlockedRange(0, 0, 3, 8, false);
        this.stash = this.player.getInventory().getContents().clone();
        this.player.getInventory().clear();
        this.isInInventory = false;
    }

    private void restoreItemsInstance() {
        if (!this.isInInventory) {
            this.player.getInventory().clear();
        }
        setPlayerUnlockedRange(0, 0, 3, 8);
        if (this.stash != null) {
            this.player.getInventory().setContents(this.stash);
        }
        this.player.updateInventory();
        this.isInInventory = true;
    }

    private void setInvItems() {
        setPlayerButton(0, GuiUtils.createButtonItem(XMaterial.REDSTONE_TORCH,
                        this.plugin.getLocale().getMessage("interface.kiteditor.generaloptions").getMessage(),
                        this.plugin.getLocale().getMessage("interface.kiteditor.generaloptionslore").getMessage().split("\\|")),
                (event) -> {
                    this.player.closeInventory();
                    this.guiManager.showGUI(this.player, new KitGeneralOptionsGui(this.plugin, this.player, this.kit, this.back));
                });

        setPlayerButton(1, GuiUtils.createButtonItem(XMaterial.EMERALD,
                        this.plugin.getLocale().getMessage("interface.kiteditor.sellingoptions").getMessage(),
                        this.plugin.getLocale().getMessage("interface.kiteditor.sellingoptionslore").getMessage().split("\\|")),
                (event) -> {
                    this.player.closeInventory();
                    this.guiManager.showGUI(this.player, new KitSellingOptionsGui(this.plugin, this.player, this.kit, this.back));
                });

        setPlayerButton(3, GuiUtils.createButtonItem(XMaterial.ITEM_FRAME,
                        this.plugin.getLocale().getMessage("interface.kiteditor.guioptions").getMessage(),
                        this.plugin.getLocale().getMessage("interface.kiteditor.guioptionslore").getMessage().split("\\|")),
                (event) -> {
                    this.player.closeInventory();
                    this.guiManager.showGUI(this.player, new KitGuiOptionsGui(this.plugin, this.player, this.kit, this.back));
                });

        setPlayerButton(4, GuiUtils.createButtonItem(XMaterial.PAPER,
                        this.plugin.getLocale().getMessage("interface.kiteditor.addcommand").getMessage(),
                        this.plugin.getLocale().getMessage("interface.kiteditor.addcommandlore").getMessage().split("\\|")),
                (event) -> {
                    event.gui.exit();
                    ChatPrompt.showPrompt(event.manager.getPlugin(), event.player, "Enter a command for this kit:", response -> {
                                String msg = response.getMessage().trim();

                                ItemStack parseStack = new ItemStack(Material.PAPER, 1);
                                ItemMeta meta = parseStack.getItemMeta();

                                ArrayList<String> lore = new ArrayList<>();

                                int index = 0;
                                while (index < msg.length()) {
                                    lore.add(ChatColor.GREEN + (index == 0 ? "/" : "") + msg.substring(index, Math.min(index + 30, msg.length())));
                                    index += 30;
                                }
                                meta.setLore(lore);
                                meta.setDisplayName(this.plugin.getLocale().getMessage("general.type.command").getMessage());
                                parseStack.setItemMeta(meta);

                                this.plugin.getLocale().newMessage(this.plugin.getLocale().getMessage("interface.kiteditor.addcommandok")
                                                .processPlaceholder("command", msg).getMessage())
                                        .sendPrefixedMessage(this.player);

                                this.inventory.addItem(parseStack);
                                Bukkit.getScheduler().runTask(this.plugin, event.player::closeInventory);
                            }).setOnClose(() -> {
                                event.manager.showGUI(event.player, this);
                            })
                            .setOnCancel(() -> {
                                event.player.sendMessage(ChatColor.RED + "Edit canceled");
                                event.manager.showGUI(event.player, this);
                            });
                });

        setPlayerButton(5, GuiUtils.createButtonItem(XMaterial.SUNFLOWER,
                        this.plugin.getLocale().getMessage("interface.kiteditor.addeconomy").getMessage(),
                        this.plugin.getLocale().getMessage("interface.kiteditor.addeconomylore").getMessage().split("\\|")),
                (event) -> {
                    AnvilGui gui = new AnvilGui(this.player, this);
                    gui.setTitle(this.plugin.getLocale().getMessage("interface.kiteditor.addeconomyprompt").getMessage());
                    gui.setAction(aevent -> {
                        String msg = gui.getInputText().trim();

                        ItemStack parseStack = new ItemStack(Material.PAPER, 1);
                        ItemMeta meta = parseStack.getItemMeta();

                        ArrayList<String> lore = new ArrayList<>();

                        int index = 0;
                        while (index < msg.length()) {
                            lore.add(ChatColor.GREEN + "$" + msg.substring(index, Math.min(index + 30, msg.length())));
                            index += 30;
                        }
                        meta.setLore(lore);
                        meta.setDisplayName(this.plugin.getLocale().getMessage("general.type.money").getMessage());
                        parseStack.setItemMeta(meta);

                        this.plugin.getLocale().getMessage("interface.kiteditor.addeconomyok").processPlaceholder("amount", msg.trim())
                                .sendPrefixedMessage(this.player);

                        this.inventory.addItem(parseStack);
                        this.player.closeInventory();

                    });
                    this.guiManager.showGUI(event.player, gui);
                });

        setPlayerButton(7, GuiUtils.createButtonItem(XMaterial.SHEEP_SPAWN_EGG,
                        this.plugin.getLocale().getMessage("interface.kiteditor.clone").getMessage(),
                        this.plugin.getLocale().getMessage("interface.kiteditor.clonelore")
                                .getMessage().split("\\|")),
                (event) -> {
                    AnvilGui gui = new AnvilGui(this.player, this);
                    gui.setTitle("Enter a new kit name");
                    gui.setAction(evnt -> {
                        String kitStr = gui.getInputText().toLowerCase().trim();

                        if (this.plugin.getKitManager().getKit(kitStr) != null) {
                            this.plugin.getLocale().getMessage("command.kit.kitalreadyexists").sendPrefixedMessage(this.player);
                            this.player.closeInventory();
                        } else {
                            Kit newKit = this.kit.clone(kitStr);
                            this.plugin.getKitManager().addKit(newKit);

                            restoreItemsInstance();
                            saveKit(this.player, this.inventory, false);
                            XSound.ENTITY_VILLAGER_YES.play(this.player);

                            Bukkit.getScheduler().runTaskLater(this.plugin, () ->
                                    this.guiManager.showGUI(this.player, new KitEditorGui(this.plugin, this.player, newKit, null)), 5L);
                        }
                    });
                    this.guiManager.showGUI(this.player, gui);
                });

        setPlayerButton(8, GuiUtils.createButtonItem(XMaterial.CHEST,
                        this.plugin.getLocale().getMessage("interface.kiteditor.animation").getMessage(),
                        this.plugin.getLocale().getMessage("interface.kiteditor.animationlore")
                                .processPlaceholder("animation", this.kit.getKitAnimation().name())
                                .getMessage().split("\\|")),
                (event) -> {
                    if (this.kit.getKitAnimation() == KitAnimation.NONE) {
                        this.kit.setKitAnimation(KitAnimation.ROULETTE);
                    } else {
                        this.kit.setKitAnimation(KitAnimation.NONE);
                    }
                    setInvItems();
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

        this.kit.saveKit(Arrays.asList(items));
        if (!muteSave) {
            this.plugin.getLocale().getMessage("interface.kiteditor.saved")
                    .processPlaceholder("kit", this.kit.getName())
                    .sendPrefixedMessage(player);
        }
    }

    public void replaceItem(Action action, Player player, ItemStack itemStack, int slot) {
        if (itemStack.getItemMeta().hasLore()) {
            ItemMeta meta = itemStack.getItemMeta();
            List<String> newLore = new ArrayList<>();
            for (String line : meta.getLore()) {
                if (line.contains("Moveable")) {
                    continue;
                }
                if (line.equals(TextUtils.formatText("&8----"))) {
                    break;
                }
                newLore.add(line);
            }
            meta.setLore(newLore);
            itemStack.setItemMeta(meta);
        }

        KitItem item = new KitItem(itemStack);

        switch (action) {
            case CHANCE_UP:
            case CHANCE_DOWN:
                if (action == Action.CHANCE_UP) {
                    item.setChance(item.getChance() >= 100 ? 5 : (item.getChance() + 5));
                } else {
                    item.setChance(item.getChance() <= 0 ? 100 : (item.getChance() - 5));
                }

                setItem(slot, getCompiledMeta(item));
                saveKit(player, this.inventory, true);
                paint();
                break;
            case DISPLAY_ITEM: {
                AnvilGui gui = new AnvilGui(player, this);
                gui.setTitle("Enter a Material");
                gui.setAction(event -> {
                    Optional<XMaterial> compatibleMaterial = XMaterial.matchXMaterial(gui.getInputText().trim());
                    if (!compatibleMaterial.isPresent()) {
                        player.sendMessage("'" + gui.getInputText().trim() + "' is not a valid material.");
                    } else {
                        Material material = compatibleMaterial.get().parseMaterial();
                        KitItem newItem = new KitItem(itemStack);
                        newItem.setDisplayItem(material);
                        setItem(slot, newItem.getMoveableItem());
                        player.closeInventory();
                        saveKit(player, this.inventory, true);
                        paint();
                    }
                });
                this.guiManager.showGUI(player, gui);
            }
            break;
            case DISPLAY_NAME: {
                AnvilGui gui = new AnvilGui(player, this);
                gui.setTitle("Enter a name");
                gui.setAction(event -> {
                    KitItem newItem = new KitItem(itemStack);
                    newItem.setDisplayName(gui.getInputText());
                    setItem(slot, getCompiledMeta(newItem));
                    player.closeInventory();
                    saveKit(player, this.inventory, true);
                    paint();
                });
                this.guiManager.showGUI(player, gui);
            }
            break;
            case DISPLAY_LORE: {
                AnvilGui gui = new AnvilGui(player, this);
                gui.setTitle("Enter lore");
                gui.setAction(event -> {
                    KitItem newItem = new KitItem(itemStack);
                    newItem.setDisplayLore(gui.getInputText());
                    setItem(slot, getCompiledMeta(newItem));
                    player.closeInventory();
                    saveKit(player, this.inventory, true);
                    paint();
                });
                this.guiManager.showGUI(player, gui);
            }
            break;
            default:
                paint();
                break;
        }
    }

    private ItemStack getCompiledMeta(KitItem item) {
        ItemStack is = item.getMoveableItem();
        ItemMeta meta;

        if (is.hasItemMeta()) {
            meta = is.getItemMeta();
        } else {
            meta = Bukkit.getItemFactory().getItemMeta(is.getType());
        }

        List<String> itemLore;

        if (meta.hasLore()) {
            itemLore = meta.getLore();
        } else {
            itemLore = new ArrayList<>();
        }
        itemLore.add(TextUtils.formatText("&8----"));
        itemLore.add(ChatColor.GRAY + this.plugin.getLocale().getMessage("general.type.chance").getMessage().replaceFirst("^" + ChatColor.RESET, "")
                + ": " + ChatColor.GOLD + item.getChance() + "%"); //TODO use a placeholder message in locales
        if (this.isInFunction) {
            itemLore.addAll(Arrays.asList(this.plugin.getLocale().getMessage("interface.kiteditor.itemfunctionlore")
                    .processPlaceholder("item", item.getDisplayItem() == null ? "" : item.getDisplayItem().name())
                    .processPlaceholder("name", item.getDisplayName())
                    .processPlaceholder("lore", item.getDisplayLore())
                    .getMessage().split("\\|")));
        }
        meta.setLore(itemLore);
        is.setItemMeta(meta);
        return is;
    }

    public enum Action {NONE, CHANCE_UP, CHANCE_DOWN, DISPLAY_ITEM, DISPLAY_NAME, DISPLAY_LORE}
}
