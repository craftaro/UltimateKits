package com.songoda.ultimatekits.events;

import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.editor.BlockEditorPlayerData;
import com.songoda.ultimatekits.editor.KitEditorPlayerData;
import com.songoda.ultimatekits.utils.Debugger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListeners implements Listener {

    private final UltimateKits instance;

    public QuitListeners(UltimateKits instance) {
        this.instance = instance;
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        try {
            Player p = event.getPlayer();

            KitEditorPlayerData playerData = instance.getKitEditor().getDataFor(p);

            if (!playerData.isInInventory() && playerData.getInventory().length != 0) {
                p.getInventory().setContents(playerData.getInventory());
                playerData.setInInventory(true);
                p.updateInventory();
            }
            instance.getKitEditor().removeFromInstance(p);

            BlockEditorPlayerData blockPlayerData = instance.getBlockEditor().getDataFor(p);
            blockPlayerData.setEditorType(BlockEditorPlayerData.EditorType.NOTIN);


            playerData.setEditorType(KitEditorPlayerData.EditorType.NOTIN);
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }
}

