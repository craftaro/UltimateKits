package com.songoda.ultimatekits;

import com.songoda.core.SongodaCore;
import com.songoda.core.SongodaPlugin;
import com.songoda.core.commands.CommandManager;
import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.configuration.Config;
import com.songoda.core.database.DataMigrationManager;
import com.songoda.core.database.DatabaseConnector;
import com.songoda.core.database.MySQLConnector;
import com.songoda.core.database.SQLiteConnector;
import com.songoda.core.gui.GuiManager;
import com.songoda.core.hooks.EconomyManager;
import com.songoda.core.hooks.HologramManager;
import com.songoda.core.utils.TextUtils;
import com.songoda.ultimatekits.commands.CommandCreatekit;
import com.songoda.ultimatekits.commands.CommandEdit;
import com.songoda.ultimatekits.commands.CommandKey;
import com.songoda.ultimatekits.commands.CommandKit;
import com.songoda.ultimatekits.commands.CommandPreviewKit;
import com.songoda.ultimatekits.commands.CommandReload;
import com.songoda.ultimatekits.commands.CommandRemove;
import com.songoda.ultimatekits.commands.CommandSet;
import com.songoda.ultimatekits.commands.CommandSettings;
import com.songoda.ultimatekits.commands.CommandUltimateKits;
import com.songoda.ultimatekits.conversion.Convert;
import com.songoda.ultimatekits.database.DataManager;
import com.songoda.ultimatekits.database.migrations._1_InitialMigration;
import com.songoda.ultimatekits.database.migrations._2_DuplicateMigration;
import com.songoda.ultimatekits.handlers.DisplayItemHandler;
import com.songoda.ultimatekits.handlers.ParticleHandler;
import com.songoda.ultimatekits.key.Key;
import com.songoda.ultimatekits.key.KeyManager;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.kit.KitAnimation;
import com.songoda.ultimatekits.kit.KitBlockData;
import com.songoda.ultimatekits.kit.KitItem;
import com.songoda.ultimatekits.kit.KitManager;
import com.songoda.ultimatekits.kit.KitType;
import com.songoda.ultimatekits.listeners.BlockListeners;
import com.songoda.ultimatekits.listeners.ChatListeners;
import com.songoda.ultimatekits.listeners.EntityListeners;
import com.songoda.ultimatekits.listeners.InteractListeners;
import com.songoda.ultimatekits.settings.Settings;
import com.songoda.ultimatekits.utils.ItemSerializer;
import com.songoda.ultimatekits.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.PluginManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class UltimateKits extends SongodaPlugin {
    private static UltimateKits INSTANCE;

    private final Config kitFile = new Config(this, "kit.yml");
    private final Config dataFile = new Config(this, "data.yml");
    private final Config keyFile = new Config(this, "keys.yml");
    
    private final GuiManager guiManager = new GuiManager(this);
    private final ParticleHandler particleHandler = new ParticleHandler(this);
    private final DisplayItemHandler displayItemHandler = new DisplayItemHandler(this);

    private KitManager kitManager;
    private CommandManager commandManager;
    private KeyManager keyManager;

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

    @Override
    public void onPluginLoad() {
        INSTANCE = this;
        try {
            this.itemSerializer = new ItemSerializer();
        } catch (NoSuchMethodException | SecurityException | ClassNotFoundException e) {
            console.sendMessage(ChatColor.RED + "Could not load the serialization class! Please report this error.");
            e.printStackTrace();
        }
    }

    @Override
    public void onPluginEnable() {
        SongodaCore.registerPlugin(this, 14, CompatibleMaterial.BEACON);

        // Load Economy
        EconomyManager.load();
        // Register Hologram Plugin
        HologramManager.load(this);

        // Setup Config
        Settings.setupConfig();
		this.setLocale(Settings.LANGUGE_MODE.getString(), false);
        
        // Set economy preference
        EconomyManager.getManager().setPreferredHook(Settings.ECONOMY_PLUGIN.getString());

        // load kits
        dataFile.load();
        checkKeyDefaults();
        loadKits();
        keyFile.saveChanges();

        // setup commands
        this.commandManager = new CommandManager(this);
        this.commandManager.addCommand(new CommandKit(guiManager));
        this.commandManager.addCommand(new CommandPreviewKit(guiManager));
        this.commandManager.addCommand(new CommandUltimateKits())
                .addSubCommand(new CommandReload())
                .addSubCommand(new CommandSettings(guiManager))
                .addSubCommand(new CommandCreatekit(guiManager))
                .addSubCommand(new CommandEdit(guiManager))
                .addSubCommand(new CommandKey())
                .addSubCommand(new CommandSet())
                .addSubCommand(new CommandRemove());

        this.kitManager = new KitManager();
        this.keyManager = new KeyManager();
        this.commandManager = new CommandManager(this);
        
        Convert.runKitConversions();

        // Event registration
        PluginManager pluginManager = getServer().getPluginManager();
        guiManager.init();
        pluginManager.registerEvents(new BlockListeners(this), this);
        pluginManager.registerEvents(new ChatListeners(this), this);
        pluginManager.registerEvents(new EntityListeners(this), this);
        pluginManager.registerEvents(new InteractListeners(this, guiManager), this);

        try {
            if (Settings.MYSQL_ENABLED.getBoolean()) {
                String hostname = Settings.MYSQL_HOSTNAME.getString();
                int port = Settings.MYSQL_PORT.getInt();
                String database = Settings.MYSQL_DATABASE.getString();
                String username = Settings.MYSQL_USERNAME.getString();
                String password = Settings.MYSQL_PASSWORD.getString();
                boolean useSSL = Settings.MYSQL_USE_SSL.getBoolean();

                this.databaseConnector = new MySQLConnector(this, hostname, port, database, username, password, useSSL);
                this.getLogger().info("Data handler connected using MySQL.");
            } else {
                this.databaseConnector = new SQLiteConnector(this);
                this.getLogger().info("Data handler connected using SQLite.");
            }

            this.dataManager = new DataManager(this.databaseConnector, this);
            this.dataMigrationManager = new DataMigrationManager(this.databaseConnector, this.dataManager,
                    new _1_InitialMigration(),
                    new _2_DuplicateMigration(this.databaseConnector instanceof SQLiteConnector));
            this.dataMigrationManager.runMigrations();

        } catch (Exception ex) {
            this.getLogger().severe("Fatal error trying to connect to database. " +
                    "Please make sure all your connection settings are correct and try again. Plugin has been disabled.");
            emergencyStop();
            return;
        }

        displayItemHandler.start();
        particleHandler.start();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this::saveKits, 6000, 6000);
        Bukkit.getScheduler().runTaskLater(this, () -> {
            this.dataManager.getBlockData((blockData) -> {
                this.kitManager.setKitLocations(blockData);
                if(HologramManager.isEnabled()) {
                    loadHolograms();
                }
            });
        }, 20L);
    }

    @Override
    public void onPluginDisable() {
        saveKits();
        dataFile.save();
        this.dataManager.bulkUpdateBlockData(this.getKitManager().getKitLocations());
        kitManager.clearKits();
        HologramManager.removeAllHolograms();
    }

    @Override
    public List<Config> getExtraConfig() {
        return Arrays.asList(kitFile, keyFile);
    }

    @Override
    public void onConfigReload() {
        this.setLocale(Settings.LANGUGE_MODE.getString(), true);

        this.dataManager.bulkUpdateBlockData(this.getKitManager().getKitLocations());
        loadKits();
    }

    void loadKits() {
        kitFile.load();

        Bukkit.getScheduler().runTaskLater(this, () -> {

            //Empty kits from manager.
            kitManager.clearKits();

            /*
             * Register kit into KitManager from Configuration.
             */
            for (String kitName : kitFile.getConfigurationSection("Kits").getKeys(false)) {
                int delay = kitFile.getInt("Kits." + kitName + ".delay");
                String title = kitFile.getString("Kits." + kitName + ".title");
                String link = kitFile.getString("Kits." + kitName + ".link");
                CompatibleMaterial material = kitFile.contains("Kits." + kitName + ".displayItem")
                        ? CompatibleMaterial.getMaterial(kitFile.getString("Kits." + kitName + ".displayItem"), CompatibleMaterial.DIAMOND_HELMET)
                        : null;
                boolean hidden = kitFile.getBoolean("Kits." + kitName + ".hidden");
                double price = kitFile.getDouble("Kits." + kitName + ".price");
                List<String> strContents = kitFile.getStringList("Kits." + kitName + ".items");
                String kitAnimation = kitFile.getString("Kits." + kitName + ".animation", KitAnimation.NONE.name());

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
            if (dataFile.contains("BlockData")) {
                for (String key : dataFile.getConfigurationSection("BlockData").getKeys(false)) {
                    Location location = Methods.unserializeLocation(key);
                    Kit kit = kitManager.getKit(dataFile.getString("BlockData." + key + ".kit"));
                    KitType type = KitType.valueOf(dataFile.getString("BlockData." + key + ".type", "PREVIEW"));
                    boolean holograms = dataFile.getBoolean("BlockData." + key + ".holograms");
                    boolean displayItems = dataFile.getBoolean("BlockData." + key + ".displayItems");
                    boolean particles = dataFile.getBoolean("BlockData." + key + ".particles");
                    boolean itemOverride = dataFile.getBoolean("BlockData." + key + ".itemOverride");

                    if (kit == null) dataFile.set("BlockData." + key, null);
                    else {
                        updateHologram(kitManager.addKitToLocation(kit, location, type, holograms, particles, displayItems, itemOverride));
                    }
                }
            }

            //Apply default keys.
            checkKeyDefaults();

            //Empty keys from manager.
            keyManager.clear();

            /*
             * Register keys into KitManager from Configuration.
             */
            if (keyFile.contains("Keys")) {
                for (String keyName : keyFile.getConfigurationSection("Keys").getKeys(false)) {
                    int amt = keyFile.getInt("Keys." + keyName + ".Item Amount");
                    int kitAmount = keyFile.getInt("Keys." + keyName + ".Amount of kit received");

                    Key key = new Key(keyName, amt, kitAmount);
                    keyManager.addKey(key);
                }
            }
            
        }, 10);
    }

    public void removeHologram(KitBlockData data) {
        if(HologramManager.isEnabled()) {
            Location location = getKitLocation(data, Settings.HOLOGRAM_LAYOUT.getStringList().size());
            HologramManager.removeHologram(location);
        }
    }

    public void updateHologram(Kit kit) {
        for (KitBlockData data : getKitManager().getKitLocations().values()) {
            if (data.getKit() != kit) continue;
            updateHologram(data);
        }
    }

    public void updateHologram(KitBlockData data) {
        if (data != null && data.isInLoadedChunk() && HologramManager.isEnabled()) {
            List<String> lines = formatHologram(data);

            if (!lines.isEmpty()) {
                Location location = getKitLocation(data, lines.size());
                if (!data.showHologram()) {
                    HologramManager.removeHologram(location);
                } else {
                    HologramManager.updateHologram(location, lines);
                }
            }
        }
    }

    private void loadHolograms() {
        Collection<KitBlockData> kitBlocks = getKitManager().getKitLocations().values();
        if (kitBlocks.isEmpty()) return;

        for (KitBlockData data : kitBlocks) {
            updateHologram(data);
        }
    }

    private Location getKitLocation(KitBlockData data, int lines) {
        Location location = data.getLocation();
        double multi = .25 * lines;
        if (data.isDisplayingItems()) {
            multi += .40;
        }
        Material type = location.getBlock().getType();
        if (type == Material.TRAPPED_CHEST
                || type == Material.CHEST
                || type.name().contains("SIGN")
                || type == Material.ENDER_CHEST) {
            multi -= .15;
        }
        location.add(0, multi, 0);
        return location;
    }

    private List<String> formatHologram(KitBlockData data) {
        getDataManager().updateBlockData(data);
        KitType kitType = data.getType();

        ArrayList<String> lines = new ArrayList<>();

        List<String> order = Settings.HOLOGRAM_LAYOUT.getStringList();

        Kit kit = data.getKit();

        for (String o : order) {
            switch (o.toUpperCase()) {
                case "{TITLE}":
                    String title = kit.getTitle();
                    if (title == null) {
                        lines.add(ChatColor.DARK_PURPLE + TextUtils.formatText(kit.getName(), true));
                    } else {
                        lines.add(ChatColor.DARK_PURPLE + TextUtils.formatText(title));
                    }
                    break;
                case "{RIGHT-CLICK}":
                    if (kitType == KitType.CRATE) {
                        lines.add(getLocale().getMessage("interface.hologram.crate").getMessage());
                        break;
                    }
                    if (kit.getLink() != null) {
                        lines.add(getLocale().getMessage("interface.hologram.buylink").getMessage());
                        break;
                    }
                    if (kit.getPrice() != 0) {
                        lines.add(getLocale().getMessage("interface.hologram.buyeco")
                                .processPlaceholder("price", kit.getPrice() != 0
                                        ? Methods.formatEconomy(kit.getPrice())
                                        : getLocale().getMessage("general.type.free").getMessage())
                                .getMessage());
                    }
                    break;
                case "{LEFT-CLICK}":
                    if (kitType == KitType.CLAIM) {
                        lines.add(getLocale().getMessage("interface.hologram.daily").getMessage());
                        break;
                    }
                    if (kit.getLink() == null && kit.getPrice() == 0) {
                        lines.add(getLocale().getMessage("interface.hologram.previewonly").getMessage());
                    } else {
                        lines.add(getLocale().getMessage("interface.hologram.preview").getMessage());
                    }
                    break;
                default:
                    lines.add(ChatColor.translateAlternateColorCodes('&', o));
                    break;
            }
        }

        return lines;
    }

    /*
     * Saves registered kits to file.
     */
    private void saveKits() {

        // Wipe old kit information
        kitFile.set("Kits", null);

        /*
         * Save kit from KitManager to Configuration.
         */
        for (Kit kit : kitManager.getKits()) {
            kitFile.set("Kits." + kit.getName() + ".delay", kit.getDelay());
            kitFile.set("Kits." + kit.getName() + ".title", kit.getTitle());
            kitFile.set("Kits." + kit.getName() + ".link", kit.getLink());
            kitFile.set("Kits." + kit.getName() + ".price", kit.getPrice());
            kitFile.set("Kits." + kit.getName() + ".hidden", kit.isHidden());
            kitFile.set("Kits." + kit.getName() + ".animation", kit.getKitAnimation().name());
            if (kit.getDisplayItem() != null)
                kitFile.set("Kits." + kit.getName() + ".displayItem", kit.getDisplayItem().toString());

            List<KitItem> contents = kit.getContents();
            List<String> strContents = new ArrayList<>();

            for (KitItem item : contents) strContents.add(item.getSerialized());

            kitFile.set("Kits." + kit.getName() + ".items", strContents);
        }

        // Save to file
        kitFile.saveChanges();
    }

    /*
     * Insert default key list into config.
     */
    private void checkKeyDefaults() {
        if(keyFile.contains("Keys")) return;
        keyFile.set("Keys.Regular.Item Amount", 3);
        keyFile.set("Keys.Regular.Amount overrides", Collections.singletonList("Tools:2"));
        keyFile.set("Keys.Regular.Amount of kit received", 1);
        keyFile.set("Keys.Ultra.Item Amount", -1);
        keyFile.set("Keys.Ultra.Amount of kit received", 1);
        keyFile.set("Keys.Insane.Item Amount", -1);
        keyFile.set("Keys.Insane.Amount of kit received", 2);
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
    public Config getKitFile() {
        return kitFile;
    }

    /**
     * Grab instance of Data File Configuration Wrapper
     *
     * @return instance of DataFile
     */
    public Config getDataFile() {
        return dataFile;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    /**
     * Grab instance of the item serializer
     *
     * @return instance of ItemSerializer
     */
    public ItemSerializer getItemSerializer() {
        return this.itemSerializer;
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
