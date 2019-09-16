package com.songoda.ultimatekits.handlers;

import com.songoda.core.utils.TextUtils;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.kit.KitBlockData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Created by songoda on 2/24/2017.
 */
public class DisplayItemHandler {

    private final UltimateKits instance;

    public DisplayItemHandler(UltimateKits instance) {
        this.instance = instance;
    }

    public void start() {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(UltimateKits.getInstance(), this::displayItems, 30L, 30L);
    }

    private void displayItems() {
        for (KitBlockData kitBlockData : instance.getKitManager().getKitLocations().values())
            displayItem(kitBlockData);
    }

    public void displayItem(KitBlockData kitBlockData) {
        Location location = kitBlockData.getLocation();
        location.add(0.5, 0, 0.5);

        Kit kit = kitBlockData.getKit();
        if (kit == null) return;

        List<ItemStack> list = kit.getReadableContents(null, false, false, false);
        if (list == null) return;

        if (list.isEmpty()) return;
        for (Entity e : location.getChunk().getEntities()) {
            if (e.getType() != EntityType.DROPPED_ITEM
                    || e.getLocation().getX() != location.getX()
                    || e.getLocation().getZ() != location.getZ()) {
                continue;
            }
            Item i = (Item) e;

            if (!kitBlockData.isDisplayingItems()) e.remove();

            int inum = Integer.parseInt(i.getItemStack().getItemMeta().getDisplayName().replace(String.valueOf(ChatColor.COLOR_CHAR), "")) + 1;
            if (inum > list.size()) inum = 1;

            ItemStack is = list.get(inum - 1);
            if (kitBlockData.isItemOverride()) {
                if (kit.getDisplayItem() != null)
                    is = kit.getDisplayItem().getItem();
            }
            ItemMeta meta = is.getItemMeta();
            is.setAmount(1);
            meta.setDisplayName(TextUtils.convertToInvisibleString(Integer.toString(inum)));
            is.setItemMeta(meta);
            i.setItemStack(is);
            i.setPickupDelay(9999);
            return;
        }
        if (!kitBlockData.isDisplayingItems()) return;

        ItemStack is = list.get(0);
        is.setAmount(1);
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(TextUtils.convertFromInvisibleString("0"));
        is.setItemMeta(meta);
        Item item = location.getWorld().dropItem(location.add(0, 1, 0), list.get(0));
        Vector vec = new Vector(0, 0, 0);
        item.setVelocity(vec);
        item.setPickupDelay(9999);
        item.setMetadata("displayItem", new FixedMetadataValue(UltimateKits.getInstance(), true));
        item.setMetadata("betterdrops_ignore", new FixedMetadataValue(UltimateKits.getInstance(), true));
    }
}
