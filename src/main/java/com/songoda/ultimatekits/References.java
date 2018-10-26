package com.songoda.ultimatekits;

import com.songoda.ultimatekits.utils.Debugger;
import org.bukkit.Sound;

public class References {

    private String prefix = null;
    private boolean playSound = false;
    private Sound sound = null;

    References() {
        try {
            prefix = Lang.PREFIX.getConfigValue() + " ";

            playSound = UltimateKits.getInstance().getConfig().getBoolean("Main.Sounds Enabled");

            if (playSound) {
                sound = Sound.valueOf(UltimateKits.getInstance().getConfig().getString("Main.Sound Played While Clicking In Inventories"));
            }
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public String getPrefix() {
        return this.prefix;
    }

    public boolean isPlaySound() {
        return this.playSound;
    }

    public Sound getSound() {
        return this.sound;
    }
}
