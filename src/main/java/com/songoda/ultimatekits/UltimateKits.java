package com.songoda.ultimatekits;

import com.songoda.ultimatekits.command.CommandManager;
import com.songoda.ultimatekits.conversion.Convert;
import com.songoda.ultimatekits.database.DataManager;
import com.songoda.ultimatekits.database.DataMigrationManager;
import com.songoda.ultimatekits.database.DatabaseConnector;
import com.songoda.ultimatekits.database.MySQLConnector;
import com.songoda.ultimatekits.database.SQLiteConnector;
import com.songoda.ultimatekits.economy.Economy;
import com.songoda.ultimatekits.economy.PlayerPointsEconomy;
import com.songoda.ultimatekits.economy.ReserveEconomy;
import com.songoda.ultimatekits.economy.VaultEconomy;
import com.songoda.ultimatekits.handlers.DisplayItemHandler;
import com.songoda.ultimatekits.handlers.ParticleHandler;
import com.songoda.ultimatekits.hologram.Hologram;
import com.songoda.ultimatekits.hologram.HologramHolographicDisplays;
import com.songoda.ultimatekits.key.Key;
import com.songoda.ultimatekits.key.KeyManager;
import com.songoda.ultimatekits.kit.*;
import com.songoda.ultimatekits.listeners.BlockListeners;
import com.songoda.ultimatekits.listeners.ChatListeners;
import com.songoda.ultimatekits.listeners.EntityListeners;
import com.songoda.ultimatekits.listeners.InteractListeners;
import com.songoda.ultimatekits.utils.*;
import com.songoda.ultimatekits.utils.locale.Locale;
import com.songoda.ultimatekits.utils.settings.Setting;
import com.songoda.ultimatekits.utils.settings.SettingsManager;
import com.songoda.ultimatekits.utils.updateModules.LocaleModule;
import com.songoda.update.Plugin;
import com.songoda.update.SongodaUpdate;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UltimateKits extends JavaPlugin {
    private static UltimateKits INSTANCE;

    private static CommandSender console = Bukkit.getConsoleSender();

    private Locale locale;

    private ServerVersion serverVersion = ServerVersion.fromPackageName(Bukkit.getServer().getClass().getPackage().getName());

    private ConfigWrapper kitFile = new ConfigWrapper(this, "", "kit.yml");
    private ConfigWrapper dataFile = new ConfigWrapper(this, "", "data.yml");
    private ConfigWrapper keyFile = new ConfigWrapper(this, "", "keys.yml");

    private SettingsManager settingsManager;
    private KitManager kitManager;
    private CommandManager commandManager;
    private KeyManager keyManager;
    private DisplayItemHandler displayItemHandler;
    private Hologram hologram;
    private Economy economy;

    private ItemSerializer itemSerializer;

    private DatabaseConnector databaseConnector;
    private DataMigrationManager dataMigrationManager;
    private DataManager dataManager;

    /**
     * Grab instance of UltimateKits
     *
     * @return instance of UltimateKits
     */
    public static UltimateKits getInstance() {
        return INSTANCE;
    }

    /*
     * On plugin enable.
     */

    @Override
    public void onEnable() {
        INSTANCE = this;

        console.sendMessage(Methods.formatText("&a============================="));
        console.sendMessage(Methods.formatText("&7UltimateKits " + this.getDescription().getVersion() + " by &5Songoda <3!"));
        console.sendMessage(Methods.formatText("&7Action: &aEnabling&7..."));

        try {
            this.itemSerializer = new ItemSerializer();
        } catch (NoSuchMethodException | SecurityException | ClassNotFoundException e) {
            console.sendMessage(Methods.formatText("&cCould not load the serialization class! Please report this error."));
            e.printStackTrace();
        }

        this.settingsManager = new SettingsManager(this);
        this.settingsManager.setupConfig();

        new Locale(this, "en_US");
        this.locale = Locale.getLocale(getConfig().getString("System.Language Mode"));

        new Convert(this);

        new ParticleHandler(this);
        this.displayItemHandler = new DisplayItemHandler(this);

        this.commandManager = new CommandManager(this);

        //Running Songoda Updater
        Plugin plugin = new Plugin(this, 14);
        plugin.addModule(new LocaleModule());
        SongodaUpdate.load(plugin);

        this.kitManager = new KitManager();
        this.keyManager = new KeyManager();
        this.commandManager = new CommandManager(this);

        PluginManager pluginManager = getServer().getPluginManager();

        // Setup Economy
        if (Setting.VAULT_ECONOMY.getBoolean() && pluginManager.isPluginEnabled("Vault"))
            this.economy = new VaultEconomy();
        else if (Setting.RESERVE_ECONOMY.getBoolean() && pluginManager.isPluginEnabled("Reserve"))
            this.economy = new ReserveEconomy();
        else if (Setting.PLAYER_POINTS_ECONOMY.getBoolean() && pluginManager.isPluginEnabled("PlayerPoints"))
            this.economy = new PlayerPointsEconomy();

        // Register Hologram Plugin
        if (pluginManager.isPluginEnabled("HolographicDisplays"))
            hologram = new HologramHolographicDisplays(this);

        // Event registration
        pluginManager.registerEvents(new BlockListeners(this), this);
        pluginManager.registerEvents(new ChatListeners(this), this);
        pluginManager.registerEvents(new EntityListeners(this), this);
        pluginManager.registerEvents(new InteractListeners(this), this);

        this.loadFromFile();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this::saveToFile, 6000, 6000);

        // Starting Metrics
        new Metrics(this);

        try {
            if (Setting.MYSQL_ENABLED.getBoolean()) {
                String hostname = Setting.MYSQL_HOSTNAME.getString();
                int port = Setting.MYSQL_PORT.getInt();
                String database = Setting.MYSQL_DATABASE.getString();
                String username = Setting.MYSQL_USERNAME.getString();
                String password = Setting.MYSQL_PASSWORD.getString();
                boolean useSSL = Setting.MYSQL_USE_SSL.getBoolean();

                this.databaseConnector = new MySQLConnector(this, hostname, port, database, username, password, useSSL);
                this.getLogger().info("Data handler connected using MySQL.");
            } else {
                this.databaseConnector = new SQLiteConnector(this);
                this.getLogger().info("Data handler connected using SQLite.");
            }
        } catch (Exception ex) {
            this.getLogger().severe("Fatal error trying to connect to database. " +
                    "Please make sure all your connection settings are correct and try again. Plugin has been disabled.");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        this.dataManager = new DataManager(this.databaseConnector, this);
        this.dataMigrationManager = new DataMigrationManager(this.databaseConnector, this.dataManager);
        this.dataMigrationManager.runMigrations();

        Bukkit.getScheduler().runTaskLater(this, () -> {
            this.dataManager.getBlockData((blockData) -> {
                this.kitManager.setKitLocations(blockData);
                kitManager.getKitLocations().forEach((location, data) -> {
                    if (hologram != null) UltimateKits.getInstance().getHologram().add(data);
                });
            });
        }, 20L);

        console.sendMessage(Methods.formatText("&a============================="));
    }

    /*
     * On plugin disable.
     */
    public void onDisable() {
        saveToFile();
        dataFile.saveConfig();
        this.dataManager.bulkUpdateBlockData(this.getKitManager().getKitLocations());
        kitManager.clearKits();
        console.sendMessage(Methods.formatText("&a============================="));
        console.sendMessage(Methods.formatText("&7UltimateKits " + this.getDescription().getVersion() + " by &5Songoda <3!"));
        console.sendMessage(Methods.formatText("&7Action: &cDisabling&7..."));
        console.sendMessage(Methods.formatText("&a============================="));
    }

    /*
     * Load configuration files into memory.
     */
    private void loadFromFile() {
        Bukkit.getScheduler().runTaskLater(this, () -> {

            //Empty kits from manager.
            kitManager.clearKits();

            /*
             * Register kit into KitManager from Configuration.
             */
            for (String kitName : kitFile.getConfig().getConfigurationSection("Kits").getKeys(false)) {
                int delay = kitFile.getConfig().getInt("Kits." + kitName + ".delay");
                String title = kitFile.getConfig().getString("Kits." + kitName + ".title");
                String link = kitFile.getConfig().getString("Kits." + kitName + ".link");
                Material material = kitFile.getConfig().contains("Kits." + kitName + ".displayItem") ? Material.valueOf(kitFile.getConfig().getString("Kits." + kitName + ".displayItem")) : null;
                boolean hidden = kitFile.getConfig().getBoolean("Kits." + kitName + ".hidden");
                double price = kitFile.getConfig().getDouble("Kits." + kitName + ".price");
                List<String> strContents = kitFile.getConfig().getStringList("Kits." + kitName + ".items");
                String kitAnimation = kitFile.getConfig().getString("Kits." + kitName + ".animation", KitAnimation.NONE.name());

                List<KitItem> contents = new ArrayList<>();

                for (String string : strContents) {
                    contents.add(new KitItem(string));
                }

                Kit kit = new Kit(kitName, title, link, price, material, delay, hidden, contents, KitAnimation.valueOf(kitAnimation));
                kitManager.addKit(kit);
            }

            /*
             * Register kit locations into KitManager from Configuration.
             */
            if (dataFile.getConfig().contains("BlockData")) {
                for (String key : dataFile.getConfig().getConfigurationSection("BlockData").getKeys(false)) {
                    Location location = Methods.unserializeLocation(key);
                    Kit kit = kitManager.getKit(dataFile.getConfig().getString("BlockData." + key + ".kit"));
                    KitType type = KitType.valueOf(dataFile.getConfig().getString("BlockData." + key + ".type", "PREVIEW"));
                    boolean holograms = dataFile.getConfig().getBoolean("BlockData." + key + ".holograms");
                    boolean displayItems = dataFile.getConfig().getBoolean("BlockData." + key + ".displayItems");
                    boolean particles = dataFile.getConfig().getBoolean("BlockData." + key + ".particles");
                    boolean itemOverride = dataFile.getConfig().getBoolean("BlockData." + key + ".itemOverride");

                    if (kit == null) dataFile.getConfig().set("BlockData." + key, null);
                    else
                        kitManager.addKitToLocation(kit, location, type, holograms, particles, displayItems, itemOverride);
                }
            }

            //Apply default keys.
            checkKeyDefaults();

            //Empty keys from manager.
            keyManager.clear();

            /*
             * Register keys into KitManager from Configuration.
             */
            if (keyFile.getConfig().contains("Keys")) {
                for (String keyName : keyFile.getConfig().getConfigurationSection("Keys").getKeys(false)) {
                    int amt = keyFile.getConfig().getInt("Keys." + keyName + ".Item Amount");
                    int kitAmount = keyFile.getConfig().getInt("Keys." + keyName + ".Amount of kit received");

                    Key key = new Key(keyName, amt, kitAmount);
                    keyManager.addKey(key);
                }
            }

            if (hologram != null)
                hologram.loadHolograms();

        }, 10);
    }

    public ServerVersion getServerVersion() {
        return serverVersion;
    }

    public boolean isServerVersion(ServerVersion version) {
        return serverVersion == version;
    }

    public boolean isServerVersion(ServerVersion... versions) {
        return ArrayUtils.contains(versions, serverVersion);
    }

    public boolean isServerVersionAtLeast(ServerVersion version) {
        return serverVersion.ordinal() >= version.ordinal();
    }

    /*
     * Saves registered kits to file.
     */
    private void saveToFile() {

        // Wipe old kit information
        kitFile.getConfig().set("Kits", null);

        /*
         * Save kit from KitManager to Configuration.
         */
        for (Kit kit : kitManager.getKits()) {
            kitFile.getConfig().set("Kits." + kit.getName() + ".delay", kit.getDelay());
            kitFile.getConfig().set("Kits." + kit.getName() + ".title", kit.getTitle());
            kitFile.getConfig().set("Kits." + kit.getName() + ".link", kit.getLink());
            kitFile.getConfig().set("Kits." + kit.getName() + ".price", kit.getPrice());
            kitFile.getConfig().set("Kits." + kit.getName() + ".hidden", kit.isHidden());
            kitFile.getConfig().set("Kits." + kit.getName() + ".animation", kit.getKitAnimation().name());
            if (kit.getDisplayItem() != null)
                kitFile.getConfig().set("Kits." + kit.getName() + ".displayItem", kit.getDisplayItem().toString());

            List<KitItem> contents = kit.getContents();
            List<String> strContents = new ArrayList<>();

            for (KitItem item : contents) strContents.add(item.getSerialized());

            kitFile.getConfig().set("Kits." + kit.getName() + ".items", strContents);
        }

        // Save to file
        kitFile.saveConfig();
    }

    /*
     * Insert default key list into config.
     */
    private void checkKeyDefaults() {
        if (keyFile.getConfig().contains("Keys")) return;
        keyFile.getConfig().set("Keys.Regular.Item Amount", 3);
        keyFile.getConfig().set("Keys.Regular.Amount overrides", Collections.singletonList("Tools:2"));
        keyFile.getConfig().set("Keys.Regular.Amount of kit received", 1);
        keyFile.getConfig().set("Keys.Ultra.Item Amount", -1);
        keyFile.getConfig().set("Keys.Ultra.Amount of kit received", 1);
        keyFile.getConfig().set("Keys.Insane.Item Amount", -1);
        keyFile.getConfig().set("Keys.Insane.Amount of kit received", 2);
        keyFile.saveConfig();
    }

    /**
     * Reload plugin yaml files.
     */
    public void reload() {
        this.dataManager.bulkUpdateBlockData(this.getKitManager().getKitLocations());
        kitFile.reloadConfig();
        this.locale = Locale.getLocale(getConfig().getString("System.Language Mode"));
        this.locale.reloadMessages();
        settingsManager.reloadConfig();
        loadFromFile();
        Bukkit.getScheduler().runTaskLater(this, () -> {
            this.dataManager.getBlockData((blockData) -> {
                this.kitManager.setKitLocations(blockData);
                kitManager.getKitLocations().forEach((location, data) -> {
                    UltimateKits.getInstance().getHologram().add(data);
                });
            });
        }, 20L);
    }

    /**
     * Get instance of KitManager
     *
     * @return instance of KitManager
     */
    public KitManager getKitManager() {
        return kitManager;
    }

    /**
     * Get instance of KeyManager
     *
     * @return instance of KeyManager
     */
    public KeyManager getKeyManager() {
        return keyManager;
    }

    /**
     * Grab instance of Kit File Configuration Wrapper
     *
     * @return instance of KitFile
     */
    public ConfigWrapper getKitFile() {
        return kitFile;
    }

    /**
     * Grab instance of Data File Configuration Wrapper
     *
     * @return instance of DataFile
     */
    public ConfigWrapper getDataFile() {
        return dataFile;
    }


    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public Hologram getHologram() {
        return hologram;
    }


    public Economy getEconomy() {
        return economy;
    }

    /**
     * Grab instance of the item serializer
     *
     * @return instance of ItemSerializer
     */
    public ItemSerializer getItemSerializer() {
        return this.itemSerializer;
    }

    public Locale getLocale() {
        return locale;
    }

    public DisplayItemHandler getDisplayItemHandler() {
        return displayItemHandler;
    }

    public DatabaseConnector getDatabaseConnector() {
        return databaseConnector;
    }

    public DataManager getDataManager() {
        return dataManager;
    }
}
