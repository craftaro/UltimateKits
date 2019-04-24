package com.songoda.ultimatekits.utils;

import com.songoda.ultimatekits.UltimateKits;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by songo on 6/4/2017.
 */
public class SettingsManager implements Listener {

    private static ConfigWrapper defs;
    private final UltimateKits instance;
    private Map<Player, String> current = new HashMap<>();
    private String pluginName = "UltimateKits";
    private Map<Player, String> cat = new HashMap<>();

    public SettingsManager(UltimateKits instance) {
        this.instance = instance;
        instance.saveResource("SettingDefinitions.yml", true);
        defs = new ConfigWrapper(instance, "", "SettingDefinitions.yml");
        defs.createNewFile("Loading data file", "UltimateKits SettingDefinitions file");
        instance.getServer().getPluginManager().registerEvents(this, instance);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory() == null
                || e.getCurrentItem() == null
                || !e.getCurrentItem().hasItemMeta()
                || !e.getCurrentItem().getItemMeta().hasDisplayName()
                || e.getWhoClicked().getOpenInventory().getTopInventory() != e.getInventory()) {
            return;
        }
        if (e.getView().getTitle().equals(pluginName + " Settings Manager")) {

            if (e.getCurrentItem().getType().name().contains("STAINED_GLASS")) {
                e.setCancelled(true);
                return;
            }

            String type = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
            cat.put((Player) e.getWhoClicked(), type);
            openEditor((Player) e.getWhoClicked());
            e.setCancelled(true);
        } else if (e.getView().getTitle().equals(pluginName + " Settings KitEditor")) {

            if (e.getCurrentItem().getType().name().contains("STAINED_GLASS")) {
                e.setCancelled(true);
                return;
            }

            Player p = (Player) e.getWhoClicked();
            e.setCancelled(true);

            String key = cat.get(p) + "." + ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());

            if (instance.getConfig().get(key).getClass().getName().equals("java.lang.Boolean")) {
                boolean bool = (Boolean) instance.getConfig().get(key);
                if (!bool)
                    instance.getConfig().set(key, true);
                else
                    instance.getConfig().set(key, false);
                finishEditing(p);
            } else {
                editObject(p, key);
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        final Player p = e.getPlayer();
        if (!current.containsKey(p)) {
            return;
        }
        switch (instance.getConfig().get(current.get(p)).getClass().getName()) {
            case "java.lang.Integer":
                instance.getConfig().set(current.get(p), Integer.parseInt(e.getMessage()));
                break;
            case "java.lang.Double":
                instance.getConfig().set(current.get(p), Double.parseDouble(e.getMessage()));
                break;
            case "java.lang.String":
                instance.getConfig().set(current.get(p), e.getMessage());
                break;
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(UltimateKits.getInstance(), () ->
                this.finishEditing(p), 0L);

        e.setCancelled(true);

    }

    private void finishEditing(Player p) {
        current.remove(p);
        instance.saveConfig();
        openEditor(p);
    }


    private void editObject(Player p, String current) {
        this.current.put(p, ChatColor.stripColor(current));
        p.closeInventory();
        p.sendMessage("");
        p.sendMessage(Methods.formatText("&7Please enter a value for &6" + current + "&7."));
        if (instance.getConfig().get(current).getClass().getName().equals("java.lang.Integer")) {
            p.sendMessage(Methods.formatText("&cUse only numbers."));
        }
        p.sendMessage("");
    }

    public void openSettingsManager(Player p) {
        Inventory i = Bukkit.createInventory(null, 27, pluginName + " Settings Manager");
        int nu = 0;
        while (nu != 27) {
            i.setItem(nu, Methods.getGlass());
            nu++;
        }

        int spot = 10;
        for (String key : instance.getConfig().getConfigurationSection("").getKeys(false)) {
            ItemStack item = new ItemStack(Material.WHITE_WOOL, 1, (byte) (spot - 9)); //ToDo: Make this function as it was meant to.
            ItemMeta meta = item.getItemMeta();
            meta.setLore(Collections.singletonList(Methods.formatText("&6Click To Edit This Category.")));
            meta.setDisplayName(Methods.formatText("&f&l" + key));
            item.setItemMeta(meta);
            i.setItem(spot, item);
            spot++;
        }
        p.openInventory(i);
    }

    private void openEditor(Player p) {
        Inventory i = Bukkit.createInventory(null, 54, pluginName + " Settings KitEditor");

        int num = 0;
        for (String key : instance.getConfig().getConfigurationSection(cat.get(p)).getKeys(true)) {
            String fKey = cat.get(p) + "." + key;
            ItemStack item = new ItemStack(Material.DIAMOND_HELMET);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(Methods.formatText("&6" + key));
            ArrayList<String> lore = new ArrayList<>();
            switch (instance.getConfig().get(fKey).getClass().getName()) {
                case "java.lang.Boolean":

                    item.setType(Material.LEVER);
                    boolean bool = (Boolean) instance.getConfig().get(fKey);

                    if (!bool)
                        lore.add(Methods.formatText("&c" + false));
                    else
                        lore.add(Methods.formatText("&a" + true));

                    break;
                case "java.lang.String":
                    item.setType(Material.PAPER);
                    String str = (String) instance.getConfig().get(fKey);
                    lore.add(Methods.formatText("&9" + str));
                    break;
                case "java.lang.Integer":
                    item.setType(Material.CLOCK);

                    int in = (Integer) instance.getConfig().get(fKey);
                    lore.add(Methods.formatText("&5" + in));
                    break;
                default:
                    continue;
            }
            if (defs.getConfig().contains(fKey)) {
                String text = defs.getConfig().getString(key);

                Pattern regex = Pattern.compile("(.{1,28}(?:\\s|$))|(.{0,28})", Pattern.DOTALL);
                Matcher m = regex.matcher(text);
                while (m.find()) {
                    if (m.end() != text.length() || m.group().length() != 0)
                        lore.add(Methods.formatText("&7" + m.group()));
                }
            }
            meta.setLore(lore);
            item.setItemMeta(meta);

            i.setItem(num, item);
            num++;
        }
        p.openInventory(i);
    }

    public void updateSettings() {
        for (settings s : settings.values()) {
            FileConfiguration config = instance.getConfig();
            if (config.contains(s.oldSetting)) {
                config.addDefault(s.setting, config.get(s.oldSetting));
                config.set(s.setting, config.get(s.oldSetting));
                config.set(s.oldSetting, null);
            } else if (s.setting.equals("Main.Upgrade Particle Type")) {
                config.addDefault(s.setting, s.option);
            } else
                config.addDefault(s.setting, s.option);
        }
    }

    public enum settings {
        o3("Only-Show-Kits-With-Perms", "Main.Only Show Players Kits They Have Permission To Use", false),
        o4("Kits-Free-With-Perms", "Main.Allow Players To Receive Kits For Free If They Have Permission", true),
        o5("Dont-Preview-Commands", "Main.Dont Preview Commands In Kits", false),
        o6("Hologram-Layout", "Main.Hologram Layout", Arrays.asList("{TITLE}", "{LEFT-CLICK}", "{RIGHT-CLICK}")),
        o7("EnableSound", "Main.Sounds Enabled", true),
        o8("Sound", "Main.Sound Played While Clicking In Inventories", "ENTITY_ENDERMAN_TELEPORT"),
        o85("-", "Main.Prevent The Redeeming of a Kit When Inventory Is Full", true),
        o342("-", "Main.Display Chance In Preview", true),
        CURRENCY_SYMBOL("-", "Main.Currency Symbol", "$"),

        o9("Exit-Icon", "Interfaces.Exit Icon", "OAK_DOOR"),
        o10("Buy-Icon", "Interfaces.Buy Icon", "EMERALD"),
        o11("Glass-Type-1", "Interfaces.Glass Type 1", 7),
        o12("Glass-Type-2", "Interfaces.Glass Type 2", 11),
        o13("Glass-Type-3", "Interfaces.Glass Type 3", 3),
        o14("Rainbow-Glass", "Interfaces.Replace Glass Type 1 With Rainbow Glass", false),
        o15("glassless", "Interfaces.Do Not Use Glass Borders", false),

        DOWNLOAD_FILES("-", "System.Download Needed Data Files", true),
        LANGUGE_MODE("-", "System.Language Mode", "en_US"),
        o16("Debug-Mode", "System.Debugger Enabled", false);

        private String setting;
        private String oldSetting;
        private Object option;

        settings(String oldSetting, String setting, Object option) {
            this.oldSetting = oldSetting;
            this.setting = setting;
            this.option = option;
        }

    }
}
