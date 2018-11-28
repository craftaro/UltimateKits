package com.songoda.ultimatekits;

import com.songoda.arconix.api.methods.formatting.TextComponent;
import com.songoda.arconix.api.utils.ConfigWrapper;
import com.songoda.arconix.plugin.Arconix;
import com.songoda.ultimatekits.command.CommandManager;
import com.songoda.ultimatekits.conversion.Convert;
import com.songoda.ultimatekits.events.*;
import com.songoda.ultimatekits.handlers.DisplayItemHandler;
import com.songoda.ultimatekits.handlers.HologramHandler;
import com.songoda.ultimatekits.handlers.ParticleHandler;
import com.songoda.ultimatekits.key.Key;
import com.songoda.ultimatekits.key.KeyManager;
import com.songoda.ultimatekits.kit.*;
import com.songoda.ultimatekits.utils.Debugger;
import com.songoda.ultimatekits.utils.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UltimateKits extends JavaPlugin {
    private static UltimateKits INSTANCE;

    private static CommandSender console = Bukkit.getConsoleSender();
    private References references;

    private ConfigWrapper langFile = new ConfigWrapper(this, "", "lang.yml");
    private ConfigWrapper kitFile = new ConfigWrapper(this, "", "kit.yml");
    private ConfigWrapper dataFile = new ConfigWrapper(this, "", "data.yml");
    private ConfigWrapper keyFile = new ConfigWrapper(this, "", "keys.yml");

    private SettingsManager settingsManager;
    private KitManager kitManager;
    private CommandManager commandManager;
    private KeyManager keyManager;
    private HologramHandler hologramHandler;
    private DisplayItemHandler displayItemHandler;

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

    private boolean checkVersion() {
        int workingVersion = 13;
        int currentVersion = Integer.parseInt(Bukkit.getServer().getClass()
                .getPackage().getName().split("\\.")[3].split("_")[1]);

        if (currentVersion < workingVersion) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
                Bukkit.getConsoleSender().sendMessage("");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "You installed the 1." + workingVersion + "+ only version of " + this.getDescription().getName() + " on a 1." + currentVersion + " server. Since you are on the wrong version we disabled the plugin for you. Please install correct version to continue using " + this.getDescription().getName() + ".");
                Bukkit.getConsoleSender().sendMessage("");
            }, 20L);
            return false;
        }
        return true;
    }

    @Override
    public void onEnable() {
        // Check to make sure the Bukkit version is compatible.
        if (!checkVersion()) return;

        INSTANCE = this;
        Arconix.pl().hook(this);

        console.sendMessage(TextComponent.formatText("&a============================="));
        console.sendMessage(TextComponent.formatText("&7UltimateKits " + this.getDescription().getVersion() + " by &5Songoda <3!"));
        console.sendMessage(TextComponent.formatText("&7Action: &aEnabling&7..."));

        this.loadLanguageFile();

        new Convert(this);

        this.references = new References();

        new ParticleHandler(this);
        this.displayItemHandler = new DisplayItemHandler(this);

        settingsManager = new SettingsManager(this);
        settingsManager.updateSettings();
        setupConfig();

        this.kitManager = new KitManager();
        this.keyManager = new KeyManager();
        this.commandManager = new CommandManager(this);
        this.hologramHandler = new HologramHandler(this);

        this.loadFromFile();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this::saveToFile, 6000, 6000);

        console.sendMessage(TextComponent.formatText("&a============================="));

        getServer().getPluginManager().registerEvents(new BlockListeners(this), this);
        getServer().getPluginManager().registerEvents(new ChatListeners(this), this);
        getServer().getPluginManager().registerEvents(new EntityListeners(this), this);
        getServer().getPluginManager().registerEvents(new InteractListeners(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListeners(this), this);
    }

    /*
     * On plugin disable.
     */
    public void onDisable() {
        saveToFile();
        kitManager.clearKits();
        console.sendMessage(TextComponent.formatText("&a============================="));
        console.sendMessage(TextComponent.formatText("&7UltimateKits " + this.getDescription().getVersion() + " by &5Songoda <3!"));
        console.sendMessage(TextComponent.formatText("&7Action: &cDisabling&7..."));
        console.sendMessage(TextComponent.formatText("&a============================="));
    }

    /*
     * Load configuration files into memory.
     */
    private void loadFromFile() {

        //Empty kits from manager.
        kitManager.clearKits();

        /*
         * Register kit into KitManager from Configuration.
         */
        for (String kitName : kitFile.getConfig().getConfigurationSection("Kits").getKeys(false)) {
            try {
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
            } catch (Exception ex) {
                console.sendMessage(TextComponent.formatText("&cYour kit &4" + kitName + " &cis setup incorrectly."));
                Debugger.runReport(ex);
            }
        }

        /*
         * Register kit locations into KitManager from Configuration.
         */
        if (dataFile.getConfig().contains("BlockData")) {
            for (String key : dataFile.getConfig().getConfigurationSection("BlockData").getKeys(false)) {
                Location location = Arconix.pl().getApi().serialize().unserializeLocation(key);
                Kit kit = kitManager.getKit(dataFile.getConfig().getString("BlockData." + key + ".kit"));
                KitType type = KitType.valueOf(dataFile.getConfig().getString("BlockData." + key + ".type", "PREVIEW"));
                boolean holograms = dataFile.getConfig().getBoolean("BlockData." + key + ".holograms");
                boolean displayItems = dataFile.getConfig().getBoolean("BlockData." + key + ".displayItems");
                boolean particles = dataFile.getConfig().getBoolean("BlockData." + key + ".particles");
                boolean itemOverride = dataFile.getConfig().getBoolean("BlockData." + key + ".itemOverride");

                if (kit == null) dataFile.getConfig().set("BlockData." + key, null);
                else kitManager.addKitToLocation(kit, location, type, holograms, particles, displayItems, itemOverride);
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

        // Wipe old block information.
        dataFile.getConfig().set("BlockData", null);

        /*
         * Save kit locations from KitManager to Configuration.
         */
        for (KitBlockData kitBlockData : kitManager.getKitLocations().values()) {
            String locationStr = Arconix.pl().getApi().serialize().serializeLocation(kitBlockData.getLocation());
            dataFile.getConfig().set("BlockData." + locationStr + ".type", kitBlockData.getType().name());
            dataFile.getConfig().set("BlockData." + locationStr + ".kit", kitBlockData.getKit().getName());
            dataFile.getConfig().set("BlockData." + locationStr + ".holograms", kitBlockData.showHologram());
            dataFile.getConfig().set("BlockData." + locationStr + ".displayItems", kitBlockData.isDisplayingItems());
            dataFile.getConfig().set("BlockData." + locationStr + ".particles", kitBlockData.hasParticles());
            dataFile.getConfig().set("BlockData." + locationStr + ".itemOverride", kitBlockData.isItemOverride());
        }

        // Save to file
        kitFile.saveConfig();
        dataFile.saveConfig();
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

    private void setupConfig() {
        settingsManager.updateSettings();
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    private void loadLanguageFile() {
        Lang.setFile(langFile.getConfig());

        for (final Lang value : Lang.values()) {
            langFile.getConfig().addDefault(value.getPath(), value.getDefault());
        }

        langFile.getConfig().options().copyDefaults(true);
        langFile.saveConfig();
    }

    /**
     * Reload plugin yaml files.
     */
    public void reload() {
        try {
            reloadConfig();
            kitFile.reloadConfig();
            langFile.reloadConfig();
            loadLanguageFile();
            this.references = new References();
            this.setupConfig();
            loadFromFile();
            hologramHandler.updateHolograms();
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

    public HologramHandler getHologramHandler() {
        return hologramHandler;
    }

    public References getReferences() {
        return references;
    }

    public DisplayItemHandler getDisplayItemHandler() {
        return displayItemHandler;
    }
}
