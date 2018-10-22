package com.songoda.ultimatekits.kit.object;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class BlockEditorPlayerData {

    private Location location;
    private Player player;
    private KitBlockData kitBlockData;
    private EditorType editorType = EditorType.NOTIN;

    public Kit getKit() {
        return kitBlockData.getKit();
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = roundLocation(location);
    }

    public EditorType getEditorType() {
        return editorType;
    }

    public void setEditorType(EditorType editorType) {
        this.editorType = editorType;
    }

    public KitBlockData getKitBlockData() {
        return kitBlockData;
    }

    public void setKitBlockData(KitBlockData kitBlockData) {
        this.kitBlockData = kitBlockData;
    }

    private Location roundLocation(Location location) {
        location = location.clone();
        location.setX(location.getBlockX());
        location.setY(location.getBlockY());
        location.setZ(location.getBlockZ());
        return location;
    }

    public enum EditorType {OVERVIEW, DECOR, NOTIN}
}
