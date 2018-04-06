package com.songoda.kitpreview;

import com.songoda.arconix.Arconix;
import com.songoda.arconix.method.formatting.TextComponent;
import com.songoda.kitpreview.api.Update;
import com.songoda.kitpreview.conversion.Convert;
import com.songoda.kitpreview.events.*;
import com.songoda.kitpreview.handlers.CommandHandler;
import com.songoda.kitpreview.handlers.DisplayItemHandler;
import com.songoda.kitpreview.handlers.HologramHandler;
import com.songoda.kitpreview.handlers.ParticleHandler;
import com.songoda.kitpreview.kits.object.Kit;
import com.songoda.kitpreview.kits.object.KitBlockData;
import com.songoda.kitpreview.kits.object.KitManager;
import com.songoda.kitpreview.utils.ConfigWrapper;
import com.songoda.kitpreview.utils.Debugger;
import com.songoda.kitpreview.utils.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KitPreview extends JavaPlugin {
    public static CommandSender console = Bukkit.getConsoleSender();

    public ConfigWrapper langFile = new ConfigWrapper(this, "", "lang.yml");
    private ConfigWrapper kitFile = new ConfigWrapper(this, "", "kits.yml");
    private ConfigWrapper dataFile = new ConfigWrapper(this, "", "data.yml");

    public References references = null;

    public HologramHandler holo = null;
    public SettingsManager sm;

    public boolean v1_7 = Bukkit.getServer().getClass().getPackage().getName().contains("1_7");
    public boolean v1_8 = Bukkit.getServer().getClass().getPackage().getName().contains("1_8");

    private static KitPreview INSTANCE;

    public Map<UUID, String> inEditor = new HashMap<>();
    public Map<UUID, Block> editing = new HashMap<>();
    public Map<UUID, Kit> editingKit = new HashMap<>();

    public Map<UUID, Integer> page = new HashMap<>();

    public Map<UUID, String> buy = new HashMap<>();
    public Map<String, String> kits = new HashMap<>();

    public Map<UUID, Kit> inKit = new HashMap<>();

    public Map<UUID, String> whereAt = new HashMap<>();

    private Convert implementation;

    private KitManager kitManager;

    public void onEnable() {
        INSTANCE = this;
        console.sendMessage(TextComponent.formatText("&a============================="));
        console.sendMessage(TextComponent.formatText("&7KitPreview " + this.getDescription().getVersion() + " by &5Brianna <3!"));
        console.sendMessage(TextComponent.formatText("&7Action: &aEnabling&7..."));
        langFile.createNewFile("Loading language file", "KitPreview language file");
        kitFile.createNewFile("Loading kits file", "KitPreview kits file");
        dataFile.createNewFile("Loading data file", "KitPreview data file");
        loadLanguageFile();

        implementation = new Convert(this);

        references = new References();

        holo = new HologramHandler(this);
        new ParticleHandler(this);
        new DisplayItemHandler(this);
        sm = new SettingsManager(this);
        sm.updateSettings();
        setupConfig();

        kitManager = new KitManager();


        /*
         * Register kits into KitManager from Configuration.
         */
        for (String kitName : kitFile.getConfig().getConfigurationSection("Kits").getKeys(false)) {
            int delay = kitFile.getConfig().getInt("Kits." + kitName + ".delay");
            String title = kitFile.getConfig().getString("Kits." + kitName + ".title");
            String link = kitFile.getConfig().getString("Kits." + kitName + ".link");
            double price = kitFile.getConfig().getDouble("Kits." + kitName + ".price");

            Kit kit = new Kit(kitName, title, link, price, delay);
            kitManager.addKit(kit);
        }

        /*
         * Register kit locations into KitManager from Configuration.
         */
        if (dataFile.getConfig().contains("BlockData")) {
            for (String key : dataFile.getConfig().getConfigurationSection("BlockData").getKeys(false)) {
                Location location = Arconix.getInstance().serialize().unserializeLocation(key);
                Kit kit = kitManager.getKit(dataFile.getConfig().getString("BlockData." + key + ".kit"));
                boolean holograms = dataFile.getConfig().getBoolean("BlockData." + key + ".holograms");
                boolean displayItems = dataFile.getConfig().getBoolean("BlockData." + key + ".displayItems");
                boolean particles = dataFile.getConfig().getBoolean("BlockData." + key + ".particles");
                kitManager.addKitToLocation(kit, location, holograms, particles, displayItems);
            }
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this::saveToFile, 6000, 6000);

        try {
            Update update = new Update(this);
            update.start();
            Bukkit.getLogger().info("MCUpdate enabled and loaded");
        } catch (IOException e) {
            Bukkit.getLogger().info("Failed initialize MCUpdate");
        }

        if (!getConfig().getBoolean("Main.Enabled Custom Kits And Kit Commands")) {
            console.sendMessage(TextComponent.formatText("&7The &a/kit&7 and &a/kits &7features have been &cdisabled&7."));
        } else {
            registerCommandDynamically("kits", new CommandHandler(this));
            registerCommandDynamically("kit", new CommandHandler(this));
        }

        console.sendMessage(TextComponent.formatText("&a============================="));

        this.getCommand("KitPreview").setExecutor(new CommandHandler(this));
        this.getCommand("PreviewKit").setExecutor(new CommandHandler(this));

        getServer().getPluginManager().registerEvents(new BlockListeners(this), this);
        getServer().getPluginManager().registerEvents(new ChatListeners(this), this);
        if (!v1_7) getServer().getPluginManager().registerEvents(new EntityListeners(this), this);
        getServer().getPluginManager().registerEvents(new InteractListeners(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListeners(this), this);
        getServer().getPluginManager().registerEvents(new QuitListeners(this), this);
    }

    private void registerCommandDynamically(String command, CommandExecutor executor) {
        try {
            // Retrieve the SimpleCommandMap from the server
            Class<?> classCraftServer = Bukkit.getServer().getClass();
            Field fieldCommandMap = classCraftServer.getDeclaredField("commandMap");
            fieldCommandMap.setAccessible(true);
            SimpleCommandMap commandMap = (SimpleCommandMap) fieldCommandMap.get(Bukkit.getServer());

            // Construct a new Command object
            Constructor<PluginCommand> constructorPluginCommand = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructorPluginCommand.setAccessible(true);
            PluginCommand commandObject = constructorPluginCommand.newInstance(command, this);
            commandObject.setExecutor(executor);

            // Register the command
            Field fieldKnownCommands = commandMap.getClass().getDeclaredField("knownCommands");
            fieldKnownCommands.setAccessible(true);
            Map<String, Command> knownCommands = (Map<String, Command>) fieldKnownCommands.get(commandMap);
            knownCommands.put(command, commandObject);
        } catch (ReflectiveOperationException e) {
            Debugger.runReport(e);
        }
    }

    private void saveToFile() {

        // Wipe old kit information
        kitFile.getConfig().set("kits", null);

        /*
         * Save kits from KitManager to Configuration.
         */
        for (Kit kit : kitManager.getKits()) {
            kitFile.getConfig().set("Kits." + kit.getName() + ".delay", kit.getDelay());
            kitFile.getConfig().set("Kits." + kit.getName() + ".title", kit.getTitle());
            kitFile.getConfig().set("Kits." + kit.getName() + ".link", kit.getLink());
            kitFile.getConfig().set("Kits." + kit.getName() + ".price", kit.getPrice());
        }

        // Wipe old block information
        dataFile.getConfig().set("BlockData", null);

        /*
         * Save kit locations from KitManager to Configuration.
         */
        for (KitBlockData kitBlockData : kitManager.getKitLocations().values()) {
            String locationStr = Arconix.getInstance().serialize().serializeLocation(kitBlockData.getLocation());
            dataFile.getConfig().set("BlockData." + locationStr + ".kit", kitBlockData.getKit().getName());
            dataFile.getConfig().set("BlockData." + locationStr + ".holograms", kitBlockData.showHologram());
            dataFile.getConfig().set("BlockData." + locationStr + ".displayItems", kitBlockData.isDisplayingItems());
            dataFile.getConfig().set("BlockData." + locationStr + ".particles", kitBlockData.hasParticles());
        }

    }

    public void onDisable() {
        for (UUID uuid : whereAt.keySet()) {
            Bukkit.getPlayer(uuid).closeInventory();
        }
        saveToFile();
        kitFile.saveConfig();
        dataFile.saveConfig();
        whereAt.clear();
        kitManager.clearKits();
        console.sendMessage(TextComponent.formatText("&a============================="));
        console.sendMessage(TextComponent.formatText("&7KitPreview " + this.getDescription().getVersion() + " by &5Brianna <3!"));
        console.sendMessage(TextComponent.formatText("&7Action: &cDisabling&7..."));
        console.sendMessage(TextComponent.formatText("&a============================="));
    }

    private void setupConfig() {
        sm.updateSettings();
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public void loadLanguageFile() {
        Lang.setFile(langFile.getConfig());

        for (final Lang value : Lang.values()) {
            langFile.getConfig().addDefault(value.getPath(), value.getDefault());
        }

        langFile.getConfig().options().copyDefaults(true);
        langFile.saveConfig();
    }

    public void reload() {
        try {
            langFile.createNewFile("Loading language file", "KitPreview language file");
            loadLanguageFile();
            references = new References();
            reloadConfig();
            saveConfig();
            holo.updateHolograms();
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }

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
     * Grab instance of Kit File Configuration Wrapper
     *
     * @return isntance of KitFile
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

    public Convert getImplementation() {
        return implementation;
    }


    /**
     * Grab instance of KitPreview
     *
     * @return instance of KitPreview
     */
    public static KitPreview getInstance() {
        return INSTANCE;
    }

}

