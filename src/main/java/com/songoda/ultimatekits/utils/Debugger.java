package com.songoda.ultimatekits.utils;

import com.songoda.ultimatekits.UltimateKits;

/**
 * Created by songoda on 3/21/2017.
 */
public class Debugger {


    public static void runReport(Exception e) {
        if (isDebug()) {
            System.out.println("==============================================================");
            System.out.println("The following is an error encountered in UltimateKits.");
            System.out.println("--------------------------------------------------------------");
            e.printStackTrace();
            System.out.println("==============================================================");
        }
        sendReport(e);
    }

    public static void sendReport(Exception e) {

    }

    public static boolean isDebug() {
        UltimateKits plugin = UltimateKits.getInstance();
        return plugin.getConfig().getBoolean("System.Debugger Enabled");
    }

}
