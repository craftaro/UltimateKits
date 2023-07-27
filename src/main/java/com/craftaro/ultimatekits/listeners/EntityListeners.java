package com.craftaro.ultimatekits.listeners;

import com.craftaro.ultimatekits.UltimateKits;
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

    private final UltimateKits plugin;

    public EntityListeners(UltimateKits plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerEntityInteract(EntityDamageEvent event) {
        if (event.getEntity().getType() != EntityType.ARMOR_STAND || plugin.getConfig().getString("data.hologramHandler") == null) {
            return;
        }
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("data.hologramHandler");
        for (String loc : section.getKeys(false)) {
            String str[] = loc.split(":");
            World world = Bukkit.getServer().getWorld(str[1].substring(0, str[1].length() - 1));
            double x = Double.parseDouble(str[2].substring(0, str[2].length() - 1)) + .5;
            double z = Double.parseDouble(str[4]) + .5;
            if (world == event.getEntity().getLocation().getWorld() && x == event.getEntity().getLocation().getX() && z == event.getEntity().getLocation().getZ()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerEntityInteract(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked().getType() != EntityType.ARMOR_STAND || plugin.getConfig().getString("data.hologramHandler") == null) {
            return;
        }
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("data.hologramHandler");
        for (String loc : section.getKeys(false)) {
            String str[] = loc.split(":");
            World w = Bukkit.getServer().getWorld(str[1].substring(0, str[1].length() - 1));
            double x = Double.parseDouble(str[2].substring(0, str[2].length() - 1)) + .5;
            double z = Double.parseDouble(str[4]) + .5;
            if (w == event.getRightClicked().getLocation().getWorld() && x == event.getRightClicked().getLocation().getX() && z == event.getRightClicked().getLocation().getZ()) {
                event.setCancelled(true);
            }
        }
    }
}
