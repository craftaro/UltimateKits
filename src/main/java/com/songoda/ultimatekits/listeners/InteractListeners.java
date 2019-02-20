package com.songoda.ultimatekits.listeners;

import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.gui.GUIBlockEditor;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.kit.KitBlockData;
import com.songoda.ultimatekits.kit.KitType;
import com.songoda.ultimatekits.utils.Debugger;
import com.songoda.ultimatekits.utils.Methods;
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

    private final UltimateKits instance;

    public InteractListeners(UltimateKits instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        try {
            boolean chand = true; // This needs to be out of my code.
            if (event.getHand() != EquipmentSlot.HAND) {
                chand = false;
            }

            Block block = event.getClickedBlock();

            if (!chand) return;

            if (event.getClickedBlock() == null) return;

            KitBlockData kitBlockData = instance.getKitManager().getKit(block.getLocation());
            if (kitBlockData == null) return;
            Kit kit = kitBlockData.getKit();

            Player player = event.getPlayer();
            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {

                if (player.isSneaking()) return;
                event.setCancelled(true);

                if (player.getItemInHand() != null && player.getItemInHand().getType() != null && player.getItemInHand().getType() == Material.TRIPWIRE_HOOK) {
                    event.setCancelled(true);
                    kit.give(player, true, false, false);
                    return;
                }

                if (kitBlockData.getType() != KitType.PREVIEW) {
                    if (kitBlockData.getType() == KitType.CRATE) {
                        long time = kit.getNextUse(player);
                        player.sendMessage(Methods.formatText(instance.getReferences().getPrefix() + instance.getLocale().getMessage("event.crate.notyet", Methods.makeReadable(time))));
                    } else if (kitBlockData.getType() == KitType.CLAIM) {
                        if (!player.hasPermission("essentials.kit." + kit.getName().toLowerCase()) || !player.hasPermission("ultimatekits.kit." + kit.getName().toLowerCase())) {
                            player.sendMessage(instance.getReferences().getPrefix() + instance.getLocale().getMessage("command.general.noperms"));
                            return;
                        }
                        if (kit.getNextUse(player) <= 0) {
                            kit.give(player, false, false, false);
                            kit.updateDelay(player);
                        } else {
                            long time = kit.getNextUse(player);
                            player.sendMessage(Methods.formatText(instance.getReferences().getPrefix() + instance.getLocale().getMessage("event.crate.notyet", Methods.makeReadable(time))));
                        }
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
                    new GUIBlockEditor(instance, player, block.getLocation());
                    return;
                }
                if (player.getItemInHand() != null && player.getItemInHand().getType() != null && player.getItemInHand().getType() == Material.TRIPWIRE_HOOK) {
                    event.setCancelled(true);
                    kit.give(player, true, false, false);
                    return;
                }
                kit.display(player, null);

            }
        } catch (Exception x) {
            Debugger.runReport(x);
        }
    }
}

