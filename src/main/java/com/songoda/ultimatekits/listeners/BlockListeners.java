package com.songoda.ultimatekits.listeners;

import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.key.Key;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.kit.KitBlockData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by songoda on 2/24/2017.
 */
public class BlockListeners implements Listener {

    private final UltimateKits plugin;

    public BlockListeners(UltimateKits plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        KitBlockData kitBlockData = plugin.getKitManager().getKit(block.getLocation());
        if (kitBlockData == null) return;
        Kit kit = kitBlockData.getKit();

        plugin.removeHologram(kitBlockData);

        plugin.getKitManager().removeKitFromLocation(block.getLocation());

        plugin.getLocale().newMessage("&8Kit &9" + kit.getKey() + " &8unassigned from: &a" + block.getType() + "&8.")
                .sendPrefixedMessage(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        Block block = e.getBlockAgainst();
        KitBlockData kitBlockData = plugin.getKitManager().getKit(block.getLocation());
        if (kitBlockData != null) {
            e.setCancelled(true);
        }
        ItemStack item = e.getItemInHand();
        if (item.getType() == Material.TRIPWIRE_HOOK && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            Key key = plugin.getKeyManager().getKey(ChatColor.stripColor(item.getItemMeta().getLore().get(0)).replace(" Key", ""));
            if (key != null)
                e.setCancelled(true);
        }
    }
}
