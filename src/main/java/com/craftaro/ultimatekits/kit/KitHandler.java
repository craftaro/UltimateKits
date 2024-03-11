package com.craftaro.ultimatekits.kit;

import com.craftaro.core.compatibility.CompatibleHand;
import com.craftaro.core.gui.Gui;
import com.craftaro.core.gui.GuiManager;
import com.craftaro.core.hooks.EconomyManager;
import com.craftaro.core.third_party.de.tr7zw.nbtapi.NBTItem;
import com.craftaro.core.utils.ItemUtils;
import com.craftaro.core.utils.TimeUtils;
import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.ultimatekits.UltimateKits;
import com.craftaro.ultimatekits.crate.Crate;
import com.craftaro.ultimatekits.gui.AnimatedKitGui;
import com.craftaro.ultimatekits.gui.ConfirmBuyGui;
import com.craftaro.ultimatekits.gui.PreviewKitGui;
import com.craftaro.ultimatekits.key.Key;
import com.craftaro.ultimatekits.kit.type.KitContentCommand;
import com.craftaro.ultimatekits.kit.type.KitContentEconomy;
import com.craftaro.ultimatekits.settings.Settings;
import com.craftaro.ultimatekits.utils.ArmorType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class KitHandler {

    private final UltimateKits plugin;

    public KitHandler(UltimateKits plugin) {
        this.plugin = plugin;
    }

    /**
     * Process the purchase of a kit.
     *
     * @param kit     The kit to give to the player
     * @param player  The player to give the kit to
     * @param manager The GUI manager
     */
    public void buy(Kit kit, Player player, GuiManager manager) {
        if (kit.hasPermissionToClaim(player)) {
            processGenericUse(kit, player, false);
            return;
        }

        if (!kit.hasPermissionToBuy(player)) {
            UltimateKits.getInstance().getLocale().getMessage("command.general.noperms")
                    .sendPrefixedMessage(player);
            return;
        }

        if (kit.getLink() != null) {
            player.sendMessage("");
            plugin.getLocale().newMessage("&a" + kit.getLink()).sendPrefixedMessage(player);
            player.sendMessage("");
            player.closeInventory();
        } else if (kit.getPrice() != 0) {
            manager.showGUI(player, new ConfirmBuyGui(plugin, player, kit, null));
        } else {
            UltimateKits.getInstance().getLocale().getMessage("command.general.noperms")
                    .sendPrefixedMessage(player);
        }
    }

    /**
     * Process the use of a key inventory item on a kit block.
     *
     * @param kit    The kit to give to the player
     * @param player The player to give the kit to
     */
    public void processKeyUse(Kit kit, Player player) {
        ItemStack item = player.getItemInHand();
        NBTItem nbtItem = new NBTItem(item);

        if (!nbtItem.hasKey("key") || !nbtItem.hasKey("kit")) {
            return;
        }

        String keyName = nbtItem.getString("key");
        String kitName = nbtItem.getString("kit");

        boolean any = kitName.equals("ANY");
        Key key = plugin.getKeyManager().getKey(keyName);

        if (key == null && !any) {
            return;
        }

        String name = kit.getName();

        if (!any && !kitName.equals(name)) {
            plugin.getLocale().getMessage("event.crate.wrongkey").sendPrefixedMessage(player);
            return;
        }

        int amount = key == null ? -1 : key.getAmount();
        if (amount == -1) {
            amount = kit.getContents().size();
        }

        if (key == null ? giveKit(kit, player) : giveKit(kit, player, amount, key.getKitAmount())) {
            plugin.getLocale().getMessage("event.key.success")
                    .processPlaceholder("kit", kit.getName()).sendPrefixedMessage(player);
            if (player.getInventory().getItemInHand().getAmount() != 1) {
                item.setAmount(item.getAmount() - 1);
                player.setItemInHand(item);
            } else {
                player.setItemInHand(null);
            }
        }
    }

    /**
     * Process the use of a crate inventory item.
     *
     * @param kit    The kit to give to the player
     * @param player The player to give the kit to
     * @param item   The item that was used
     * @param hand   The hand that the item was used from
     */
    public void processCrateUse(Kit kit, Player player, ItemStack item, CompatibleHand hand) {
        Crate crate = plugin.getCrateManager().getCrate(item);

        if (crate == null || !giveKit(kit, player, crate.getKitAmount() != 0 ? crate.getKitAmount() : -1, crate.getKitAmount())) {
            return;
        }

        ItemUtils.takeActiveItem(player, hand);

        plugin.getLocale().getMessage("event.crate.success")
                .processPlaceholder("crate", crate.getName()).sendPrefixedMessage(player);
    }

    /**
     * Process the purchase of a kit.
     *
     * @param kit    The kit to give to the player
     * @param player The player to give the kit to
     */
    public void processPurchaseUse(Kit kit, Player player) {
        if (!EconomyManager.isEnabled()) {
            return;
        }

        String name = kit.getName();

        if (!player.hasPermission("ultimatekits.buy." + kit.getKey())) {
            UltimateKits.getInstance().getLocale().getMessage("command.general.noperms")
                    .sendPrefixedMessage(player);
            return;
        }

        if (!EconomyManager.hasBalance(player, kit.getPrice())) {
            plugin.getLocale().getMessage("event.claim.cannotafford")
                    .processPlaceholder("kit", name).sendPrefixedMessage(player);
            return;
        }

        if (kit.getDelay() > 0 && kit.getNextUse(player) != 0) {
            plugin.getLocale().getMessage("event.claim.delay")
                    .processPlaceholder("time", TimeUtils.makeReadable(kit.getNextUse(player)))
                    .sendPrefixedMessage(player);
            return;
        }

        if (kit.getNextUse(player) == -1) {
            plugin.getLocale().getMessage("event.claim.nottwice").sendPrefixedMessage(player);
            return;
        }

        if (giveKit(kit, player)) {
            EconomyManager.withdrawBalance(player, kit.getPrice());
            if (kit.getDelay() != 0) {
                kit.updateDelay(player);
            }

            plugin.getLocale().getMessage("event.claim.purchasesuccess")
                    .processPlaceholder("kit", name).sendPrefixedMessage(player);
        }
    }

    /**
     * Process the use of a kit.
     *
     * @param kit    The kit to give to the player
     * @param player The player to give the kit to
     * @param forced Whether the kit should be given regardless of the delay
     */
    public void processGenericUse(Kit kit, Player player, boolean forced) {
        if (kit.getNextUse(player) == -1 && !forced) {
            plugin.getLocale().getMessage("event.claim.nottwice").sendPrefixedMessage(player);
        } else if (kit.getNextUse(player) <= 0 || forced) {
            if (giveKit(kit, player)) {
                kit.updateDelay(player);
                if (kit.getKitAnimation() == KitAnimation.NONE) {
                    plugin.getLocale().getMessage("event.claim.givesuccess")
                            .processPlaceholder("kit", kit.getName()).sendPrefixedMessage(player);
                }
            }
        } else {
            plugin.getLocale().getMessage("event.claim.delay")
                    .processPlaceholder("time", TimeUtils.makeReadable(kit.getNextUse(player)))
                    .sendPrefixedMessage(player);
        }
    }

    /**
     * Give a kit to a player.
     *
     * @param kit    The kit to give to the player
     * @param player The player to give the kit to
     * @return True if the kit was given successfully, false otherwise
     */
    public boolean giveKit(Kit kit, Player player) {
        return giveKit(kit, player, kit.getContents().size(), -1);
    }

    /**
     * Give a kit to a player with a specified item amount and kit amount.
     *
     * @param kit        The kit to give to the player
     * @param player     The player to give the kit to
     * @param itemAmount The amount of items to give from the kit
     * @param kitAmount  The amount of times to give the kit
     * @return True if the kit was given successfully, false otherwise
     */
    private boolean giveKit(Kit kit, Player player, int itemAmount, int kitAmount) {
        List<KitItem> kitItems = new ArrayList<>(kit.getContents());
        int itemGiveAmount = (kitAmount > 0) ? itemAmount * kitAmount : itemAmount;

        if (kit.getKitAnimation() == KitAnimation.ROULETTE) {
            itemGiveAmount = 1;
        }

        if (Settings.NO_REDEEM_WHEN_FULL.getBoolean() && !kit.hasRoom(player, itemGiveAmount)) {
            plugin.getLocale().getMessage("event.claim.full").sendPrefixedMessage(player);
            return false;
        }

        if (Settings.SOUNDS_ENABLED.getBoolean() && kit.getKitAnimation() == KitAnimation.NONE) {
            XSound.ENTITY_PLAYER_LEVELUP.play(player, 0.6F, 15.0F);
        }

        return giveKitItems(kit, kitItems, itemGiveAmount, player);
    }

    /**
     * Give kit items to a player.
     *
     * @param kit            The kit to give items from
     * @param kitItems       The list of kit items to give
     * @param itemGiveAmount The amount of items to give
     * @param player         The player to give the items to
     * @return True if the items were given successfully, false otherwise
     */
    private boolean giveKitItems(Kit kit, List<KitItem> kitItems, int itemGiveAmount, Player player) {
        if (kitItems.size() != itemGiveAmount || kit.getKitAnimation() != KitAnimation.NONE) {
            Collections.shuffle(kitItems);
        }

        for (KitItem item : kitItems) {
            if (itemGiveAmount <= 0) {
                break;
            }

            double chance = (item.getChance() == 0) ? 100 : item.getChance();
            double random = Math.random() * 100;
            itemGiveAmount--;

            if (random < chance) {
                if (kit.getKitAnimation() != KitAnimation.NONE) {
                    plugin.getGuiManager().showGUI(player, new AnimatedKitGui(plugin, player, kit, item.getItem()));
                    return true;
                } else {
                    ItemStack parseStack = item.getContent().process(player).clone();
                    if (!(item.getContent() instanceof KitContentEconomy || item.getContent() instanceof KitContentCommand)) {
                        if (Settings.AUTO_EQUIP_ARMOR.getBoolean() && ArmorType.equip(player, parseStack)) {
                            continue;
                        }

                        Map<Integer, ItemStack> overfilled = player.getInventory().addItem(parseStack);
                        for (ItemStack item2 : overfilled.values()) {
                            player.getWorld().dropItemNaturally(player.getLocation(), item2);
                        }
                    }
                }
            }
        }

        player.updateInventory();
        return true;
    }

    /**
     * Display a kit preview to a player.
     *
     * @param kit     The kit to display
     * @param player  The player to display the preview to
     * @param manager The GUI manager
     * @param back    The GUI to go back to
     */
    public void display(Kit kit, Player player, GuiManager manager, Gui back) {
        if (!kit.hasPermissionToPreview(player)) {
            UltimateKits.getInstance().getLocale().getMessage("command.general.noperms")
                    .sendPrefixedMessage(player);
            return;
        }

        if (kit.getKey() == null) {
            plugin.getLocale().getMessage("command.kit.kitdoesntexist").sendPrefixedMessage(player);
            return;
        }

        plugin.getLocale().getMessage("event.preview.kit")
                .processPlaceholder("kit", kit.getName()).sendPrefixedMessage(player);
        manager.showGUI(player, new PreviewKitGui(plugin, player, kit, back));
    }
}
