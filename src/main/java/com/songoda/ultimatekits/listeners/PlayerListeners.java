package com.songoda.ultimatekits.listeners;

import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.settings.Settings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListeners implements Listener {

    private final UltimateKits instance;

    public PlayerListeners() {
        instance = UltimateKits.getInstance();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.hasPlayedBefore()) return;

        if (instance.getKitManager().getKit(Settings.STARTER_KIT.getString()) == null
                || Settings.STARTER_KIT.getString() == null
                || Settings.STARTER_KIT.getString().equalsIgnoreCase("none")) return;

        instance.getKitManager().getKit(Settings.STARTER_KIT.getString()).giveKit(player);
    }
}
