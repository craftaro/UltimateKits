package com.songoda.ultimatekits.events;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.ultimatekits.Lang;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.object.Kit;
import com.songoda.ultimatekits.kit.object.KitBlockData;
import com.songoda.ultimatekits.kit.object.KitType;
import com.songoda.ultimatekits.utils.Debugger;
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
    public void onBlockInteract(PlayerInteractEvent e) {
        try {
            boolean chand = true; // This needs to be out of my code.
            if (!UltimateKits.getInstance().v1_7 && !UltimateKits.getInstance().v1_8) {
                if (e.getHand() != EquipmentSlot.HAND) {
                    chand = false;
                }
            }

            Block b = e.getClickedBlock();

            if (!chand) return;

            if (e.getClickedBlock() == null) return;

            KitBlockData kitBlockData = instance.getKitManager().getKit(b.getLocation());
            if (kitBlockData == null) return;
            Kit kit = kitBlockData.getKit();

            Player p = e.getPlayer();
            if (e.getAction() == Action.LEFT_CLICK_BLOCK) {

                if (p.isSneaking()) return;
                e.setCancelled(true);

                if (p.getItemInHand() != null && p.getItemInHand().getType() != null && p.getItemInHand().getType() == Material.TRIPWIRE_HOOK) {
                    e.setCancelled(true);
                    kit.give(p, true, false, false);
                    return;
                }

                if (kitBlockData.getType() != KitType.PREVIEW) {
                    if (kitBlockData.getType() == KitType.CRATE) {
                        p.sendMessage(Arconix.pl().getApi().format().formatText(instance.references.getPrefix() + Lang.NOT_KEY.getConfigValue()));
                    } else if (kitBlockData.getType() == KitType.CLAIM) {
                        if (!p.hasPermission("essentials.kit." + kit.getName().toLowerCase()) || !p.hasPermission("ultimatekits.kit." + kit.getName().toLowerCase())) {
                            p.sendMessage(instance.references.getPrefix() + Lang.NO_PERM.getConfigValue());
                            return;
                        }
                        if (kit.getNextUse(p) <= 0) {
                            kit.give(p, false, false, false);
                            kit.updateDelay(p);
                        } else {
                            long time = kit.getNextUse(p);
                            p.sendMessage(instance.references.getPrefix() + Lang.NOT_YET.getConfigValue(Arconix.pl().getApi().format().readableTime(time)));
                        }
                    }
                } else if (kit.getLink() != null || kit.getPrice() != 0) {
                    kit.buy(p);
                } else {
                    kit.display(p, false);
                }
            } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (b.getState() instanceof InventoryHolder || b.getType() == Material.ENDER_CHEST) {
                    e.setCancelled(true);
                }
                if (p.isSneaking() && p.hasPermission("ultimatekits.admin")) {
                    instance.getBlockEditor().openOverview(p, b.getLocation());
                    return;
                }
                if (p.getItemInHand() != null && p.getItemInHand().getType() != null && p.getItemInHand().getType() == Material.TRIPWIRE_HOOK) {
                    e.setCancelled(true);
                    kit.give(p, true, false, false);
                    return;
                }
                kit.display(p, false);

            }
        } catch (Exception x) {
            Debugger.runReport(x);
        }
    }
}

