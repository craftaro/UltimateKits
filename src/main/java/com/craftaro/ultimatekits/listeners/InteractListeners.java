package com.craftaro.ultimatekits.listeners;

import com.craftaro.core.compatibility.CompatibleHand;
import com.craftaro.core.compatibility.ServerVersion;
import com.craftaro.core.gui.GuiManager;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.core.utils.TimeUtils;
import com.craftaro.ultimatekits.UltimateKits;
import com.craftaro.ultimatekits.gui.BlockEditorGui;
import com.craftaro.ultimatekits.kit.Kit;
import com.craftaro.ultimatekits.kit.KitBlockData;
import com.craftaro.ultimatekits.kit.KitType;
import com.craftaro.ultimatekits.settings.Settings;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class InteractListeners implements Listener {
    private final UltimateKits plugin;
    private final GuiManager guiManager;

    public InteractListeners(UltimateKits plugin, GuiManager guiManager) {
        this.plugin = plugin;
        this.guiManager = guiManager;
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_9)) {
            if (event.getHand() == EquipmentSlot.OFF_HAND) {
                return;
            }
        }

        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        KitBlockData kitBlockData = this.plugin.getKitManager().getKit(block.getLocation());
        if (kitBlockData == null) {
            return;
        }

        Kit kit = kitBlockData.getKit();

        Player player = event.getPlayer();

        Material itemInHand = player.getItemInHand().getType();

        Material keyMaterial = Settings.KEY_MATERIAL.getMaterial().parseMaterial();
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (player.isSneaking()) {
                return;
            }

            event.setCancelled(true);

            if (kitBlockData.getType() == KitType.PREVIEW) {
                kit.display(player, this.guiManager, null);

            } else if (kitBlockData.getType() == KitType.CRATE) {
                if (itemInHand == keyMaterial) {
                    kit.processKeyUse(player);
                } else {
                    this.plugin.getLocale().getMessage("event.crate.needkey").sendPrefixedMessage(player);
                    return;
                }
            } else if (kitBlockData.getType() == KitType.CLAIM) {
                if (!kit.hasPermissionToClaim(player)) {
                    this.plugin.getLocale().getMessage("command.general.noperms").sendPrefixedMessage(player);
                    return;
                }

                if (kit.getNextUse(player) > 0) {
                    long time = kit.getNextUse(player);
                    this.plugin.getLocale().getMessage("event.crate.notyet").processPlaceholder("time", TimeUtils.makeReadable(time)).sendPrefixedMessage(player);
                    return;
                }

                if (kit.getLink() != null || kit.getPrice() != 0) {
                    kit.buy(player, this.guiManager);
                } else {
                    kit.processGenericUse(player, false);
                }

                kit.updateDelay(player);
            }
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);

            if (player.isSneaking() && player.hasPermission("ultimatekits.admin")) {
                this.guiManager.showGUI(player, new BlockEditorGui(this.plugin, kitBlockData));
                return;
            }

            kit.display(player, this.guiManager, null);
        }
    }

    @EventHandler
    public void onCrateClick(PlayerInteractEvent event) {
        // Would be better to use NBT to make the item persist over aesthetic changes.
        // Yes you really should have used NBT. In fact we have an API for this in SongodaCore...

        // Filter physical actions (pressure plates, buttons)
        if (event.getAction() == Action.PHYSICAL
                || event.getItem() == null
                || XMaterial.AIR.isSimilar(event.getItem())
                || !XMaterial.CHEST.isSimilar(event.getItem())) {
            return;
        }

        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (!item.hasItemMeta() || !item.getItemMeta().hasLore() || item.getItemMeta().getLore().isEmpty()) {
            return;
        }

        Kit kit = this.plugin.getKitManager().getKit(ChatColor.stripColor(item.getItemMeta().getLore().get(0).split(" ")[0]));

        if (kit == null) {
            return;
        }

        event.setCancelled(true);

        // Function
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // Open the crate
            kit.processCrateUse(player, item, CompatibleHand.getHand(event));
        } else // There are only left click actions left
        {
            kit.display(player, this.guiManager, null);
        }
    }
}
