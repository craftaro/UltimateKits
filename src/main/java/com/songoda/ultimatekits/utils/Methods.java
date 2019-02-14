package com.songoda.ultimatekits.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.songoda.ultimatekits.UltimateKits;

import net.milkbowl.vault.economy.Economy;

/**
 * Created by songoda on 2/24/2017.
 */
public class Methods {


    public static ItemStack getGlass() {
        try {
            UltimateKits instance = UltimateKits.getInstance();
            return Methods.getGlass(instance.getConfig().getBoolean("Interfaces.Replace Glass Type 1 With Rainbow Glass"), instance.getConfig().getInt("Interfaces.Glass Type 1"));
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return null;
    }

    public static ItemStack getBackgroundGlass(boolean type) {
        try {
            UltimateKits instance = UltimateKits.getInstance();
            if (type)
                return getGlass(false, instance.getConfig().getInt("Interfaces.Glass Type 2"));
            else
                return getGlass(false, instance.getConfig().getInt("Interfaces.Glass Type 3"));
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return null;
    }

    private static ItemStack getGlass(Boolean rainbow, int type) {
        int randomNum = 1 + (int) (Math.random() * 6);
        ItemStack glass;
        if (rainbow) {
            glass = new ItemStack(Material.LEGACY_STAINED_GLASS_PANE, 1, (short) randomNum);
        } else {
            glass = new ItemStack(Material.LEGACY_STAINED_GLASS_PANE, 1, (short) type);
        }
        ItemMeta glassmeta = glass.getItemMeta();
        glassmeta.setDisplayName("§l");
        glass.setItemMeta(glassmeta);
        return glass;
    }


    public static boolean canGiveKit(Player player) {
        try {
            if (player.hasPermission("ultimatekits.cangive")) return true;

            if (player.hasPermission("essentials.kit.others")) return true;
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return false;
    }

    public static boolean pay(Player p, double amount) {
        if (UltimateKits.getInstance().getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = UltimateKits.getInstance().getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);

        net.milkbowl.vault.economy.Economy econ = rsp.getProvider();

        econ.depositPlayer(p, amount);
        return true;
    }


    public static String getKitFromLocation(Location location) {
        return UltimateKits.getInstance().getConfig().getString("data.block." + serializeLocation(location));
    }



    /**
     * Serializes the location of the block specified.
     *
     * @param b The block whose location is to be saved.
     * @return The serialized data.
     */
    public static String serializeLocation(Block b) {
        if (b == null)
            return "";
        return serializeLocation(b.getLocation());
    }

    /**
     * Serializes the location specified.
     *
     * @param location The location that is to be saved.
     * @return The serialized data.
     */
    public static String serializeLocation(Location location) {
        if (location == null)
            return "";
        String w = location.getWorld().getName();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        String str = w + ":" + x + ":" + y + ":" + z;
        str = str.replace(".0", "").replace("/", "");
        return str;
    }

    private static Map<String, Location> serializeCache = new HashMap<>();

    /**
     * Deserializes a location from the string.
     *
     * @param str The string to parse.
     * @return The location that was serialized in the string.
     */
    public static Location unserializeLocation(String str) {
        if (str == null || str.equals(""))
            return null;
        if (serializeCache.containsKey(str)) {
            return serializeCache.get(str).clone();
        }
        String cacheKey = str;
        str = str.replace("y:", ":").replace("z:", ":").replace("w:", "").replace("x:", ":").replace("/", ".");
        List<String> args = Arrays.asList(str.split("\\s*:\\s*"));

        World world = Bukkit.getWorld(args.get(0));
        double x = Double.parseDouble(args.get(1)), y = Double.parseDouble(args.get(2)), z = Double.parseDouble(args.get(3));
        Location location = new Location(world, x, y, z, 0, 0);
        serializeCache.put(cacheKey, location.clone());
        return location;
    }


    public static String convertToInvisibleString(String s) {
        if (s == null || s.equals(""))
            return "";
        StringBuilder hidden = new StringBuilder();
        for (char c : s.toCharArray()) hidden.append(ChatColor.COLOR_CHAR + "").append(c);
        return hidden.toString();
    }

    public static String formatTitle(String text) {
        if (text == null || text.equals(""))
            return "";
        /* if (UltimateKits.getInstance().isServerVersionAtLeast(ServerVersion.V1_9)) {
            if (text.length() > 31)
                text = text.substring(0, 29) + "...";
        } */
        text = formatText(text);
        return text;
    }

    public static String formatText(String text) {
        if (text == null || text.equals(""))
            return "";
        return formatText(text, false);
    }

    public static String formatText(String text, boolean cap) {
        if (text == null || text.equals(""))
            return "";
        if (cap)
            text = text.substring(0, 1).toUpperCase() + text.substring(1);
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    /**
     * Makes the specified Unix Epoch time human readable as per the format settings in the Arconix config.
     *
     * @param time The time to convert.
     * @return A human readable string representing to specified time.
     */
    public static String makeReadable(Long time) {
        if (time == null)
            return "";
        return String.format("%d hour(s), %d min(s), %d sec(s)", TimeUnit.MILLISECONDS.toHours(time), TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)), TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
    }

    /**
     * Formats the specified double into the Economy format specified in the Arconix config.
     *
     * @param amt The double to format.
     * @return The economy formatted double.
     */
    public static String formatEconomy(double amt) {
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        return formatter.format(amt);
    }

    public static boolean isInt(String number) {
        if (number == null || number.equals(""))
            return false;
        try {
            Integer.parseInt(number);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * Determines if the provided string is a valid number (int, double, float, or otherwise).
     *
     * @param s The string to check.
     * @return <code>true</code> if the string is numeric, otherwise <code>false</code>
     */
    public static boolean isNumeric(String s) {
        if (s == null || s.equals(""))
            return false;
        return s.matches("[-+]?\\d*\\.?\\d+");
    }



    /**
     * Fills the provided inventory with glass panes of the specified color type.
     *
     * @param i    The inventory to fill.
     */
    public static void fillGlass(Inventory i) {
        ItemStack glass = getGlass();
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName("§5");
        glass.setItemMeta(glassMeta);

        int nu = 0;
        while (nu != 27) {
            i.setItem(nu, glass);
            nu++;
        }
    }

    /**
     * Adds the specified texture to the supplied head itemstack.
     *
     * @param item    A head to apply the texture to.
     * @param headURL The URL of the texture to apply.
     * @return The head with the textrue.
     */
    public static ItemStack addTexture(ItemStack item, String headURL) {
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", new Object[]{headURL}).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));

        Field profileField;
        try {
            profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        item.setItemMeta(meta);
        return item;
    }
    /**
     * Creates a glass itemstack
     *
     * @param rainbow Whether or not to assign a random color to the glass.
     * @param type    If rainbow is false, the glass color.
     * @return A glass itemstack conforming to the params.
     */
    public static ItemStack toGlass(Boolean rainbow, int type) {
        int randomNum = 1 + (int) (Math.random() * 6);
        ItemStack glass;
        if (rainbow) {
            glass = new ItemStack(Material.LEGACY_STAINED_GLASS_PANE, 1, (short) randomNum);
        } else {
            glass = new ItemStack(Material.LEGACY_STAINED_GLASS_PANE, 1, (short) type);
        }
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName("§l");
        glass.setItemMeta(glassMeta);
        return glass;
    }

}