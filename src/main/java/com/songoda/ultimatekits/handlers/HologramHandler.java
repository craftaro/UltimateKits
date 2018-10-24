package com.songoda.ultimatekits.handlers;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.ultimatekits.Lang;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.kit.KitBlockData;
import com.songoda.ultimatekits.kit.KitType;
import com.songoda.ultimatekits.utils.Debugger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by songoda on 2/24/2017.
 */
public class HologramHandler {

    private final UltimateKits instance;


    public HologramHandler(UltimateKits instance) {
        this.instance = instance;
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(instance, this::updateHolograms, 0L, 5000L);
    }

    public void updateHolograms() {
        try {

            Map<Location, KitBlockData> kitBlocks = instance.getKitManager().getKitLocations();
            for (KitBlockData kitBlockData : kitBlocks.values()) {
                if (kitBlockData.getLocation().getWorld() == null) continue;

                KitType kitType = kitBlockData.getType();

                List<String> lines = new ArrayList<String>();

                List<String> order = instance.getConfig().getStringList("Main.Hologram Layout");

                Kit kit = kitBlockData.getKit();

                for (String o : order) {
                    switch (o.toUpperCase()) {
                        case "{TITLE}":
                            String title = kit.getTitle();
                            if (title == null) {
                                lines.add(Arconix.pl().getApi().format().formatText("&5" + Arconix.pl().getApi().format().formatText(kit.getName(), true)));
                            } else {
                                lines.add(Arconix.pl().getApi().format().formatText("&5" + Arconix.pl().getApi().format().formatText(title)));
                            }
                            break;
                        case "{RIGHT-CLICK}":
                            if (kitType == KitType.CRATE) {
                                lines.add(Arconix.pl().getApi().format().formatText(Lang.OPEN_CRATE_HOLOGRAM.getConfigValue()));
                                break;
                            }
                            if (kit.getLink() != null) {
                                lines.add(Arconix.pl().getApi().format().formatText(Lang.BUY_LINK_HOLOGRAM.getConfigValue()));
                                break;
                            }
                            if (kit.getPrice() != 0) {
                                double cost = kit.getPrice();
                                if (cost != 0) {
                                    lines.add(Arconix.pl().getApi().format().formatText(Lang.BUY_ECO_HOLOGRAM.getConfigValue(Arconix.pl().getApi().format().formatEconomy(cost))));
                                } else {
                                    lines.add(Lang.BUY_ECO_HOLOGRAM.getConfigValue(Arconix.pl().getApi().format().formatText(Lang.FREE.getConfigValue())));
                                }
                            }
                            break;
                        case "{LEFT-CLICK}":
                            if (kitType == KitType.CLAIM) {
                                lines.add(Arconix.pl().getApi().format().formatText(Lang.DAILY_HOLOGRAM.getConfigValue(kit)));
                                break;
                            }
                            if (kit.getLink() == null && kit.getPrice() == 0) {
                                lines.add(Arconix.pl().getApi().format().formatText(Lang.PREVIEW_ONLY_HOLOGRAM.getConfigValue(kit)));
                            } else {
                                lines.add(Arconix.pl().getApi().format().formatText(Lang.PREVIEW_HOLOGRAM.getConfigValue(kit)));
                            }
                            break;
                        default:
                            lines.add(Arconix.pl().getApi().format().formatText(o));
                            break;

                    }
                }

                double multi = .25 * lines.size();
                Location location = kitBlockData.getLocation();
                location.add(.5, .75, .5);
                Block b = location.getBlock();

                if (kitBlockData.isDisplayingItems()) multi += .40;

                if (b.getType() == Material.TRAPPED_CHEST
                        || b.getType() == Material.CHEST
                        || b.getType() == Material.SIGN
                        || b.getType() == Material.ENDER_CHEST) multi -= .15;

                location.add(0, multi, 0);

                remove(location);


                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(instance, () -> {
                    if (kitBlockData.showHologram()) {
                        remove(location);
                        Arconix.pl().getApi().packetLibrary.getHologramManager().spawnHolograms(location, lines);
                    }
                }, 5L);
            }
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public void remove(Location location) {


        for (Entity e : location.getWorld().getNearbyEntities(location, 1, 50, 1)) {
            if (e.getType().equals(EntityType.ARMOR_STAND))
                Arconix.pl().getApi().packetLibrary.getHologramManager().despawnHologram(e.getLocation());
        }

    }
}
