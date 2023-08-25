package com.craftaro.ultimatekits.listeners;

import com.craftaro.ultimatekits.UltimateKits;
import com.craftaro.ultimatekits.key.Key;
import com.craftaro.ultimatekits.kit.Kit;
import com.craftaro.ultimatekits.kit.KitBlockData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BlockListeners implements Listener {
    private final UltimateKits plugin;

    public BlockListeners(UltimateKits plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        KitBlockData kitBlockData = this.plugin.getKitManager().getKit(block.getLocation());
        if (kitBlockData == null) {
            return;
        }
        Kit kit = kitBlockData.getKit();

        this.plugin.removeHologram(kitBlockData);

        this.plugin.getKitManager().removeKitFromLocation(block.getLocation());

        this.plugin.getLocale().newMessage("&8Kit &9" + kit.getKey() + " &8unassigned from: &a" + block.getType() + "&8.")
                .sendPrefixedMessage(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        Block block = e.getBlockAgainst();
        KitBlockData kitBlockData = this.plugin.getKitManager().getKit(block.getLocation());
        if (kitBlockData != null) {
            e.setCancelled(true);
        }
        ItemStack item = e.getItemInHand();
        if (item.getType() == Material.TRIPWIRE_HOOK && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            Key key = this.plugin.getKeyManager().getKey(ChatColor.stripColor(item.getItemMeta().getLore().get(0)).replace(" Key", ""));
            if (key != null) {
                e.setCancelled(true);
            }
        }
    }
}
