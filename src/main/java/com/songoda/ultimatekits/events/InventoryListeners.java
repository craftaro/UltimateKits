package com.songoda.ultimatekits.events;

import com.songoda.ultimatekits.Lang;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.Kit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;

public class InventoryListeners implements Listener {

    private final UltimateKits instance;

    public InventoryListeners(UltimateKits plugin) {
        this.instance = plugin;
    }

    /*
    @EventHandler(ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        try {
            Player player = (Player) event.getWhoClicked();
            PlayerData playerData = instance.getPlayerDataManager().getPlayerAction(player);

            if (playerData.isInCrate()) {
                event.setCancelled(true);
            }


        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        try {
            if (instance.getPlayerDataManager().getPlayerAction((Player) event.getWhoClicked()).getGuiLocation() != PlayerData.GUILocation.DISPLAY)
                return;
            event.setCancelled(true);
            if (instance.getReferences().isPlaySound())
                ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), instance.getReferences().getSound(), 10.0F, 1.0F);

        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    @EventHandler
    public void onInteract(InventoryInteractEvent event) {
        try {
            if (instance.getPlayerDataManager().getPlayerAction((Player) event.getWhoClicked()).getGuiLocation() != PlayerData.GUILocation.DISPLAY)
                return;
            event.setCancelled(true);
            if (instance.getReferences().isPlaySound())
                ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), instance.getReferences().getSound(), 10.0F, 1.0F);

        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        try {
            final Player player = (Player) event.getPlayer();

            PlayerData playerData = instance.getPlayerDataManager().getPlayerAction(player);

            playerData.setGuiLocation(PlayerData.GUILocation.NOT_IN);

        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    } */
}