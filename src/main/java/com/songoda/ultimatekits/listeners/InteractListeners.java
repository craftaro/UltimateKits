package com.songoda.ultimatekits.listeners;

import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.gui.GUIBlockEditor;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.kit.KitBlockData;
import com.songoda.ultimatekits.kit.KitType;
import com.songoda.ultimatekits.utils.Debugger;
import com.songoda.ultimatekits.utils.Methods;
import com.songoda.ultimatekits.utils.ServerVersion;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;

public class InteractListeners implements Listener {

    private final UltimateKits plugin;

    public InteractListeners(UltimateKits plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        try {
            if (plugin.isServerVersionAtLeast(ServerVersion.V1_9)) {
                if (event.getHand() == EquipmentSlot.OFF_HAND) return;
            }

            Block block = event.getClickedBlock();

            if (event.getClickedBlock() == null) return;

            KitBlockData kitBlockData = plugin.getKitManager().getKit(block.getLocation());

            if (kitBlockData == null) return;

            Kit kit = kitBlockData.getKit();

            Player player = event.getPlayer();
            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {

                if (player.isSneaking()) return;
                event.setCancelled(true);

                if (player.getItemInHand().getType() == Material.TRIPWIRE_HOOK) {
                    event.setCancelled(true);
                    kit.processKeyUse(player);
                    return;
                }

                if (kitBlockData.getType() != KitType.PREVIEW) {
                    if (!kit.hasPermission(player)) {
                        plugin.getLocale().getMessage("command.general.noperms").sendPrefixedMessage(player);
                        return;
                    }
                    if (kit.getNextUse(player) <= 0) {
                        kit.processGenericUse(player, false);
                        kit.updateDelay(player);
                    } else {
                        long time = kit.getNextUse(player);
                        plugin.getLocale().getMessage("event.crate.notyet").processPlaceholder("time",
                                Methods.makeReadable(time)).sendPrefixedMessage(player);
                    }
                } else if (kit.getLink() != null || kit.getPrice() != 0) {
                    kit.buy(player);
                } else {
                    kit.display(player, null);
                }
            } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (block.getState() instanceof InventoryHolder || block.getType() == Material.ENDER_CHEST) {
                    event.setCancelled(true);
                }
                if (player.isSneaking() && player.hasPermission("ultimatekits.admin")) {
                    new GUIBlockEditor(plugin, player, block.getLocation());
                    return;
                }
                if (player.getItemInHand().getType() == Material.TRIPWIRE_HOOK) {
                    event.setCancelled(true);
                    kit.processKeyUse(player);
                    return;
                }
                kit.display(player, null);

            }
        } catch (Exception x) {
            Debugger.runReport(x);
        }
    }
}

