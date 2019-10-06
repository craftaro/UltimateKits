package com.songoda.ultimatekits.listeners;

import com.songoda.ultimatekits.UltimateKits;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Created by songoda on 2/24/2017.
 */
public class ChatListeners implements Listener {

    private final UltimateKits plugin;

    public ChatListeners(UltimateKits plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCommandPreprocess(AsyncPlayerChatEvent event) {
        if (event.getMessage().equalsIgnoreCase("/kit") || event.getMessage().equalsIgnoreCase("/kit")) {
            event.setCancelled(true);
        }
    }
}