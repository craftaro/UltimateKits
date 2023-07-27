package com.craftaro.ultimatekits.kit;

public enum KitType {

    PREVIEW, CRATE, CLAIM;

    public static KitType getKitType(String search) {
        if (search != null) {
            for (KitType t : values()) {
                if (t.name().equalsIgnoreCase(search)) {
                    return t;
                }
            }
        }
        return null;
    }
}
