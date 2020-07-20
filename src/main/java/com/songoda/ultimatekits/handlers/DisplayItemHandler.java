package com.songoda.ultimatekits.handlers;

import com.songoda.core.nms.NmsManager;
import com.songoda.core.nms.nbt.NBTItem;
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

import java.util.Collections;
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
        Bukkit.getServer().getScheduler().runTaskTimer(instance, this::displayItems, 30L, 30L);
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

        if (!location.getWorld().isChunkLoaded((int) location.getX() >> 4, (int) location.getZ() >> 4))
            return;

        for (Entity e : location.getChunk().getEntities()) {
            if (e.getType() != EntityType.DROPPED_ITEM
                    || e.getLocation().getX() != location.getX()
                    || e.getLocation().getZ() != location.getZ()) {
                continue;
            }
            Item i = (Item) e;

            if (!kitBlockData.isDisplayingItems()) e.remove();


            NBTItem nbtItem = NmsManager.getNbt().of(i.getItemStack());
            int inum = nbtItem.has("num") ? nbtItem.getNBTObject("num").asInt() + 1 : 0;

            if (inum > list.size()) inum = 1;

            ItemStack is = list.get(inum);
            if (kitBlockData.isItemOverride()) {
                if (kit.getDisplayItem() != null)
                    is = kit.getDisplayItem().getItem();
            }
            is.setAmount(1);
            ItemMeta meta = is.getItemMeta();
            meta.setDisplayName(null);
            is.setItemMeta(meta);
            nbtItem = NmsManager.getNbt().of(is);
            nbtItem.set("num", inum);
            i.setItemStack(nbtItem.finish());
            i.setPickupDelay(9999);
            return;
        }
        if (!kitBlockData.isDisplayingItems()) return;

        ItemStack is = list.get(0);
        is.setAmount(1);
        ItemMeta meta = is.getItemMeta();
        meta.setLore(Collections.singletonList("Display Item"));
        is.setItemMeta(meta);

        NBTItem nbtItem = NmsManager.getNbt().of(is);
        nbtItem.set("num", 0);

        Bukkit.getScheduler().runTask(instance, () -> {
            Item item = location.getWorld().dropItem(location.add(0, 1, 0), nbtItem.finish());
            Vector vec = new Vector(0, 0, 0);
            item.setVelocity(vec);
            item.setPickupDelay(9999);
            item.setCustomName(null);
            item.setMetadata("US_EXEMPT", new FixedMetadataValue(UltimateKits.getInstance(), true));
            item.setMetadata("displayItem", new FixedMetadataValue(UltimateKits.getInstance(), true));
            item.setMetadata("betterdrops_ignore", new FixedMetadataValue(UltimateKits.getInstance(), true));
        });
    }
}
