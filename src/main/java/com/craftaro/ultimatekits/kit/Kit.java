package com.craftaro.ultimatekits.kit;

import com.craftaro.core.compatibility.CompatibleHand;
import com.craftaro.core.configuration.Config;
import com.craftaro.core.gui.Gui;
import com.craftaro.core.gui.GuiManager;
import com.craftaro.core.hooks.EconomyManager;
import com.craftaro.core.third_party.de.tr7zw.nbtapi.NBTItem;
import com.craftaro.core.utils.ItemUtils;
import com.craftaro.core.utils.TextUtils;
import com.craftaro.core.utils.TimeUtils;
import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.ultimatekits.UltimateKits;
import com.craftaro.ultimatekits.category.Category;
import com.craftaro.ultimatekits.crate.Crate;
import com.craftaro.ultimatekits.gui.AnimatedKitGui;
import com.craftaro.ultimatekits.gui.ConfirmBuyGui;
import com.craftaro.ultimatekits.gui.PreviewKitGui;
import com.craftaro.ultimatekits.key.Key;
import com.craftaro.ultimatekits.kit.type.KitContentCommand;
import com.craftaro.ultimatekits.kit.type.KitContentEconomy;
import com.craftaro.ultimatekits.settings.Settings;
import com.craftaro.ultimatekits.utils.ArmorType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by songoda on 2/24/2017.
 */
public class Kit implements Cloneable {

    private String key;
    private String name;
    private Category category = null;

    private static UltimateKits plugin;
    private double price = 0;
    private String link, title = null;
    private long delay = 0L;
    private boolean hidden = false;
    private ItemStack displayItem = null;
    private List<KitItem> contents = new ArrayList<>();
    private KitAnimation kitAnimation = KitAnimation.NONE;

    public Kit(String key) {
        if (plugin == null) {
            plugin = UltimateKits.getInstance();
        }
        this.key = key;
        this.name = TextUtils.formatText(key, true);
    }


    public boolean hasRoom(Player player, int itemAmount) {
        int space = 0;

        for (ItemStack content : player.getInventory().getContents())
            if (content == null)
                space++;

        // Since roulette only gives one item, we don't need to check if the user has room for the whole kit.
        if (this.kitAnimation == KitAnimation.ROULETTE && space >= 1) {
            return true;
        }

        return space >= itemAmount;
    }

    public void saveKit(List<ItemStack> items) {
        List<KitItem> list = new ArrayList<>();
        for (ItemStack is : items) {
            if (is != null && is.getType() != Material.AIR) {

                if (is.getItemMeta().hasLore()) {
                    ItemMeta meta = is.getItemMeta();
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
                    is.setItemMeta(meta);
                }

                if (is.getType() == Material.PAPER && is.getItemMeta().getDisplayName()
                        .equals(UltimateKits.getInstance().getLocale().getMessage("general.type.command")
                                .getMessage())) {
                    StringBuilder command = new StringBuilder();
                    for (String line : is.getItemMeta().getLore()) {
                        command.append(line);
                    }
                    list.add(new KitItem(is, ChatColor.stripColor(command.toString())));
                } else if (is.getType() == Material.PAPER && is.getItemMeta().getDisplayName()
                        .equals(UltimateKits.getInstance().getLocale().getMessage("general.type.money")
                                .getMessage())) {
                    String money = is.getItemMeta().getLore().get(0);
                    list.add(new KitItem(is, ChatColor.stripColor(money)));
                } else {
                    list.add(new KitItem(is));
                }
            }
        }
        this.contents = list;
        plugin.saveKits(false);
    }


    public List<ItemStack> getReadableContents(Player player, boolean preview, boolean commands, boolean moveable) {
        List<ItemStack> stacks = new ArrayList<>();
        for (KitItem item : getContents()) {
            if ((!item.getSerialized().startsWith("/") && !item.getSerialized().startsWith(Settings.CURRENCY_SYMBOL.getString())) || commands) { //ToDO: I doubt this is correct.
                ItemStack stack = moveable ? item.getMoveableItem() : item.getItem();
                if (preview) {
                    stack = item.getItemForDisplay();
                }
                if (stack == null) {
                    continue;
                }

                ItemStack fin = stack;
                if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") && stack.getItemMeta().getLore() != null) {
                    ArrayList<String> lore2 = new ArrayList<>();
                    ItemMeta meta2 = stack.getItemMeta();
                    for (String lor : stack.getItemMeta().getLore()) {
                        lor = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, lor.replace(" ", "_")).replace("_", " ");
                        lore2.add(lor);
                    }
                    meta2.setLore(lore2);
                    fin.setItemMeta(meta2);
                }
                stacks.add(fin);
            }
        }
        return stacks;
    }

    public void updateDelay(Player player) {
        plugin.getDataFile().set("Kits." + this.key + ".delays." + player.getUniqueId(), System.currentTimeMillis());
    }

    public Long getNextUse(Player player) {
        String configSectionPlayer = "Kits." + this.key + ".delays." + player.getUniqueId();
        Config config = plugin.getDataFile();

        if (!config.contains(configSectionPlayer)) {
            return 0L;
        } else if (this.delay == -1) {
            return -1L;
        }

        long last = config.getLong(configSectionPlayer);
        long delay = this.delay * 1000;

        return (last + delay) >= System.currentTimeMillis() ? (last + delay) - System.currentTimeMillis() : 0L;
    }

    public boolean hasPermissionToClaim(Player player) {
        return player.hasPermission("ultimatekits.claim." + this.key.toLowerCase());
    }

    public boolean hasPermissionToPreview(Player player) {
        return player.hasPermission("ultimatekits.preview." + this.key.toLowerCase());
    }

    public boolean hasPermissionToBuy(Player player) {
        return player.hasPermission("ultimatekits.buy." + this.key.toLowerCase());
    }

    public double getPrice() {
        return this.price;
    }

    public Kit setPrice(double price) {
        this.price = price;
        return this;
    }

    public String getLink() {
        return this.link;
    }

    public Kit setLink(String link) {
        this.link = link;
        return this;
    }

    public String getTitle() {
        return this.title;
    }

    public Kit setTitle(String title) {
        this.title = title;
        return this;
    }

    public long getDelay() {
        return this.delay;
    }

    public Kit setDelay(long delay) {
        this.delay = delay;
        return this;
    }

    public Category getCategory() {
        return this.category;
    }

    public Kit setCategory(Category category) {
        this.category = category;
        return this;
    }

    public List<KitItem> getContents() {
        return Collections.unmodifiableList(this.contents);
    }

    public Kit setContents(List<KitItem> contents) {
        this.contents = contents;
        return this;
    }

    public String getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    public ItemStack getDisplayItem() {
        return this.displayItem;
    }

    public Kit setDisplayItem(ItemStack item) {
        this.displayItem = item;
        return this;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public Kit setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public KitAnimation getKitAnimation() {
        return this.kitAnimation;
    }

    public Kit setKitAnimation(KitAnimation kitAnimation) {
        this.kitAnimation = kitAnimation;
        return this;
    }

    public Kit clone(String key) {
        try {
            Kit newKit = (Kit) super.clone();

            List<KitItem> contents = new ArrayList<>();

            for (KitItem item : newKit.contents) {
                contents.add(item.clone());
            }

            newKit.setContents(contents);

            newKit.key = key;
            newKit.name = TextUtils.formatText(key, true);

            return newKit;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public int hashCode() {
        return 31 * (this.key != null ? this.key.hashCode() : 0);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Kit)) {
            return false;
        }

        Kit kit = (Kit) obj;
        return Objects.equals(this.key, kit.key);
    }
}
