package com.songoda.ultimatekits.player;

import com.songoda.ultimatekits.kit.object.Kit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerData {

    public enum GUILocation { NOT_IN, BUY_FINAL, KITS, DISPLAY }

    private final UUID playerUUID;
    private boolean kitMode;
    private Kit inKit = null;
    private int kitsPage = 0;
    private GUILocation guiLocation = GUILocation.NOT_IN;

    PlayerData(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(playerUUID);
    }

    public boolean isKitsMode() {
        return kitMode;
    }

    public void setKitMode(boolean kitMode) {
        this.kitMode = kitMode;
    }

    public Kit getInKit() {
        return inKit;
    }

    public void setInKit(Kit inKit) {
        this.inKit = inKit;
    }

    public int getKitsPage() {
        return kitsPage;
    }

    public void setKitsPage(int kitsPage) {
        this.kitsPage = kitsPage;
    }

    public GUILocation getGuiLocation() {
        return guiLocation;
    }

    public void setGuiLocation(GUILocation guiLocation) {
        this.guiLocation = guiLocation;
    }
}
