package com.songoda.ultimatekits.kit;

import com.songoda.core.compatibility.CompatibleHand;
import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.configuration.Config;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiManager;
import com.songoda.core.hooks.EconomyManager;
import com.songoda.core.nms.NmsManager;
import com.songoda.core.nms.nbt.NBTItem;
import com.songoda.core.utils.ItemUtils;
import com.songoda.core.utils.TextUtils;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.category.Category;
import com.songoda.ultimatekits.crate.Crate;
import com.songoda.ultimatekits.gui.AnimatedKitGui;
import com.songoda.ultimatekits.gui.ConfirmBuyGui;
import com.songoda.ultimatekits.gui.PreviewKitGui;
import com.songoda.ultimatekits.key.Key;
import com.songoda.ultimatekits.kit.type.KitContentCommand;
import com.songoda.ultimatekits.kit.type.KitContentEconomy;
import com.songoda.ultimatekits.kit.type.KitContentItem;
import com.songoda.ultimatekits.settings.Settings;
import com.songoda.ultimatekits.utils.ArmorType;
import com.songoda.ultimatekits.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * Created by songoda on 2/24/2017.
 */
public class Kit {

    private final String key, name;
    private Category category = null;

    private static UltimateKits plugin;
    private double price = 0;
    private String link, title = null;
    private long delay = 0L;
    private boolean hidden = false;
    private CompatibleMaterial displayItem = null;
    private List<KitItem> contents = new ArrayList<>();
    private KitAnimation kitAnimation = KitAnimation.NONE;

    public Kit(String key) {
        if (plugin == null)
            plugin = UltimateKits.getInstance();
        this.key = key;
        this.name = TextUtils.formatText(key, true);
    }

    public void buy(Player player, GuiManager manager) {
        if (hasPermissionToClaim(player)) {
            processGenericUse(player, false);
            return;
        }

        if (!hasPermissionToBuy(player)) {
            UltimateKits.getInstance().getLocale().getMessage("command.general.noperms")
                    .sendPrefixedMessage(player);
            return;
        }

        if (link != null) {
            player.sendMessage("");
            plugin.getLocale().newMessage("&a" + link).sendPrefixedMessage(player);
            player.sendMessage("");
            player.closeInventory();
        } else if (price != 0) {
            manager.showGUI(player, new ConfirmBuyGui(plugin, player, this, null));
        } else {
            UltimateKits.getInstance().getLocale().getMessage("command.general.noperms")
                    .sendPrefixedMessage(player);
        }
    }


    private boolean hasRoom(Player player, int itemAmount) {
        int space = 0;

        for (ItemStack content : player.getInventory().getStorageContents()) {
            if (content == null) {
                space++;
            }
        }

        // Since roulette only gives one item, we don't need to check if the user has room for the whole kit.
        if (kitAnimation == KitAnimation.ROULETTE && space >= 1)
            return true;

        return space >= itemAmount;
    }

    public void processKeyUse(Player player) {
        ItemStack item = player.getItemInHand();
        NBTItem nbtItem = NmsManager.getNbt().of(item);

        if (!nbtItem.has("key") || !nbtItem.has("kit"))
            return;

        String keyName = nbtItem.getNBTObject("key").asString();
        String kitName = nbtItem.getNBTObject("kit").asString();

        boolean any = kitName.equals("ANY");
        Key key = plugin.getKeyManager().getKey(keyName);

        if (key == null && !any)
            return;

        if (!any && !kitName.equals(name)) {
            plugin.getLocale().getMessage("event.crate.wrongkey").sendPrefixedMessage(player);
            return;
        }

        if (giveKit(player, key)) {
            plugin.getLocale().getMessage("event.key.success")
                    .processPlaceholder("kit", name).sendPrefixedMessage(player);
            if (player.getInventory().getItemInHand().getAmount() != 1) {
                item.setAmount(item.getAmount() - 1);
                player.setItemInHand(item);
            } else {
                player.setItemInHand(null);
            }
        }
    }

    public void processCrateUse(Player player, ItemStack item, CompatibleHand hand) {
        Crate crate = plugin.getCrateManager().getCrate(item);

        if (crate == null || !giveKit(player, crate))
            return;

        ItemUtils.takeActiveItem(player, hand);

        plugin.getLocale().getMessage("event.crate.success")
                .processPlaceholder("crate", name).sendPrefixedMessage(player);
    }

    public void processPurchaseUse(Player player) {
        if (!EconomyManager.isEnabled()) return;

        if (!player.hasPermission("ultimatekits.buy." + key)) {
            UltimateKits.getInstance().getLocale().getMessage("command.general.noperms")
                    .sendPrefixedMessage(player);
            return;
        } else if (!EconomyManager.hasBalance(player, price)) {
            plugin.getLocale().getMessage("event.claim.cannotafford")
                    .processPlaceholder("kit", name).sendPrefixedMessage(player);
            return;
        }
        if (this.delay > 0) {
            if (getNextUse(player) != 0) {
                plugin.getLocale().getMessage("event.claim.delay")
                        .processPlaceholder("time", Methods.makeReadable(this.getNextUse(player)))
                        .sendPrefixedMessage(player);
                return;
            }
        } else if (getNextUse(player) == -1) {
            plugin.getLocale().getMessage("event.claim.nottwice").sendPrefixedMessage(player);
            return;
        }
        if (giveKit(player)) {
            EconomyManager.withdrawBalance(player, price);
            if (delay != 0)
                updateDelay(player); //updates delay on buy

            plugin.getLocale().getMessage("event.claim.purchasesuccess")
                    .processPlaceholder("kit", name).sendPrefixedMessage(player);
        }
    }

    public void processGenericUse(Player player, boolean forced) {
        if (getNextUse(player) == -1 && !forced) {
            plugin.getLocale().getMessage("event.claim.nottwice").sendPrefixedMessage(player);
        } else if (getNextUse(player) <= 0 || forced) {
            if (giveKit(player)) {
                updateDelay(player);
                if (kitAnimation == KitAnimation.NONE)
                    plugin.getLocale().getMessage("event.claim.givesuccess")
                            .processPlaceholder("kit", name).sendPrefixedMessage(player);
            }
        } else {
            plugin.getLocale().getMessage("event.claim.delay")
                    .processPlaceholder("time", Methods.makeReadable(getNextUse(player)))
                    .sendPrefixedMessage(player);
        }
    }

    @SuppressWarnings("Duplicates")
    public void display(Player player, GuiManager manager, Gui back) {
        if (!hasPermissionToPreview(player)) {
            UltimateKits.getInstance().getLocale().getMessage("command.general.noperms")
                    .sendPrefixedMessage(player);
            return;
        }
        if (key == null) {
            plugin.getLocale().getMessage("command.kit.kitdoesntexist").sendPrefixedMessage(player);
            return;
        }

        plugin.getLocale().getMessage("event.preview.kit")
                .processPlaceholder("kit", name).sendPrefixedMessage(player);
        manager.showGUI(player, new PreviewKitGui(plugin, player, this, back));
    }

    public void saveKit(List<ItemStack> items) {
        List<KitItem> list = new ArrayList<>();
        for (ItemStack is : items) {
            if (is != null && is.getType() != Material.AIR) {

                if (is.getItemMeta().hasLore()) {
                    ItemMeta meta = is.getItemMeta();
                    List<String> newLore = new ArrayList<>();
                    for (String line : meta.getLore()) {
                        if (line.contains("Moveable")) continue;
                        if (line.equals(TextUtils.formatText("&8----"))) break;
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
        contents = list;
        plugin.saveKits(false);
    }


    public List<ItemStack> getReadableContents(Player player, boolean preview, boolean commands, boolean moveable) {
        List<ItemStack> stacks = new ArrayList<>();
        for (KitItem item : getContents()) {
            if ((!item.getSerialized().startsWith("/") && !item.getSerialized().startsWith(Settings.CURRENCY_SYMBOL.getString())) || commands) { //ToDO: I doubt this is correct.
                ItemStack stack = moveable ? item.getMoveableItem() : item.getItem();
                if (preview) stack = item.getItemForDisplay();
                if (stack == null) continue;

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

    public boolean giveKit(Player player) {
        return giveKit(player, getContents().size(), -1);
    }

    private boolean giveKit(Player player, Key key) {
        return key == null ? giveKit(player) : giveKit(player, key.getAmount(), key.getKitAmount());
    }

    private boolean giveKit(Player player, Crate crate) {
        return giveKit(player, crate.getAmount(), crate.getKitAmount());
    }

    private boolean giveKit(Player player, int itemAmount, int kitAmount) {
        List<KitItem> innerContents = new ArrayList<>(getContents());
        int kitSize = innerContents.size();

        // Amount of items from the kit to give to the player.
        if (kitAnimation == KitAnimation.ROULETTE) itemAmount = 1; //TODO how about kitAmount > 1? generateRandomItem() will only give 1 random item instead of kitAmount
        int itemGiveAmount = itemAmount > 0 ? itemAmount : kitSize;
        if (kitAmount > 0) itemGiveAmount = itemGiveAmount * kitAmount;
        
        if (Settings.NO_REDEEM_WHEN_FULL.getBoolean() && !hasRoom(player, itemGiveAmount)) {
            plugin.getLocale().getMessage("event.claim.full").sendPrefixedMessage(player);
            return false;
        }

        if (Settings.SOUNDS_ENABLED.getBoolean() && kitAnimation == KitAnimation.NONE)
            CompatibleSound.ENTITY_PLAYER_LEVELUP.play(player, 0.6F, 15.0F);

        return generateRandomItem(innerContents, itemGiveAmount, player);
    }

    private boolean generateRandomItem(List<KitItem> innerContents, int itemGiveAmount, Player player) {
        if (innerContents.size() != itemGiveAmount || kitAnimation != KitAnimation.NONE)
            Collections.shuffle(innerContents);

        for (KitItem item : new ArrayList<>(innerContents)) {
            if (itemGiveAmount == 0) break;
            double ch = item.getChance() == 0 ? 100 : item.getChance();
            double rand = Math.random() * 100;
            itemGiveAmount--;
            if (rand < ch || ch == 100) {

                ItemStack parseStack = item.getContent().process(player);
                if (kitAnimation != KitAnimation.NONE) {
                    // TODO: this is a very bad way to solve this problem.
                    // Giving the player kit rewards really should be done outside of the Kit class.
                    plugin.getGuiManager().showGUI(player, new AnimatedKitGui(plugin, player, this, parseStack));
                    return true;
                } else {
                    if (item.getContent() instanceof KitContentEconomy
                            || item.getContent() instanceof KitContentCommand)
                        continue;

                    innerContents.remove(item);

                    if (Settings.AUTO_EQUIP_ARMOR.getBoolean() && ArmorType.equip(player, parseStack)) continue;

                    Map<Integer, ItemStack> overfilled = player.getInventory().addItem(parseStack);
                    for (ItemStack item2 : overfilled.values()) {
                        player.getWorld().dropItemNaturally(player.getLocation(), item2);
                    }
                }
            }
        }

        if (itemGiveAmount != 0) {
            return generateRandomItem(innerContents, itemGiveAmount, player);
        }

        player.updateInventory();
        return true;
    }

    public void updateDelay(Player player) {
        plugin.getDataFile().set("Kits." + key + ".delays." + player.getUniqueId().toString(), System.currentTimeMillis());
    }

    public Long getNextUse(Player player) {
        String configSectionPlayer = "Kits." + key + ".delays." + player.getUniqueId().toString();
        Config config = plugin.getDataFile();

        if (!config.contains(configSectionPlayer)) {
            return 0L;
        } else if (this.delay == -1) return -1L;

        long last = config.getLong(configSectionPlayer);
        long delay = this.delay * 1000;

        return (last + delay) >= System.currentTimeMillis() ? (last + delay) - System.currentTimeMillis() : 0L;
    }

    public boolean hasPermissionToClaim(Player player) {
        return player.hasPermission("ultimatekits.claim." + key.toLowerCase());
    }

    public boolean hasPermissionToPreview(Player player) {
        return player.hasPermission("ultimatekits.preview." + key.toLowerCase());
    }

    public boolean hasPermissionToBuy(Player player) {
        return player.hasPermission("ultimatekits.buy." + key.toLowerCase());
    }

    public double getPrice() {
        return price;
    }

    public Kit setPrice(double price) {
        this.price = price;
        return this;
    }

    public String getLink() {
        return link;
    }

    public Kit setLink(String link) {
        this.link = link;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Kit setTitle(String title) {
        this.title = title;
        return this;
    }

    public long getDelay() {
        return delay;
    }

    public Kit setDelay(long delay) {
        this.delay = delay;
        return this;
    }

    public Category getCategory() {
        return category;
    }

    public Kit setCategory(Category category) {
        this.category = category;
        return this;
    }

    public List<KitItem> getContents() {
        return this.contents;
    }

    public Kit setContents(List<KitItem> contents) {
        this.contents = contents;
        return this;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public CompatibleMaterial getDisplayItem() {
        return displayItem;
    }

    public void setDisplayItem(ItemStack item) {
        this.displayItem = item != null ? CompatibleMaterial.getMaterial(item) : null;
    }

    public Kit setDisplayItem(CompatibleMaterial material) {
        this.displayItem = material;
        return this;
    }

    public boolean isHidden() {
        return hidden;
    }

    public Kit setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public KitAnimation getKitAnimation() {
        return kitAnimation;
    }

    public Kit setKitAnimation(KitAnimation kitAnimation) {
        this.kitAnimation = kitAnimation;
        return this;
    }

    @Override
    public int hashCode() {
        return 31 * (key != null ? key.hashCode() : 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Kit)) return false;

        Kit kit = (Kit) o;
        return Objects.equals(key, kit.key);
    }

}
