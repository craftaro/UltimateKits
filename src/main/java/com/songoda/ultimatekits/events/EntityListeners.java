package com.songoda.ultimatekits.events;

import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.utils.Debugger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class EntityListeners implements Listener {

    private final UltimateKits instance;

    public EntityListeners(UltimateKits instance) {
        this.instance = instance;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerEntityInteract(EntityDamageEvent e) {
        try {
            if (e.getEntity().getType() != EntityType.ARMOR_STAND || instance.getConfig().getString("data.holo") == null) {
                return;
            }
            ConfigurationSection section = instance.getConfig().getConfigurationSection("data.holo");
            for (String loc : section.getKeys(false)) {
                String str[] = loc.split(":");
                World w = Bukkit.getServer().getWorld(str[1].substring(0, str[1].length() - 1));
                double x = Double.parseDouble(str[2].substring(0, str[2].length() - 1)) + .5;
                double z = Double.parseDouble(str[4]) + .5;
                if (w == e.getEntity().getLocation().getWorld() && x == e.getEntity().getLocation().getX() && z == e.getEntity().getLocation().getZ()) {
                    e.setCancelled(true);
                }
            }
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    @EventHandler
    public void onPlayerEntityInteract(PlayerInteractAtEntityEvent e) {
        try {
            if (e.getRightClicked().getType() != EntityType.ARMOR_STAND || instance.getConfig().getString("data.holo") == null) {
                return;
            }
            ConfigurationSection section = instance.getConfig().getConfigurationSection("data.holo");
            for (String loc : section.getKeys(false)) {
                String str[] = loc.split(":");
                World w = Bukkit.getServer().getWorld(str[1].substring(0, str[1].length() - 1));
                double x = Double.parseDouble(str[2].substring(0, str[2].length() - 1)) + .5;
                double z = Double.parseDouble(str[4]) + .5;
                if (w == e.getRightClicked().getLocation().getWorld() && x == e.getRightClicked().getLocation().getX() && z == e.getRightClicked().getLocation().getZ()) {
                    e.setCancelled(true);
                }
            }
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }
}
