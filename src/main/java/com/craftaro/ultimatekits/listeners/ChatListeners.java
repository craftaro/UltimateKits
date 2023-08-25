package com.craftaro.ultimatekits.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListeners implements Listener {
    @EventHandler
    public void onCommandPreprocess(AsyncPlayerChatEvent event) {
        if (event.getMessage().equalsIgnoreCase("/kit") || event.getMessage().equalsIgnoreCase("/kit")) {
            event.setCancelled(true);
        }
    }
}
