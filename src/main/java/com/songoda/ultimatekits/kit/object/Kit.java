package com.songoda.ultimatekits.kit.object;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.ultimatekits.Lang;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.key.Key;
import com.songoda.ultimatekits.utils.Debugger;
import com.songoda.ultimatekits.utils.Methods;
import com.sun.xml.internal.ws.util.StringUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by songoda on 2/24/2017.
 */
public class Kit {

    private final String name, showableName;

    private double price;
    private String link, title;

    private int delay;

    private boolean hidden;

    private Material displayItem;

    private List<String> contents;

    public Kit(String name, String title, String link, double price, Material displayItem, int delay, boolean hidden, List<String> contents) {
        this.name = name;
        this.showableName = Arconix.pl().getApi().format().formatText(name, true);
        this.price = price;
        this.link = link;
        this.title = title;
        this.delay = delay;
        this.hidden = hidden;
        this.displayItem = displayItem;
        this.contents = contents;
    }

    public Kit(String name) {
        this(name, null, null, 0, null, 0, false, new ArrayList<>());
    }

    public void buy(Player p) {
        try {
            if (hasPermission(p) && UltimateKits.getInstance().getConfig().getBoolean("Main.Allow Players To Receive Kits For Free If They Have Permission")) {
                give(p, false, false, false);
                return;
            }

            if (!p.hasPermission("ultimatekits.buy."+name)) {
                p.sendMessage(UltimateKits.getInstance().references.getPrefix() + Lang.NO_PERM.getConfigValue());
                return;
            }

            if (link != null) {
                p.sendMessage("");
                p.sendMessage(UltimateKits.getInstance().references.getPrefix() + Arconix.pl().getApi().format().formatText("&a" + link));
                p.sendMessage("");
                p.closeInventory();
            } else if (price != 0) {
                confirmBuy(name, p);
            } else {
                p.sendMessage(Lang.NO_PERM.getConfigValue());
            }
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    private List<ItemStack> getItemContents() {
        List<ItemStack> items = new ArrayList<>();
        for (String str : this.getContents()) {
            if ((!str.startsWith("/") && !str.startsWith("$")))
                items.add(Methods.deserializeItemStack(str));
        }
        return items;
    }

    private boolean hasRoom(Player p) {
        int space = 0;

        for (ItemStack content : p.getInventory().getContents()) {
            if (content == null) {
                space++;
            }
        }

        return space >= getItemContents().size();
    }

    public void give(Player p, boolean useKey, boolean economy, boolean console) {
        try {
            if (UltimateKits.getInstance().getConfig().getBoolean("Main.Prevent The Redeeming of a Kit When Inventory Is Full") && !hasRoom(p)) {
                p.sendMessage(UltimateKits.getInstance().references.getPrefix() + Arconix.pl().getApi().format().formatText(Lang.INVENTORY_FULL.getConfigValue()));
                return;
            }
            if (UltimateKits.getInstance().getConfig().getBoolean("Main.Sounds Enabled")) {
                if (!UltimateKits.getInstance().v1_8 && !UltimateKits.getInstance().v1_7) {
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.6F, 15.0F);
                } else {
                    p.playSound(p.getLocation(), Sound.valueOf("LEVEL_UP"), 2F, 15.0F);
                }
            }
            if (useKey) {
                if (p.getItemInHand().getType() != Material.TRIPWIRE_HOOK || !p.getItemInHand().hasItemMeta()) {
                    return;
                }

                Key key = UltimateKits.getInstance().getKeyManager().getKey(ChatColor.stripColor(p.getItemInHand().getItemMeta().getLore().get(0)).replace(" Key", ""));

                if (!p.getItemInHand().getItemMeta().getDisplayName().equals(Lang.KEY_TITLE.getConfigValue(showableName)) && !p.getItemInHand().getItemMeta().getDisplayName().equals(Lang.KEY_TITLE.getConfigValue("All"))) {
                    p.sendMessage(Arconix.pl().getApi().format().formatText(UltimateKits.getInstance().references.getPrefix() + Lang.WRONG_KEY.getConfigValue()));
                    return;
                }
                for (int i = 0; i < key.getKitAmount(); i++)
                    givePartKit(p, key);
                p.sendMessage(UltimateKits.getInstance().references.getPrefix() + Arconix.pl().getApi().format().formatText(Lang.KEY_SUCCESS.getConfigValue(showableName)));
                if (p.getInventory().getItemInHand().getAmount() != 1) {
                    ItemStack is = p.getItemInHand();
                    is.setAmount(is.getAmount() - 1);
                    p.setItemInHand(is);
                } else {
                    p.setItemInHand(null);
                }
                return;
            }

            if (getNextUse(p) == -1 && !economy && !console) {
                p.sendMessage(UltimateKits.getInstance().references.getPrefix() + Arconix.pl().getApi().format().formatText(Lang.NOT_TWICE.getConfigValue(showableName)));
            } else if (getNextUse(p) <= 0 || economy || console) {
                giveKit(p);
                if (economy) {
                    p.sendMessage(UltimateKits.getInstance().references.getPrefix() + Arconix.pl().getApi().format().formatText(Lang.PURCHASE_SUCCESS.getConfigValue(showableName)));
                } else {
                    updateDelay(p);
                    p.sendMessage(UltimateKits.getInstance().references.getPrefix() + Arconix.pl().getApi().format().formatText(Lang.GIVE_SUCCESS.getConfigValue(showableName)));
                }
            } else {
                p.sendMessage(UltimateKits.getInstance().references.getPrefix() + Arconix.pl().getApi().format().formatText(Lang.DELAY.getConfigValue(Arconix.pl().getApi().format().readableTime(getNextUse(p)))));
            }

        } catch (Exception ex) {
            Debugger.runReport(ex);
        }

    }

    @SuppressWarnings("Duplicates")
    public void display(Player p, boolean back) {
        try {
            if (!p.hasPermission("previewkit.use")
                    && !p.hasPermission("previewkit." + name)
                    && !p.hasPermission("ultimatekits.use")
                    && !p.hasPermission("ultimatekits." + name)) {
                p.sendMessage(UltimateKits.getInstance().references.getPrefix() + Lang.NO_PERM.getConfigValue());
                return;
            }
            if (name == null) {
                p.sendMessage(UltimateKits.getInstance().references.getPrefix() + Lang.KIT_DOESNT_EXIST.getConfigValue(showableName));
                return;
            }
            UltimateKits.getInstance().inKit.put(p.getUniqueId(), this);
            p.sendMessage(UltimateKits.getInstance().references.getPrefix() + Lang.PREVIEWING_KIT.getConfigValue(showableName));
            String guititle = Arconix.pl().getApi().format().formatTitle(Lang.PREVIEW_TITLE.getConfigValue(showableName));
            if (title != null) {
                guititle = Lang.PREVIEW_TITLE.getConfigValue(Arconix.pl().getApi().format().formatText(title, true));
            }

            guititle = Arconix.pl().getApi().format().formatText(guititle);

            List<ItemStack> list = getReadableContents(p, true);

            int amt = 0;
            for (ItemStack is : list) {
                if (is.getAmount() > 64) {
                    int overflow = is.getAmount() % 64;
                    int stackamt = is.getAmount() / 64;
                    int num3 = 0;
                    while (num3 != stackamt) {
                        amt++;
                        num3++;
                    }
                    if (overflow != 0) {
                        amt++;
                    }
                } else {
                    amt++;
                }
            }
            boolean buyable = false;
            if (link != null || price != 0) {
                buyable = true;
            }
            int min = 0;
            if (UltimateKits.getInstance().getConfig().getBoolean("Interfaces.Do Not Use Glass Borders")) {
                min = 9;
                if (!buyable) {
                    min = min + 9;
                }
            }
            Inventory i = Bukkit.createInventory(null, 54 - min, Arconix.pl().getApi().format().formatTitle(guititle));
            int max = 54 - min;
            if (amt <= 7) {
                i = Bukkit.createInventory(null, 27 - min, Arconix.pl().getApi().format().formatTitle(guititle));
                max = 27 - min;
            } else if (amt <= 16) {
                i = Bukkit.createInventory(null, 36 - min, Arconix.pl().getApi().format().formatTitle(guititle));
                max = 36 - min;
            } else if (amt <= 23) {
                i = Bukkit.createInventory(null, 45 - min, Arconix.pl().getApi().format().formatTitle(guititle));
                max = 45 - min;
            }


            int num = 0;
            if (!UltimateKits.getInstance().getConfig().getBoolean("Interfaces.Do Not Use Glass Borders")) {
                ItemStack exit = new ItemStack(Material.valueOf(UltimateKits.getInstance().getConfig().getString("Interfaces.Exit Icon")), 1);
                ItemMeta exitmeta = exit.getItemMeta();
                exitmeta.setDisplayName(Lang.EXIT.getConfigValue());
                exit.setItemMeta(exitmeta);
                while (num != 10) {
                    i.setItem(num, Methods.getGlass());
                    num++;
                }
                int num2 = max - 10;
                while (num2 != max) {
                    i.setItem(num2, Methods.getGlass());
                    num2++;
                }
                i.setItem(8, exit);


                i.setItem(0, Methods.getBackgroundGlass(true));
                i.setItem(1, Methods.getBackgroundGlass(true));
                i.setItem(9, Methods.getBackgroundGlass(true));

                i.setItem(7, Methods.getBackgroundGlass(true));
                i.setItem(17, Methods.getBackgroundGlass(true));

                i.setItem(max - 18, Methods.getBackgroundGlass(true));
                i.setItem(max - 9, Methods.getBackgroundGlass(true));
                i.setItem(max - 8, Methods.getBackgroundGlass(true));

                i.setItem(max - 10, Methods.getBackgroundGlass(true));
                i.setItem(max - 2, Methods.getBackgroundGlass(true));
                i.setItem(max - 1, Methods.getBackgroundGlass(true));

                i.setItem(2, Methods.getBackgroundGlass(false));
                i.setItem(6, Methods.getBackgroundGlass(false));
                i.setItem(max - 7, Methods.getBackgroundGlass(false));
                i.setItem(max - 3, Methods.getBackgroundGlass(false));
            }

            if (buyable) {
                ItemStack link = new ItemStack(Material.valueOf(UltimateKits.getInstance().getConfig().getString("Interfaces.Buy Icon")), 1);
                ItemMeta linkmeta = link.getItemMeta();
                linkmeta.setDisplayName(Lang.BUYNOW.getConfigValue());
                ArrayList<String> lore = new ArrayList<>();
                if (hasPermission(p) && UltimateKits.getInstance().getConfig().getBoolean("Main.Allow Players To Receive Kits For Free If They Have Permission")) {
                    lore.add(Lang.CLICKECO.getConfigValue("0"));
                    if (p.isOp()) {
                        lore.add("");
                        lore.add(Arconix.pl().getApi().format().formatText("&7This is free because"));
                        lore.add(Arconix.pl().getApi().format().formatText("&7you have perms for it."));
                        lore.add(Arconix.pl().getApi().format().formatText("&7Everyone else buys"));
                        lore.add(Arconix.pl().getApi().format().formatText("&7this for &a$" + Arconix.pl().getApi().format().formatEconomy(price) + "&7."));
                    }
                } else {
                    lore.add(Lang.CLICKECO.getConfigValue(Arconix.pl().getApi().format().formatEconomy(price)));
                }
                if (delay != 0 && p.isOp()) {
                    lore.add("");
                    lore.add(Arconix.pl().getApi().format().formatText("&7You do not have a delay"));
                    lore.add(Arconix.pl().getApi().format().formatText("&7because you have perms"));
                    lore.add(Arconix.pl().getApi().format().formatText("&7to bypass the delay."));
                }
                linkmeta.setLore(lore);
                link.setItemMeta(linkmeta);
                i.setItem(max - 5, link);
            }

            for (ItemStack is : list) {
                if (!UltimateKits.getInstance().getConfig().getBoolean("Interfaces.Do Not Use Glass Borders")) {
                    if (num == 17)
                        num++;
                    if (num == (max - 18))
                        num++;
                }
                if (is.getAmount() > 64) {
                    if (is.hasItemMeta() && is.getItemMeta().hasLore()) {
                        ArrayList<String> lore = new ArrayList<>();
                        for (String str : is.getItemMeta().getLore()) {
                            str = str.replace("{PLAYER}", p.getName());
                            str = str.replace("<PLAYER>", p.getName());
                            lore.add(str);
                        }
                        is.getItemMeta().setLore(lore);

                    }
                    int overflow = is.getAmount() % 64;
                    int stackamt = is.getAmount() / 64;
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
                    continue;
                }
                if (!UltimateKits.getInstance().getConfig().getBoolean("Main.Dont Preview Commands In Kits") || is.getType() != Material.PAPER || !is.getItemMeta().hasDisplayName() || !is.getItemMeta().getDisplayName().equals(Lang.COMMAND.getConfigValue())) {
                    i.setItem(num, is);
                    num++;
                }
            }

            if (back && !UltimateKits.getInstance().getConfig().getBoolean("Interfaces.Do Not Use Glass Borders")) {

                ItemStack head2 = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
                ItemStack skull2 = head2;
                if (!UltimateKits.getInstance().v1_7)
                    skull2 = Arconix.pl().getApi().getGUI().addTexture(head2, "http://textures.minecraft.net/texture/3ebf907494a935e955bfcadab81beafb90fb9be49c7026ba97d798d5f1a23");
                SkullMeta skull2Meta = (SkullMeta) skull2.getItemMeta();
                if (UltimateKits.getInstance().v1_7)
                    skull2Meta.setOwner("MHF_ArrowLeft");
                skull2.setDurability((short) 3);
                skull2Meta.setDisplayName(Lang.BACK.getConfigValue());
                skull2.setItemMeta(skull2Meta);
                i.setItem(0, skull2);
            }

            p.openInventory(i);
            UltimateKits.getInstance().whereAt.remove(p.getUniqueId());
            UltimateKits.getInstance().whereAt.put(p.getUniqueId(), "display");
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }

    }

    public void saveKit(List<ItemStack> items) {
        try {
            List<String> list = new ArrayList<>();
            for (ItemStack is : items) {
                if (is != null && is.getType() != null && is.getType() != Material.AIR) {
                    if (is.getType() == Material.PAPER && ChatColor.stripColor(is.getItemMeta().getDisplayName()).equals("Command")) {
                        StringBuilder command = new StringBuilder();
                        for (String line : is.getItemMeta().getLore()) {
                            command.append(line);
                        }
                        list.add(ChatColor.stripColor(command.toString()));
                    } else if (is.getType() == Material.PAPER && ChatColor.stripColor(is.getItemMeta().getDisplayName()).equals("Money")) {
                        String money = is.getItemMeta().getLore().get(0);
                        list.add(ChatColor.stripColor(money));
                    } else {
                        String serialized = Methods.serializeItemStack(is);
                        list.add(serialized);
                    }
                }
            }
            contents = list;
        } catch (Exception e) {
            Debugger.runReport(e);
        }
    }


    public List<ItemStack> getReadableContents(Player player, boolean commands) {
        List<ItemStack> stacks = new ArrayList<>();
        try {
            for (String str : getContents()) {
                if ((!str.startsWith("/") && !str.startsWith("$")) || commands) {
                    ItemStack parseStack;
                    if (str.startsWith("$")) {
                        parseStack = new ItemStack(Material.PAPER, 1);
                        ItemMeta meta = parseStack.getItemMeta();
                        ArrayList<String> lore = new ArrayList<>();
                        lore.add("§a" + str.trim());
                        meta.setLore(lore);
                        meta.setDisplayName(Lang.MONEY.getConfigValue());
                        parseStack.setItemMeta(meta);
                    } else if (str.startsWith("/")) {
                        parseStack = new ItemStack(Material.PAPER, 1);
                        ItemMeta meta = parseStack.getItemMeta();

                        ArrayList<String> lore = new ArrayList<>();

                        int index = 0;
                        while (index < str.length()) {
                            lore.add(Arconix.pl().getApi().format().formatText("&a" + str.substring(index, Math.min(index + 30, str.length()))));
                            index += 30;
                        }
                        meta.setLore(lore);
                        meta.setDisplayName(Lang.COMMAND.getConfigValue());
                        parseStack.setItemMeta(meta);
                    } else {
                        parseStack = Methods.deserializeItemStack(str);
                    }
                    ItemStack fin = parseStack;
                    if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") && parseStack.getItemMeta().getLore() != null) {
                        ArrayList<String> lore2 = new ArrayList<>();
                        ItemMeta meta2 = parseStack.getItemMeta();
                        for (String lor : parseStack.getItemMeta().getLore()) {
                            lor = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, lor.replace(" ", "_")).replace("_", " ");
                            lore2.add(lor);
                        }
                        meta2.setLore(lore2);
                        fin.setItemMeta(meta2);
                    }
                    stacks.add(fin);
                }
            }
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return stacks;
    }

    public void giveKit(Player player) {
        try {
            givePartKit(player, null);
        } catch (Exception e) {
            Debugger.runReport(e);
        }
    }

    public void givePartKit(Player player, Key key) {
        try {

            List<String> innerContents = new ArrayList<>(getContents());
            int amt = innerContents.size();

            if (key != null && key.getAmt() != -1 && key.getAmt() < innerContents.size()) {
                amt = innerContents.size() - key.getAmt();
            }

            while (key != null && amt != 0 && key.getAmt() != -1) {
                int num = ThreadLocalRandom.current().nextInt(0, innerContents.size());
                innerContents.remove(num);
                amt--;
            }
            for (String line : innerContents) {
                if (line.startsWith("$")) {
                    try {
                        Methods.pay(player, Double.parseDouble(line.substring("$".length()).trim()));
                        player.sendMessage(Lang.ECO_SENT.getConfigValue(Arconix.pl().getApi().format().formatEconomy(Double.parseDouble(line.substring("$".length()).trim()))));
                    } catch (NumberFormatException ex) {
                        Debugger.runReport(ex);
                    }
                    continue;
                } else if (line.startsWith("/")) {
                    String parsed = line.substring(1);
                    parsed = parsed.replace("{player}", player.getName());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsed); // Not proud of this.
                    continue;
                }

                ItemStack parseStack = Methods.deserializeItemStack(line);
                if (parseStack.getType() == Material.AIR) continue;

                Map<Integer, ItemStack> overfilled = player.getInventory().addItem(parseStack);


                for (ItemStack item : overfilled.values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), item);
                }
            }

            player.updateInventory();
        } catch (Exception e) {
            Debugger.runReport(e);
        }
    }

    public void updateDelay(Player player) {
        UltimateKits.getInstance().getDataFile().getConfig().set("Kits." + name + ".delays." + player.getUniqueId().toString(), System.currentTimeMillis());
    }

    public Long getNextUse(Player player) {
        String configSectionPlayer = "Kits." + name + ".delays." + player.getUniqueId().toString();
        FileConfiguration config = UltimateKits.getInstance().getDataFile().getConfig();

        if (!config.contains(configSectionPlayer)) {
            return 0L;
        } else if (this.delay == -1) return -1L;


        long last = config.getLong(configSectionPlayer);
        long delay = (long) this.delay * 1000;

        return (last + delay) >= System.currentTimeMillis() ? (last + delay) - System.currentTimeMillis() : 0L;
    }

    public void confirmBuy(String kitName, Player p) {
        try {

            double cost = price;
            if (hasPermission(p) && UltimateKits.getInstance().getConfig().getBoolean("Main.Allow Players To Receive Kits For Free If They Have Permission")) {
                cost = 0;
            }
            Inventory i = Bukkit.createInventory(null, 27, Arconix.pl().getApi().format().formatTitle(Lang.GUI_TITLE_YESNO.getConfigValue(cost)));

            String title = Arconix.pl().getApi().format().formatTitle("&c" + StringUtils.capitalize(kitName.toLowerCase()));
            ItemStack item = new ItemStack(Material.DIAMOND_HELMET);
            if (displayItem != null) item = new ItemStack(displayItem);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(title);
            ArrayList<String> lore = new ArrayList<>();
            lore.add(Arconix.pl().getApi().format().formatText("&a$" + Arconix.pl().getApi().format().formatEconomy(cost)));

            int nu = 0;
            while (nu != 27) {
                i.setItem(nu, Methods.getGlass());
                nu++;
            }

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

            ItemStack item2 = new ItemStack(Material.valueOf(UltimateKits.getInstance().getConfig().getString("Interfaces.Buy Icon")), 1);
            ItemMeta itemmeta2 = item2.getItemMeta();
            itemmeta2.setDisplayName(Lang.YES_GUI.getConfigValue());
            item2.setItemMeta(itemmeta2);

            ItemStack item3 = new ItemStack(Material.valueOf(UltimateKits.getInstance().getConfig().getString("Interfaces.Exit Icon")), 1);
            ItemMeta itemmeta3 = item3.getItemMeta();
            itemmeta3.setDisplayName(Lang.NO_GUI.getConfigValue());
            item3.setItemMeta(itemmeta3);

            i.setItem(4, item);
            i.setItem(11, item2);
            i.setItem(15, item3);

            Bukkit.getScheduler().scheduleSyncDelayedTask(UltimateKits.getInstance(), () -> {
                p.openInventory(i);
                UltimateKits.getInstance().buy.put(p.getUniqueId(), kitName);
            }, 1);
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public void buyWithEconomy(Player p) {
        try {
            if (UltimateKits.getInstance().getServer().getPluginManager().getPlugin("Vault") == null) return;
            RegisteredServiceProvider<Economy> rsp = UltimateKits.getInstance().getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);

            net.milkbowl.vault.economy.Economy econ = rsp.getProvider();
            if (!econ.has(p, price) && !hasPermission(p)) {
                if (!hasPermission(p))
                    p.sendMessage(UltimateKits.getInstance().references.getPrefix() + Arconix.pl().getApi().format().formatText(Lang.NO_PERM.getConfigValue(showableName)));
                else
                    p.sendMessage(UltimateKits.getInstance().references.getPrefix() + Arconix.pl().getApi().format().formatText(Lang.CANNOT_AFFORD.getConfigValue(showableName)));
                return;
            }
            if (this.delay > 0) {

                if (getNextUse(p) == -1) {
                    p.sendMessage(UltimateKits.getInstance().references.getPrefix() + Arconix.pl().getApi().format().formatText(Lang.NOT_TWICE.getConfigValue(showableName)));
                } else if (getNextUse(p) != 0) {
                    p.sendMessage(UltimateKits.getInstance().references.getPrefix() + Arconix.pl().getApi().format().formatText(Lang.DELAY.getConfigValue(Arconix.pl().getApi().format().readableTime(getNextUse(p)))));
                    return;
                }
            }
            if (delay != 0) {
                updateDelay(p); //updates delay on buy
            }
            econ.withdrawPlayer(p, price);
            give(p, false, true, false);

        } catch (Exception ex) {
            Debugger.runReport(ex);
        }

    }

    public boolean hasPermission(Player player) {
        try {
            if (player.hasPermission("uc.kit." + name.toLowerCase())) return true;
            if (player.hasPermission("essentials.kit." + name.toLowerCase())) return true;
            if (player.hasPermission("ultimatekits.kit." + name.toLowerCase())) return true;
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return false;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public List<String> getContents() {
        return this.contents;
    }

    public String getName() {
        return name;
    }

    public String getShowableName() {
        return showableName;
    }

    public Material getDisplayItem() {
        return displayItem;
    }

    public void setDisplayItem(Material displayItem) {
        this.displayItem = displayItem;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public void setContents(List<String> contents) {
        this.contents = contents;
    }

    @Override
    public int hashCode() {
        return 31 * (name != null ? name.hashCode() : 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Kit)) return false;

        Kit kit = (Kit) o;
        return Objects.equals(name, kit.name);
    }

}
