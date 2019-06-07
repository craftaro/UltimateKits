package com.songoda.ultimatekits.gui;

import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.utils.Methods;
import com.songoda.ultimatekits.utils.ServerVersion;
import com.songoda.ultimatekits.utils.gui.AbstractGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class GUIKitSelector extends AbstractGUI {

    private Player player;
    private UltimateKits plugin;

    private int timer;
    private int page = 1;
    private int max;
    private List<String> kitList;
    private boolean kitsmode = false;
    
    private boolean glassless;

    public GUIKitSelector(UltimateKits plugin, Player player) {
        super(player);
        this.player = player;
        this.plugin = plugin;

        kitList = new ArrayList<>();

        setUpPage();
        
        glassless = plugin.getConfig().getBoolean("Interfaces.Do Not Use Glass Borders");

        int n = 7;
        if (glassless)
            n = 9;
        max = 27;
        if (kitList.size() > n) {
            max = 36;
        }
        if (glassless) {
            if (kitList.size() > n + 36)
                max = max + 36;
            else if (kitList.size() > n + 27)
                max = max + 27;
            else if (kitList.size() > n + 18)
                max = max + 18;
            else if (kitList.size() > n + 9)
                max = max + 9;
        }
        if (glassless) max -= 18;

        init(plugin.getLocale().getMessage("interface.selector.title"), max);

        timer = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (inventory.getViewers().isEmpty()) return;
            constructGUI();
        }, 20L, 20L);
    }

    private void setUpPage() {
        int ino = 14;
        if (plugin.getConfig().getBoolean("Interfaces.Do Not Use Glass Borders")) ino = 54;
        int num = 0;
        int start = (page - 1) * ino;
        int show = 1;
        kitList.clear();
        for (Kit kit : plugin.getKitManager().getKits()) {
            if (!kit.isHidden()
                    && (!plugin.getConfig().getBoolean("Main.Only Show Players Kits They Have Permission To Use") || kit.hasPermission(player))
                    && num >= start
                    && show <= ino) {
                kitList.add(kit.getName());
                show++;
            }
            num++;
        }
    }

    @Override
    protected void constructGUI() {
        resetClickables();
        registerClickables();

        ItemStack exit = new ItemStack(Material.valueOf(plugin.getConfig().getString("Interfaces.Exit Icon")), 1);
        ItemMeta exitmeta = exit.getItemMeta();
        exitmeta.setDisplayName(UltimateKits.getInstance().getLocale().getMessage("interface.button.exit"));
        exit.setItemMeta(exitmeta);

        int num = 0;
        if (!glassless) {
            while (num != max) {
                ItemStack glass = Methods.getGlass();
                inventory.setItem(num, glass);
                num++;
            }

            inventory.setItem(0, Methods.getBackgroundGlass(true));
            inventory.setItem(1, Methods.getBackgroundGlass(true));
            inventory.setItem(9, Methods.getBackgroundGlass(true));

            inventory.setItem(7, Methods.getBackgroundGlass(true));
            inventory.setItem(8, Methods.getBackgroundGlass(true));
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

        num = glassless ? 0 : 10;
        int id = 0;
        int tmax = max;
        if (!glassless)
            tmax = tmax - 10;
        for (int index = num; index != tmax; index++) {
            if (!glassless) {
                if (index == 17 || index == (max - 18)) index++;
                if (index == 18 && max == 36) index++;
            }
            if (id > kitList.size() - 1) {
                inventory.setItem(index, new ItemStack(Material.AIR));
                continue;
            }
            String kitItem = kitList.get(id);

            Kit kit = plugin.getKitManager().getKit(kitItem);

            String title = plugin.getLocale().getMessage("interface.selector.kit", Methods.formatText(kitItem, true));
            if (kit.getTitle() != null)
                title = Methods.formatText(kit.getTitle());

            ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
            if (kit.getDisplayItem() != null)
                item.setType(kit.getDisplayItem());
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(Methods.convertToInvisibleString(kitItem + ":") + Methods.formatText(title));
            ArrayList<String> lore = new ArrayList<>();
            if (kit.getPrice() != 0)
                lore.add(Methods.formatText("&7This kit costs &a$" + kit.getPrice() + "&7."));
            else if (kit.getLink() != null)
                lore.add(plugin.getLocale().getMessage("general.type.link"));


            if (!kitsmode) {
                if (!plugin.getLocale().getMessage("interface.selector.aboutkit").trim().equals("")) {
                    String[] parts = plugin.getLocale().getMessage("interface.selector.aboutkit").split("\\|");
                    lore.add("");
                    for (String line : parts)
                        lore.add(Methods.formatText(line));
                }
                if (kit.hasPermission(player)) {
                    if (kit.getNextUse(player) == -1) {
                        lore.add(plugin.getLocale().getMessage("event.claim.once"));
                    } else if (kit.getNextUse(player) > 0) {
                        if (!plugin.getLocale().getMessage("event.claim.wait").trim().equals("")) {
                            lore.add(plugin.getLocale().getMessage("event.claim.wait", Methods.makeReadable(kit.getNextUse(player))));
                        }
                    } else if (!plugin.getLocale().getMessage("event.claim.ready").trim().equals("")) {
                        lore.add(plugin.getLocale().getMessage("event.claim.ready"));
                    }
                } else
                    lore.add(plugin.getLocale().getMessage("event.claim.noaccess"));
                lore.add("");
                lore.add(plugin.getLocale().getMessage("interface.selector.leftpreview"));
                if (kit.hasPermission(player)) {
                    lore.add(plugin.getLocale().getMessage("interface.selector.rightclaim"));
                } else if (kit.getPrice() != 0 || kit.getLink() != null) {
                    lore.add(plugin.getLocale().getMessage("interface.selector.rightbuy"));
                }

                if (player.hasPermission("ultimatekits.admin")) {
                    lore.add("");
                    lore.add(Methods.formatText("&6Middle Click &7to edit positioning."));
                }
            } else {
                lore.add(Methods.formatText("&6&lEdit Mode"));

                lore.add("");
                lore.add(Methods.formatText("&6Left Click &7to move kit left"));
                lore.add(Methods.formatText("&6Right Click &7to move kit right"));
                lore.add("");

                lore.add(Methods.formatText("&6Middle Click &7to go back."));
            }
            meta.setLore(lore);
            item.setItemMeta(meta);


            inventory.setItem(index, item);
            registerClickable(index, ((player1, inventory1, cursor, slot, type) -> {
                if (type == ClickType.MIDDLE && player.hasPermission("ultimatekits.admin")) {
                    kitsmode = !kitsmode;
                    constructGUI();
                    return;
                }

                if (kitsmode) {
                    if (type == ClickType.RIGHT) {
                        plugin.getKitManager().moveKit(kit, true);
                    } else if (type == ClickType.LEFT) {
                        plugin.getKitManager().moveKit(kit, false);
                    }
                    setUpPage();
                    constructGUI();
                    return;
                }


                if (type == ClickType.LEFT) {
                    kit.display(player, this);
                    return;
                }

                if (type == ClickType.RIGHT) {
                    kit.buy(player);
                    constructGUI();
                }
            }));

            id++;
        }

        ItemStack info = new ItemStack(Material.BOOK, 1);
        ItemMeta infometa = info.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        String[] parts = plugin.getLocale().getMessage("interface.selector.details", player.getName()).split("\\|");
        boolean hit = false;
        for (String line : parts) {
            if (!hit)
                infometa.setDisplayName(Methods.formatText(line));
            else
                lore.add(Methods.formatText(line));
            hit = true;
        }
        infometa.setLore(lore);
        info.setItemMeta(infometa);

        ItemStack head = new ItemStack(plugin.isServerVersionAtLeast(ServerVersion.V1_13) ? Material.PLAYER_HEAD : Material.valueOf("SKULL_ITEM"), 1, (byte) 3);
        ItemStack skull = Methods.addTexture(head, "http://textures.minecraft.net/texture/1b6f1a25b6bc199946472aedb370522584ff6f4e83221e5946bd2e41b5ca13b");
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skull.setDurability((short) 3);
        skullMeta.setDisplayName(plugin.getLocale().getMessage("interface.button.next"));
        skull.setItemMeta(skullMeta);

        ItemStack skull2 = Methods.addTexture(head, "http://textures.minecraft.net/texture/3ebf907494a935e955bfcadab81beafb90fb9be49c7026ba97d798d5f1a23");
        SkullMeta skull2Meta = (SkullMeta) skull2.getItemMeta();
        skull2.setDurability((short) 3);
        skull2Meta.setDisplayName(plugin.getLocale().getMessage("interface.button.next"));
        skull2.setItemMeta(skull2Meta);

        if (!plugin.getConfig().getBoolean("Interfaces.Do Not Use Glass Borders"))
            inventory.setItem(max - 5, exit);
        if (kitList.size() == 14)
            inventory.setItem(max - 4, skull);
        if (page != 1)
            inventory.setItem(max - 6, skull2);
        if (!plugin.getConfig().getBoolean("Interfaces.Do Not Use Glass Borders"))
            inventory.setItem(4, info);
    }

    @Override
    protected void registerClickables() {

        registerClickable(max - 5, (player, inventory, cursor, slot, type) -> player.closeInventory());

        registerClickable(max - 6, ((player1, inventory1, cursor, slot, type) -> {
            if (page == 1) return;
            page --;
            setUpPage();
            constructGUI();
        }));

        registerClickable(max - 4, ((player1, inventory1, cursor, slot, type) -> {
            if (kitList.size() == 14) {
                page++;
                setUpPage();
                constructGUI();
            }
        }));
    }

    @Override
    protected void registerOnCloses() {
        registerOnClose(((player1, inventory1) -> Bukkit.getScheduler().cancelTask(timer)));
    }

}
