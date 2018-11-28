package com.songoda.ultimatekits.kit;

import com.songoda.arconix.api.methods.formatting.TextComponent;
import com.songoda.arconix.plugin.Arconix;
import com.songoda.ultimatekits.Lang;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.gui.GUIConfirmBuy;
import com.songoda.ultimatekits.gui.GUIDisplayKit;
import com.songoda.ultimatekits.key.Key;
import com.songoda.ultimatekits.kit.type.KitContentCommand;
import com.songoda.ultimatekits.kit.type.KitContentEconomy;
import com.songoda.ultimatekits.kit.type.KitContentItem;
import com.songoda.ultimatekits.tasks.CrateAnimateTask;
import com.songoda.ultimatekits.utils.Debugger;
import com.songoda.ultimatekits.utils.Methods;
import com.songoda.ultimatekits.utils.gui.AbstractGUI;
import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.StringUtils;
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

import java.util.*;

/**
 * Created by songoda on 2/24/2017.
 */
public class Kit {

    private final String name, showableName;
    private final UltimateKits plugin;
    private double price;
    private String link, title;
    private int delay;
    private boolean hidden;
    private Material displayItem;
    private List<KitItem> contents;
    private KitAnimation kitAnimation;

    public Kit(String name, String title, String link, double price, Material displayItem, int delay, boolean hidden, List<KitItem> contents, KitAnimation kitAnimation) {
        this.name = name;
        this.showableName = Arconix.pl().getApi().format().formatText(name, true);
        this.price = price;
        this.link = link;
        this.kitAnimation = kitAnimation;
        this.title = title;
        this.delay = delay;
        this.hidden = hidden;
        this.displayItem = displayItem;
        this.contents = contents;
        this.plugin = UltimateKits.getInstance();
    }

    public Kit(String name) {
        this(name, null, null, 0, null, 0, false, new ArrayList<>(), KitAnimation.NONE);
    }

    public void buy(Player player) {
        try {
            if (hasPermission(player) && !player.isOp() && plugin.getConfig().getBoolean("Main.Allow Players To Receive Kits For Free If They Have Permission")) {
                give(player, false, false, false);
                return;
            }

            if (!player.hasPermission("ultimatekits.buy." + name)) {
                player.sendMessage(plugin.getReferences().getPrefix() + Lang.NO_PERM.getConfigValue());
                return;
            }

            if (link != null) {
                player.sendMessage("");
                player.sendMessage(plugin.getReferences().getPrefix() + Arconix.pl().getApi().format().formatText("&a" + link));
                player.sendMessage("");
                player.closeInventory();
            } else if (price != 0) {
                new GUIConfirmBuy(plugin, player, this);
            } else {
                player.sendMessage(Lang.NO_PERM.getConfigValue());
            }
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    private List<ItemStack> getItemContents() {
        List<ItemStack> items = new ArrayList<>();
        for (KitItem item : this.getContents()) {
            if (item.getContent() instanceof KitContentItem)
                items.add(((KitContentItem) item.getContent()).getItemStack());
        }
        return items;
    }

    private boolean hasRoom(Player p, boolean useKey) {
        int space = 0;

        for (ItemStack content : p.getInventory().getContents()) {
            if (content == null) {
                space++;
            }
        }

        int spaceNeeded = getItemContents().size();


        if (useKey) {
            Key key = plugin.getKeyManager().getKey(ChatColor.stripColor(p.getItemInHand().getItemMeta().getLore().get(0)).replace(" Key", ""));
            if (key.getAmt() != -1)
                spaceNeeded = key.getAmt();
        }

        return space >= spaceNeeded;
    }

    public void give(Player player, boolean useKey, boolean economy, boolean console) {
        try {
            if (plugin.getConfig().getBoolean("Main.Prevent The Redeeming of a Kit When Inventory Is Full") && !hasRoom(player, useKey)) {
                player.sendMessage(plugin.getReferences().getPrefix() + Arconix.pl().getApi().format().formatText(Lang.INVENTORY_FULL.getConfigValue()));
                return;
            }
            if (plugin.getConfig().getBoolean("Main.Sounds Enabled") && kitAnimation == KitAnimation.NONE) {
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.6F, 15.0F);
            }
            if (useKey) {
                if (player.getItemInHand().getType() != Material.TRIPWIRE_HOOK || !player.getItemInHand().hasItemMeta()) {
                    return;
                }

                Key key = plugin.getKeyManager().getKey(ChatColor.stripColor(player.getItemInHand().getItemMeta().getLore().get(0)).replace(" Key", ""));

                if (!player.getItemInHand().getItemMeta().getDisplayName().equals(Lang.KEY_TITLE.getConfigValue(showableName)) && !player.getItemInHand().getItemMeta().getDisplayName().equals(Lang.KEY_TITLE.getConfigValue("Any"))) {
                    player.sendMessage(Arconix.pl().getApi().format().formatText(plugin.getReferences().getPrefix() + Lang.WRONG_KEY.getConfigValue()));
                    return;
                }
                for (int i = 0; i < key.getKitAmount(); i++)
                    givePartKit(player, key);
                player.sendMessage(plugin.getReferences().getPrefix() + Arconix.pl().getApi().format().formatText(Lang.KEY_SUCCESS.getConfigValue(showableName)));
                if (player.getInventory().getItemInHand().getAmount() != 1) {
                    ItemStack is = player.getItemInHand();
                    is.setAmount(is.getAmount() - 1);
                    player.setItemInHand(is);
                } else {
                    player.setItemInHand(null);
                }
                return;
            }

            if (getNextUse(player) == -1 && !economy && !console) {
                player.sendMessage(plugin.getReferences().getPrefix() + Arconix.pl().getApi().format().formatText(Lang.NOT_TWICE.getConfigValue(showableName)));
            } else if (getNextUse(player) <= 0 || economy || console) {
                giveKit(player);
                if (economy) {
                    player.sendMessage(plugin.getReferences().getPrefix() + Arconix.pl().getApi().format().formatText(Lang.PURCHASE_SUCCESS.getConfigValue(showableName)));
                } else {
                    updateDelay(player);
                    if (kitAnimation == KitAnimation.NONE) {
                        player.sendMessage(plugin.getReferences().getPrefix() + Arconix.pl().getApi().format().formatText(Lang.GIVE_SUCCESS.getConfigValue(showableName)));
                    }
                }
            } else {
                player.sendMessage(plugin.getReferences().getPrefix() + Arconix.pl().getApi().format().formatText(Lang.DELAY.getConfigValue(Arconix.pl().getApi().format().readableTime(getNextUse(player)))));
            }

        } catch (Exception ex) {
            Debugger.runReport(ex);
        }

    }

    @SuppressWarnings("Duplicates")
    public void display(Player player, AbstractGUI back) {
        try {
            if (!player.hasPermission("previewkit.use")
                    && !player.hasPermission("previewkit." + name)
                    && !player.hasPermission("ultimatekits.use")
                    && !player.hasPermission("ultimatekits." + name)) {
                player.sendMessage(plugin.getReferences().getPrefix() + Lang.NO_PERM.getConfigValue());
                return;
            }
            if (name == null) {
                player.sendMessage(plugin.getReferences().getPrefix() + Lang.KIT_DOESNT_EXIST.getConfigValue(showableName));
                return;
            }

            player.sendMessage(plugin.getReferences().getPrefix() + Lang.PREVIEWING_KIT.getConfigValue(showableName));
            new GUIDisplayKit(plugin, back, player, this);
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }

    }

    public void saveKit(List<ItemStack> items) {
        try {
            List<KitItem> list = new ArrayList<>();
            for (ItemStack is : items) {
                if (is != null && is.getType() != null && is.getType() != Material.AIR) {

                    if (is.getItemMeta().hasLore()) {
                        ItemMeta meta = is.getItemMeta();
                        List<String> newLore = new ArrayList<>();
                        for (String line : meta.getLore()) {
                            if (line.equals(TextComponent.convertToInvisibleString("----"))) break;
                            newLore.add(line);
                        }
                        meta.setLore(newLore);
                        is.setItemMeta(meta);
                    }

                    if (is.getType() == Material.PAPER && ChatColor.stripColor(is.getItemMeta().getDisplayName()).endsWith("Command")) {
                        StringBuilder command = new StringBuilder();
                        for (String line : is.getItemMeta().getLore()) {
                            command.append(line);
                        }
                        list.add(new KitItem(ChatColor.stripColor(command.toString())));
                    } else if (is.getType() == Material.PAPER && ChatColor.stripColor(is.getItemMeta().getDisplayName()).endsWith("Money")) {
                        String money = is.getItemMeta().getLore().get(0);
                        list.add(new KitItem(ChatColor.stripColor(money)));
                    } else {
                        list.add(new KitItem(is));
                    }
                }
            }
            contents = list;
        } catch (Exception e) {
            Debugger.runReport(e);
        }
    }


    public List<ItemStack> getReadableContents(Player player, boolean preview, boolean commands, boolean moveable) {
        List<ItemStack> stacks = new ArrayList<>();
        try {
            for (KitItem item : getContents()) {
                if ((!item.getSerialized().startsWith("/") && !item.getSerialized().startsWith(plugin.getConfig().getString("Main.Currency Symbol"))) || commands) { //ToDO: I doubt this is correct.
                    ItemStack stack = moveable ? item.getMoveableItem() : item.getItem();
                    if (preview) stack = item.getItemForDisplay();

                    ItemStack fin = stack;
                    if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") && stack.getItemMeta().getLore() != null) {
                        ArrayList<String> lore2 = new ArrayList<>();
                        ItemMeta meta2 = stack.getItemMeta();
                        for (String lor : stack.getItemMeta().getLore()) {
                            lor = PlaceholderAPI.setPlaceholders(player, lor.replace(" ", "_")).replace("_", " ");
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
            List<KitItem> innerContents = new ArrayList<>(getContents());
            Collections.shuffle(innerContents);
            int amt = innerContents.size();
            int amtToGive = key == null ? amt : key.getAmt();

            CrateAnimateTask task = null;

            int num = 0;
            for (KitItem item : innerContents) {
                if (amtToGive == 0) continue;
                int ch = item.getChance() == 0 ? 100 : item.getChance();
                double rand = Math.random() * 100;
                if (rand - ch < 0 || ch == 100) {
                    if (item.getContent() instanceof KitContentEconomy) {
                        try {
                            Methods.pay(player, ((KitContentEconomy) item.getContent()).getAmount());
                            player.sendMessage(Lang.ECO_SENT.getConfigValue(Arconix.pl().getApi().format().formatEconomy(((KitContentEconomy) item.getContent()).getAmount())));
                        } catch (NumberFormatException ex) {
                            Debugger.runReport(ex);
                        }
                        amtToGive--;
                        continue;
                    } else if (item.getContent() instanceof KitContentCommand) {
                        String parsed = ((KitContentCommand) item.getContent()).getCommand();
                        parsed = parsed.replace("{player}", player.getName());
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsed);
                        amtToGive--;
                        continue;
                    }

                    ItemStack parseStack = ((KitContentItem) item.getContent()).getItemStack();
                    if (parseStack.getType() == Material.AIR) continue;

                    amtToGive--;

                    if (kitAnimation != KitAnimation.NONE) {
                        final CrateAnimateTask cTask = new CrateAnimateTask(plugin, player, this, item.getItem());
                        task = cTask;
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, cTask::start, 140 * num);
                    } else {
                        Map<Integer, ItemStack> overfilled = player.getInventory().addItem(item.getItem());
                        for (ItemStack item2 : overfilled.values()) {
                            player.getWorld().dropItemNaturally(player.getLocation(), item2);
                        }
                    }
                    num++;
                }
            }
            if (task != null) {
                task.setLast(true);
            }

            player.updateInventory();
        } catch (Exception e) {
            Debugger.runReport(e);
        }
    }

    public void updateDelay(Player player) {
        plugin.getDataFile().getConfig().set("Kits." + name + ".delays." + player.getUniqueId().toString(), System.currentTimeMillis());
    }

    public Long getNextUse(Player player) {
        String configSectionPlayer = "Kits." + name + ".delays." + player.getUniqueId().toString();
        FileConfiguration config = plugin.getDataFile().getConfig();

        if (!config.contains(configSectionPlayer)) {
            return 0L;
        } else if (this.delay == -1) return -1L;


        long last = config.getLong(configSectionPlayer);
        long delay = (long) this.delay * 1000;

        return (last + delay) >= System.currentTimeMillis() ? (last + delay) - System.currentTimeMillis() : 0L;
    }

    public void buyWithEconomy(Player player) {
        try {
            if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) return;
            RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            net.milkbowl.vault.economy.Economy econ = rsp.getProvider();

            if (!hasPermission(player)) {
                player.sendMessage(plugin.getReferences().getPrefix() + Arconix.pl().getApi().format().formatText(Lang.NO_PERM.getConfigValue(showableName)));
                return;
            } else if (!econ.has(player, price)) {
                player.sendMessage(plugin.getReferences().getPrefix() + Arconix.pl().getApi().format().formatText(Lang.CANNOT_AFFORD.getConfigValue(showableName)));
                return;
            }
            if (this.delay > 0) {

                if (getNextUse(player) == -1) {
                    player.sendMessage(plugin.getReferences().getPrefix() + Arconix.pl().getApi().format().formatText(Lang.NOT_TWICE.getConfigValue(showableName)));
                } else if (getNextUse(player) != 0) {
                    player.sendMessage(plugin.getReferences().getPrefix() + Arconix.pl().getApi().format().formatText(Lang.DELAY.getConfigValue(Arconix.pl().getApi().format().readableTime(getNextUse(player)))));
                    return;
                }
            }
            if (delay != 0) {
                updateDelay(player); //updates delay on buy
            }
            econ.withdrawPlayer(player, price);
            give(player, false, true, false);

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

    public List<KitItem> getContents() {
        return this.contents;
    }

    public void setContents(List<KitItem> contents) {
        this.contents = contents;
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

    public KitAnimation getKitAnimation() {
        return kitAnimation;
    }

    public void setKitAnimation(KitAnimation kitAnimation) {
        this.kitAnimation = kitAnimation;
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
