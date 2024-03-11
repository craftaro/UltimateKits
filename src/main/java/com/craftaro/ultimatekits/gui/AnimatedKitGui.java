package com.craftaro.ultimatekits.gui;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.gui.Gui;
import com.craftaro.core.gui.GuiUtils;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.third_party.org.apache.commons.text.WordUtils;
import com.craftaro.ultimatekits.UltimateKits;
import com.craftaro.ultimatekits.kit.Kit;
import com.craftaro.ultimatekits.kit.KitItem;
import com.craftaro.ultimatekits.settings.Settings;
import com.craftaro.ultimatekits.utils.ArmorType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class AnimatedKitGui extends Gui {
    static final Random rand = new Random();

    private final UltimateKits plugin;
    private final Player player;
    private final ItemStack give;
    private final ArrayDeque<KitItem> items = new ArrayDeque<>();
    private boolean finish = false;
    private boolean done = false;
    private int tick = 0, updateTick = 0;
    private int ticksPerUpdate = 3;
    private int task;

    public AnimatedKitGui(UltimateKits plugin, Player player, Kit kit, ItemStack give) {
        this.plugin = plugin;
        this.player = player;
        this.give = give;
        setRows(3);
        setAllowClose(false);
        setTitle(kit.getName());
        setDefaultItem(GuiUtils.getBorderItem(XMaterial.GRAY_STAINED_GLASS_PANE));

        // ideally, we'd populate the items in such a way that the end item isn't far from the center when the animation is complete
        // would be something to do if people have large kit loot tables.
        List<KitItem> kitItems = new ArrayList<>(kit.getContents());
        if (kitItems.isEmpty()) {
            throw new RuntimeException("Cannot give an empty kit!");
        }
        Collections.shuffle(kitItems);
        this.items.addAll(kitItems);
        while (this.items.size() < 10) {
            this.items.addAll(kitItems);
        }

        setItem(4, GuiUtils.getBorderItem(XMaterial.TRIPWIRE_HOOK));
        setItem(22, GuiUtils.getBorderItem(XMaterial.TRIPWIRE_HOOK));
        tick();
        setOnOpen(event -> this.task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::tick, 1L, 1L));
    }

    private void tick() {
        if (++this.tick < this.ticksPerUpdate) {
            return;
        }
        this.tick = 0;
        int updatesPerSlow = 6;
        if (++this.updateTick >= updatesPerSlow) {
            this.updateTick = 0;
            int ticksPerUpdateSlow = Settings.ROULETTE_LENGTH_MULTIPLIER.getInt();
            if (++this.ticksPerUpdate >= ticksPerUpdateSlow) {
                this.finish = true;
            }
        }
        // now update the display
        // rainbow disco!
        for (int col = 0; col < 9; ++col) {
            if (col == 4) {
                continue;
            }

            setItem(0, col, GuiUtils.getBorderItem(CompatibleMaterial.getGlassPaneForColor(rand.nextInt(16))));
            setItem(2, col, GuiUtils.getBorderItem(CompatibleMaterial.getGlassPaneForColor(rand.nextInt(16))));
        }

        // item slider
        if (!this.done) {
            XSound.UI_BUTTON_CLICK.play(this.player, 5F, 5F);
            this.items.addFirst(this.items.getLast());
            this.items.removeLast();
            Iterator<KitItem> itemIter = this.items.iterator();
            for (int i = 9; i < 18; i++) {
                setItem(0, i, itemIter.next().getItem());
            }
        }

        // should we try to wrap it up?
        if (this.finish) {
            ItemStack item = getItem(13);
            KitItem kitItem = this.items.stream().filter(i -> i.getItem().isSimilar(item)).findFirst().orElse(null);
            if (item == null) {
                this.done = true; // idk.
            } else if (item.isSimilar(this.give)) {
                if (!this.done) {
                    this.done = true;
                    if (!Settings.AUTO_EQUIP_ARMOR_ROULETTE.getBoolean() || !ArmorType.equip(this.player, this.give)) {

                        ItemStack processedItem = kitItem.getContent().process(this.player);
                        if (processedItem != null) {
                            Map<Integer, ItemStack> overfilled = this.player.getInventory().addItem(this.give);
                            for (ItemStack item2 : overfilled.values()) {
                                this.player.getWorld().dropItemNaturally(this.player.getLocation(), item2);
                            }
                        }
                    }

                    XSound.ENTITY_PLAYER_LEVELUP.play(this.player, 10f, 10f);
                    this.plugin.getLocale().getMessage("event.create.won")
                            .processPlaceholder("item", WordUtils.capitalize(this.give.getType().name().toLowerCase().replace("_", " ")))
                            .sendPrefixedMessage(this.player);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, this::finish, 50);
                    setAllowClose(true);
                }
            }
        }
    }

    private void finish() {
        Bukkit.getScheduler().cancelTask(this.task);
        exit();
    }
}
