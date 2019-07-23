package com.songoda.ultimatekits.utils.settings;

import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.utils.ServerVersion;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Setting {

    ONLY_SHOW_KITS_WITH_PERMS("Main.Only Show Players Kits They Have Permission To Use", false),
    KITS_FREE_WITH_PERMS("Main.Allow Players To Receive Kits For Free If They Have Permission", true),
    DONT_PREVIEW_COMMANDS("Main.Dont Preview Commands In Kits", false),
    HOLOGRAM_LAYOUT("Main.Hologram Layout", Arrays.asList("{TITLE}", "{LEFT-CLICK}", "{RIGHT-CLICK}")),
    SOUNDS_ENABLED("Main.Sounds Enabled", true),
    NO_REDEEM_WHEN_FULL("Main.Prevent The Redeeming of a Kit When Inventory Is Full", true),
    AUTO_EQUIP_ARMOR("Main.Automatically Equip Armor Given From a Kit", true),
    AUTO_EQUIP_ARMOR_ROULETTE("Main.Automatically Equip Armor Given From a Kit with the Roulette Animation", false),
    CHANCE_IN_PREVIEW("Main.Display Chance In Preview", true),
    CURRENCY_SYMBOL("Main.Currency Symbol", "$"),

    VAULT_ECONOMY("Economy.Use Vault Economy", true,
            "Should Vault be used?"),

    RESERVE_ECONOMY("Economy.Use Reserve Economy", true,
            "Should Reserve be used?"),

    PLAYER_POINTS_ECONOMY("Economy.Use Player Points Economy", false,
            "Should PlayerPoints be used?"),

    EXIT_ICON("Interfaces.Exit Icon", UltimateKits.getInstance().isServerVersionAtLeast(ServerVersion.V1_13) ? "OAK_DOOR" : "WOOD_DOOR"),
    BUY_ICON("Interfaces.Buy Icon", "EMERALD"),
    GLASS_TYPE_1("Interfaces.Glass Type 1", 7),
    GLASS_TYPE_2("Interfaces.Glass Type 2", 11),
    GLASS_TYPE_3("Interfaces.Glass Type 3", 3),
    RAINBOW("Interfaces.Replace Glass Type 1 With Rainbow Glass", false),
    DO_NOT_USE_GLASS_BORDERS("Interfaces.Do Not Use Glass Borders", false),

    LANGUGE_MODE("System.Language Mode", "en_US",
            "The enabled language file.",
            "More language files (if available) can be found in the plugins data folder.");

    private String setting;
    private Object option;
    private String[] comments;

    Setting(String setting, Object option, String... comments) {
        this.setting = setting;
        this.option = option;
        this.comments = comments;
    }

    Setting(String setting, Object option) {
        this.setting = setting;
        this.option = option;
        this.comments = null;
    }

    public static Setting getSetting(String setting) {
        List<Setting> settings = Arrays.stream(values()).filter(setting1 -> setting1.setting.equals(setting)).collect(Collectors.toList());
        if (settings.isEmpty()) return null;
        return settings.get(0);
    }

    public String getSetting() {
        return setting;
    }

    public Object getOption() {
        return option;
    }

    public String[] getComments() {
        return comments;
    }

    public List<Integer> getIntegerList() {
        return UltimateKits.getInstance().getConfig().getIntegerList(setting);
    }

    public List<String> getStringList() {
        return UltimateKits.getInstance().getConfig().getStringList(setting);
    }

    public boolean getBoolean() {
        return UltimateKits.getInstance().getConfig().getBoolean(setting);
    }

    public int getInt() {
        return UltimateKits.getInstance().getConfig().getInt(setting);
    }

    public long getLong() {
        return UltimateKits.getInstance().getConfig().getLong(setting);
    }

    public String getString() {
        return UltimateKits.getInstance().getConfig().getString(setting);
    }

    public char getChar() {
        return UltimateKits.getInstance().getConfig().getString(setting).charAt(0);
    }

    public double getDouble() {
        return UltimateKits.getInstance().getConfig().getDouble(setting);
    }

}