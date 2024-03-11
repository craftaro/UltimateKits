package com.craftaro.ultimatekits.listeners;

import com.craftaro.ultimatekits.UltimateKits;
import com.craftaro.ultimatekits.settings.Settings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListeners implements Listener {
    private final UltimateKits plugin;

    public PlayerListeners(UltimateKits plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPlayedBefore())
            return;

        if (plugin.getKitManager().getKit(Settings.STARTER_KIT.getString()) == null
                || Settings.STARTER_KIT.getString() == null
                || Settings.STARTER_KIT.getString().equalsIgnoreCase("none")) {
            return;
        }

        plugin.getKitHandler().giveKit(plugin.getKitManager().getKit(Settings.STARTER_KIT.getString()), player);
    }
}
