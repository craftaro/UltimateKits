package com.songoda.ultimatekits.listeners;

import com.songoda.core.compatibility.ServerVersion;
import com.songoda.core.gui.GuiManager;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.gui.BlockEditorGui;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.kit.KitBlockData;
import com.songoda.ultimatekits.kit.KitType;
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

    private final UltimateKits plugin;
    private final GuiManager guiManager;

    public InteractListeners(UltimateKits plugin, GuiManager guiManager) {
        this.plugin = plugin;
        this.guiManager = guiManager;
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_9))
            if (event.getHand() == EquipmentSlot.OFF_HAND) return;

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
                kit.buy(player, guiManager);
            } else {
                kit.display(player, guiManager, null);
            }
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (block.getState() instanceof InventoryHolder || block.getType() == Material.ENDER_CHEST) {
                event.setCancelled(true);
            }
            if (player.isSneaking() && player.hasPermission("ultimatekits.admin")) {
                guiManager.showGUI(player, new BlockEditorGui(plugin, kitBlockData));
                return;
            }
            if (player.getItemInHand().getType() == Material.TRIPWIRE_HOOK) {
                event.setCancelled(true);
                kit.processKeyUse(player);
                return;
            }
            kit.display(player, guiManager, null);

        }
    }
}

