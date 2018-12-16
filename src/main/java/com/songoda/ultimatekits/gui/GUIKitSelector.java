package com.songoda.ultimatekits.gui;

import com.songoda.arconix.api.methods.formatting.TextComponent;
import com.songoda.arconix.plugin.Arconix;
import com.songoda.ultimatekits.Lang;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.utils.Methods;
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
            if (kitList.size() > n + 34)
                max = max + 34;
            else if (kitList.size() > n + 25)
                max = max + 25;
            else if (kitList.size() > n + 16)
                max = max + 16;
            else if (kitList.size() > n + 7)
                max = max + 7;
        }
        if (glassless) max -= 18;

        init(Lang.KITS_TITLE.getConfigValue(), max);

        timer = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            Bukkit.broadcastMessage("af");
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
        ItemStack exit = new ItemStack(Material.valueOf(plugin.getConfig().getString("Interfaces.Exit Icon")), 1);
        ItemMeta exitmeta = exit.getItemMeta();
        exitmeta.setDisplayName(Lang.EXIT.getConfigValue());
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
            if (!glassless && index == 17) index = 19;
            if (id > kitList.size() - 1) {
                inventory.setItem(index, new ItemStack(Material.AIR));
                continue;
            }
            String kitItem = kitList.get(id);

            Kit kit = plugin.getKitManager().getKit(kitItem);

            String title = Lang.GUI_KIT_NAME.getConfigValue(Arconix.pl().getApi().format().formatText(kitItem, true));
            if (kit.getTitle() != null)
                title = Arconix.pl().getApi().format().formatText(kit.getTitle());

            ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
            if (kit.getDisplayItem() != null)
                item.setType(kit.getDisplayItem());
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(TextComponent.convertToInvisibleString(kitItem + ":") + Arconix.pl().getApi().format().formatText(title));
            ArrayList<String> lore = new ArrayList<>();
            if (kit.getPrice() != 0)
                lore.add(Arconix.pl().getApi().format().formatText("&7This kit costs &a$" + kit.getPrice() + "&7."));
            else if (kit.getLink() != null)
                lore.add(Lang.LINK.getConfigValue());


            if (!kitsmode) {
                if (!Lang.ABOUT_KIT.getConfigValue().trim().equals("")) {
                    String[] parts = Lang.ABOUT_KIT.getConfigValue().split("\\|");
                    lore.add("");
                    for (String line : parts)
                        lore.add(Arconix.pl().getApi().format().formatText(line));
                }
                if (kit.hasPermission(player)) {
                    if (kit.getNextUse(player) == -1) {
                        lore.add(Arconix.pl().getApi().format().formatText(Lang.ONCE.getConfigValue()));
                    } else if (kit.getNextUse(player) > 0) {
                        if (!Lang.PLEASE_WAIT.getConfigValue().trim().equals("")) {
                            lore.add(Arconix.pl().getApi().format().formatText(Lang.PLEASE_WAIT.getConfigValue(Arconix.pl().getApi().format().readableTime(kit.getNextUse(player)))));
                        }
                    } else if (!Lang.READY.getConfigValue().trim().equals("")) {
                        lore.add(Arconix.pl().getApi().format().formatText(Lang.READY.getConfigValue()));
                    }
                } else
                    lore.add(Arconix.pl().getApi().format().formatText(Lang.NO_ACCESS.getConfigValue()));
                lore.add("");
                lore.add(Arconix.pl().getApi().format().formatText(Lang.LEFT_PREVIEW.getConfigValue()));
                if (kit.hasPermission(player)) {
                    lore.add(Arconix.pl().getApi().format().formatText(Lang.RIGHT_CLAIM.getConfigValue()));
                } else if (kit.getPrice() != 0 || kit.getLink() != null) {
                    lore.add(Arconix.pl().getApi().format().formatText(Lang.RIGHT_BUY.getConfigValue()));
                }

                if (player.hasPermission("ultimatekits.admin")) {
                    lore.add("");
                    lore.add(Arconix.pl().getApi().format().formatText("&6Middle Click &7to edit positioning."));
                }
            } else {
                lore.add(Arconix.pl().getApi().format().formatText("&6&lEdit Mode"));

                lore.add("");
                lore.add(Arconix.pl().getApi().format().formatText("&6Left Click &7to move kit left"));
                lore.add(Arconix.pl().getApi().format().formatText("&6Right Click &7to move kit right"));
                lore.add("");

                lore.add(Arconix.pl().getApi().format().formatText("&6Middle Click &7to go back."));
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
            inventory.setItem(index, item);
            id++;
        }

        ItemStack info = new ItemStack(Material.BOOK, 1);
        ItemMeta infometa = info.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        String[] parts = Lang.DETAILS.getConfigValue(player.getName()).split("\\|");
        boolean hit = false;
        for (String line : parts) {
            if (!hit)
                infometa.setDisplayName(Arconix.pl().getApi().format().formatText(line));
            else
                lore.add(Arconix.pl().getApi().format().formatText(line));
            hit = true;
        }
        infometa.setLore(lore);
        info.setItemMeta(infometa);

        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1, (byte) 3);
        ItemStack skull = Arconix.pl().getApi().getGUI().addTexture(head, "http://textures.minecraft.net/texture/1b6f1a25b6bc199946472aedb370522584ff6f4e83221e5946bd2e41b5ca13b");
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skull.setDurability((short) 3);
        skullMeta.setDisplayName(Lang.NEXT.getConfigValue());
        skull.setItemMeta(skullMeta);

        ItemStack head2 = new ItemStack(Material.PLAYER_HEAD, 1, (byte) 3);
        ItemStack skull2 = Arconix.pl().getApi().getGUI().addTexture(head2, "http://textures.minecraft.net/texture/3ebf907494a935e955bfcadab81beafb90fb9be49c7026ba97d798d5f1a23");
        SkullMeta skull2Meta = (SkullMeta) skull2.getItemMeta();
        skull2.setDurability((short) 3);
        skull2Meta.setDisplayName(Lang.LAST.getConfigValue());
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

        registerClickable(glassless ? 0 : 10, glassless ? max : max - 11, (player1, inventory1, cursor, slot, type) -> {
            String kitName = inventory1.getItem(slot).getItemMeta().getDisplayName().replace(String.valueOf(ChatColor.COLOR_CHAR), "").split(":")[0];

            if (type == ClickType.MIDDLE && player.hasPermission("ultimatekits.admin")) {
                kitsmode = !kitsmode;
                constructGUI();
                return;
            }

            Kit kit = plugin.getKitManager().getKit(kitName);

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
        });

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
