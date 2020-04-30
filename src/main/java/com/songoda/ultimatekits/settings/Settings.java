package com.songoda.ultimatekits.settings;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.configuration.Config;
import com.songoda.core.configuration.ConfigSetting;
import com.songoda.core.hooks.EconomyManager;
import com.songoda.ultimatekits.UltimateKits;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Settings {

    static final Config config = UltimateKits.getInstance().getCoreConfig();

    public static final ConfigSetting ONLY_SHOW_KITS_WITH_PERMS = new ConfigSetting(config, "Main.Only Show Players Kits They Have Permission To Use", false);
    public static final ConfigSetting KITS_FREE_WITH_PERMS = new ConfigSetting(config, "Main.Allow Players To Receive Kits For Free If They Have Permission", true,
            "I'm fully aware that this is a strange setting to have",
            "enabled by default. The reason I do this is because a lot of our users",
            "come from the plugin essentials where the user having permission to the",
            "kit allows them to get the kit for free. So when they come to this plugin",
            "they would often report this mechanic as a bug. So enabling this by default",
            "kind of made sense and we get a lot less tickets about this plugin because of that.");
    public static final ConfigSetting DONT_PREVIEW_COMMANDS = new ConfigSetting(config, "Main.Dont Preview Commands In Kits", false);
    public static final ConfigSetting HOLOGRAM_LAYOUT = new ConfigSetting(config, "Main.Hologram Layout", Arrays.asList("{TITLE}", "{LEFT-CLICK}", "{RIGHT-CLICK}"));
    public static final ConfigSetting SOUNDS_ENABLED = new ConfigSetting(config, "Main.Sounds Enabled", true);
    public static final ConfigSetting NO_REDEEM_WHEN_FULL = new ConfigSetting(config, "Main.Prevent The Redeeming of a Kit When Inventory Is Full", true);
    public static final ConfigSetting AUTO_EQUIP_ARMOR = new ConfigSetting(config, "Main.Automatically Equip Armor Given From a Kit", true);
    public static final ConfigSetting AUTO_EQUIP_ARMOR_ROULETTE = new ConfigSetting(config, "Main.Automatically Equip Armor Given From a Kit with the Roulette Animation", false);
    public static final ConfigSetting CHANCE_IN_PREVIEW = new ConfigSetting(config, "Main.Display Chance In Preview", true);
    public static final ConfigSetting CURRENCY_SYMBOL = new ConfigSetting(config, "Main.Currency Symbol", "$");
    public static final ConfigSetting STARTER_KIT = new ConfigSetting(config, "Main.Starter Kit", "none");

    public static final ConfigSetting ECONOMY_PLUGIN = new ConfigSetting(config, "Main.Economy", EconomyManager.getEconomy() == null ? "Vault" : EconomyManager.getEconomy().getName(),
            "Which economy plugin should be used?",
            "Supported plugins you have installed: \"" + EconomyManager.getManager().getRegisteredPlugins().stream().collect(Collectors.joining("\", \"")) + "\".");

    public static final ConfigSetting EXIT_ICON = new ConfigSetting(config, "Interfaces.Exit Icon", "OAK_DOOR");
    public static final ConfigSetting BUY_ICON = new ConfigSetting(config, "Interfaces.Buy Icon", "EMERALD");
    public static final ConfigSetting GLASS_TYPE_1 = new ConfigSetting(config, "Interfaces.Glass Type 1", 7);
    public static final ConfigSetting GLASS_TYPE_2 = new ConfigSetting(config, "Interfaces.Glass Type 2", 11);
    public static final ConfigSetting GLASS_TYPE_3 = new ConfigSetting(config, "Interfaces.Glass Type 3", 3);
    public static final ConfigSetting RAINBOW = new ConfigSetting(config, "Interfaces.Replace Glass Type 1 With Rainbow Glass", false);
    public static final ConfigSetting DO_NOT_USE_GLASS_BORDERS = new ConfigSetting(config, "Interfaces.Do Not Use Glass Borders", false);

    public static final ConfigSetting LANGUGE_MODE = new ConfigSetting(config, "System.Language Mode", "en_US",
            "The enabled language file.",
            "More language files (if available) can be found in the plugins data folder.");

    public static final ConfigSetting MYSQL_ENABLED = new ConfigSetting(config, "MySQL.Enabled", false, "Set to 'true' to use MySQL for data storage.");
    public static final ConfigSetting MYSQL_HOSTNAME = new ConfigSetting(config, "MySQL.Hostname", "localhost");
    public static final ConfigSetting MYSQL_PORT = new ConfigSetting(config, "MySQL.Port", 3306);
    public static final ConfigSetting MYSQL_DATABASE = new ConfigSetting(config, "MySQL.Database", "your-database");
    public static final ConfigSetting MYSQL_USERNAME = new ConfigSetting(config, "MySQL.Username", "user");
    public static final ConfigSetting MYSQL_PASSWORD = new ConfigSetting(config, "MySQL.Password", "pass");
    public static final ConfigSetting MYSQL_USE_SSL = new ConfigSetting(config, "MySQL.Use SSL", false);

    public static final ConfigSetting PARTICLE_AMOUNT = new ConfigSetting(config, "data.particlesettings.amount", 25);
    public static final ConfigSetting PARTICLE_TYPE = new ConfigSetting(config, "data.particlesettings.type", "SPELL_WITCH");
    
    /**
     * In order to set dynamic economy comment correctly, this needs to be
     * called after EconomyManager load
     */
    public static void setupConfig() {
        config
                .setDefaultComment("Main", "General settings and options.")
                .setDefaultComment("Interfaces", 
                        "These settings allow you to alter the way interfaces look.",
                        "They are used in GUI's to make patterns, change them up then open up a",
                        "# GUI to see how it works.")
                .setDefaultComment("System", "System related settings.");
        config.load();
        config.setAutoremove(true).setAutosave(true);

        // convert glass pane settings
        int color;
        if ((color = GLASS_TYPE_1.getInt(-1)) != -1) {
            config.set(GLASS_TYPE_1.getKey(), CompatibleMaterial.getGlassPaneColor(color).name());
        }
        if ((color = GLASS_TYPE_2.getInt(-1)) != -1) {
            config.set(GLASS_TYPE_2.getKey(), CompatibleMaterial.getGlassPaneColor(color).name());
        }
        if ((color = GLASS_TYPE_3.getInt(-1)) != -1) {
            config.set(GLASS_TYPE_3.getKey(), CompatibleMaterial.getGlassPaneColor(color).name());
        }

        // convert economy settings
        if (config.getBoolean("Economy.Use Vault Economy") && EconomyManager.getManager().isEnabled("Vault")) {
            config.set("Main.Economy", "Vault");
        } else if (config.getBoolean("Economy.Use Reserve Economy") && EconomyManager.getManager().isEnabled("Reserve")) {
            config.set("Main.Economy", "Reserve");
        } else if (config.getBoolean("Economy.Use Player Points Economy") && EconomyManager.getManager().isEnabled("PlayerPoints")) {
            config.set("Main.Economy", "PlayerPoints");
        }

        // spelling correction
        if (config.contains("data.particlesettings.ammount")) {
            config.set("data.particlesettings.amount", config.getInt("data.particlesettings.ammount"));
        }

        config.saveChanges();
    }
}
