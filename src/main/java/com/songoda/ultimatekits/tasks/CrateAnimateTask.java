package com.songoda.ultimatekits.tasks;

import com.songoda.arconix.api.methods.formatting.TextComponent;
import com.songoda.arconix.api.methods.inventory.AInventory;
import com.songoda.ultimatekits.Lang;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.kit.KitItem;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class CrateAnimateTask extends BukkitRunnable {

    private final UltimateKits plugin;
    private final Player player;
    private final Inventory inventory;
    private final ArrayDeque<KitItem> items;
    private final ItemStack give;
    private CrateAnimateTask instance;
    private boolean slow = false;
    private boolean finish = false;
    private boolean done = false;
    private boolean last;
    private int num = 0;

    private Listener listener;

    public CrateAnimateTask(UltimateKits plugin, Player player, Kit kit, ItemStack give, boolean last) {
        this.plugin = plugin;
        this.player = player;
        this.give = give;
        this.last = last;
        this.inventory = Bukkit.createInventory(null, 27, TextComponent.formatText(kit.getShowableName()));

        List<KitItem> items = kit.getContents();
        Collections.shuffle(items);
        this.items = new ArrayDeque<>(items);
        while (this.items.size() < 10) {
            for (KitItem item : items) {
                if (this.items.size() < 10)
                    this.items.addLast(item);
            }
        }

        this.listener = new Listener() {
            @EventHandler
            public void onInventoryClick(InventoryClickEvent event) {
                if (!(event.getWhoClicked() instanceof Player) && event.getWhoClicked() == player) return;

                event.setCancelled(true);
            }
        };

        Bukkit.getPluginManager().registerEvents(listener, UltimateKits.getInstance());
    }

    public void start() {
        if (instance == null) instance = this;
        instance.runTaskTimer(plugin, 0, 3);

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            slow = true;
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> finish = true, 20);
        }, 60);
    }

    @Override
    public void run() {
        if (slow && num == 1) {
            num = 0;
            return;
        }
        num = slow ? 1 : 0;

        for (int i = 0; i < 27; i++) {
            inventory.setItem(i, AInventory.toGlass(true, 0));
        }

        for (int i = 9; i < 18; i++) {
            inventory.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }


        inventory.setItem(4, new ItemStack(Material.TRIPWIRE_HOOK));
        inventory.setItem(22, new ItemStack(Material.TRIPWIRE_HOOK));

        if (!done) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 5f, 5f);
            this.items.addFirst(this.items.getLast());
            this.items.removeLast();
        }

        List<KitItem> items = new ArrayList<>(this.items);
        for (int i = 0; i < 9; i++) {
            inventory.setItem(9 + i, items.get(i).getItem());
        }

        if (finish) {
            if (inventory.getItem(13).isSimilar(give)) {
                if (!done) {
                    Map<Integer, ItemStack> overfilled = player.getInventory().addItem(give);
                    for (ItemStack item2 : overfilled.values()) {
                        player.getWorld().dropItemNaturally(player.getLocation(), item2);
                    }
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10f, 10f);
                    player.sendMessage(plugin.getReferences().getPrefix() + TextComponent.formatText(Lang.CRATE_WON.getConfigValue(WordUtils.capitalize(give.getType().name().toLowerCase().replace("_", " ")))));
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::finish, 50);
                }
                done = true;

            }
        }
        player.openInventory(inventory);

    }

    private void finish() {
        instance.cancel();
        HandlerList.unregisterAll(listener);
        if (last) {
            Bukkit.broadcastMessage("last");
            listener = null;
            player.closeInventory();
        }
    }
}