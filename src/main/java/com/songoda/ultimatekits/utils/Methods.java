package com.songoda.ultimatekits.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
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

import com.songoda.arconix.plugin.Arconix;
import com.songoda.ultimatekits.UltimateKits;

import net.milkbowl.vault.economy.Economy;

/**
 * Created by songoda on 2/24/2017.
 */
public class Methods {


    public static ItemStack getGlass() {
        UltimateKits plugin = UltimateKits.getInstance();
        return Arconix.pl().getApi().getGUI().getGlass(plugin.getConfig().getBoolean("Interfaces.Replace Glass Type 1 With Rainbow Glass"), plugin.getConfig().getInt("Interfaces.Glass Type 1"));
    }

    public static ItemStack getBackgroundGlass(boolean type) {
        UltimateKits plugin = UltimateKits.getInstance();
        if (type)
            return Arconix.pl().getApi().getGUI().getGlass(false, plugin.getConfig().getInt("Interfaces.Glass Type 2"));
        else
            return Arconix.pl().getApi().getGUI().getGlass(false, plugin.getConfig().getInt("Interfaces.Glass Type 3"));
    }

    public static void fillGlass(Inventory i) {
        int nu = 0;
        while (nu != 27) {
            ItemStack glass = getGlass();
            i.setItem(nu, glass);
            nu++;
        }
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
    
    /**
     * Inserts the version declaration for any string containing NMS
     * 
     * @param s the string to format, must contain NMS.
     * @return formatted string
     */
    public static String formatNMS(String s) {
		String packageName = Bukkit.getServer().getClass().getPackage().getName();
        String nmsVersion = packageName.substring(packageName.lastIndexOf('.') + 1);
        return s.replace("NMS", nmsVersion);
    }

    /**
     * Deserializes a JSON String
     * 
     * @param jsonString the JSON String to parse
     * @return the deserialized ItemStack
     */
    public static ItemStack deserializeItemStackFromJson(String jsonString) {
        try {
        	Method parseString = Class.forName(formatNMS("net.minecraft.server.NMS.MojangsonParser")).getMethod("parse", String.class);
        	Object nbtTagCompound = parseString.invoke(null, jsonString);
        	
        	Method toItemStack = Class.forName(formatNMS("net.minecraft.server.NMS.ItemStack")).getMethod("a", nbtTagCompound.getClass());
        	Object citemStack = toItemStack.invoke(null, nbtTagCompound);
        	
        	Method tobItemStack = Class.forName(formatNMS("org.bukkit.craftbukkit.NMS.inventory.CraftItemStack")).getMethod("asBukkitCopy", citemStack.getClass());
        	return (ItemStack) tobItemStack.invoke(null, citemStack);
        	
        } catch (Exception ex) {
        	ex.printStackTrace();
            return null;
        }
    }

    /**
     * Serializes an item stack
     *
     * @param itemStack the ItemStack to parse
     * @return condensed JSON String
     */
    public static String serializeItemStackToJson(ItemStack itemStack) {
    	try {
			Method tocItemStack = Class.forName(formatNMS("org.bukkit.craftbukkit.NMS.inventory.CraftItemStack")).getDeclaredMethod("asNMSCopy", Class.forName("org.bukkit.inventory.ItemStack"));
			Object citemStack = tocItemStack.invoke(null, itemStack);
			
			Object nbtTagCompoundObject = Class.forName(formatNMS("net.minecraft.server.NMS.NBTTagCompound")).newInstance();
			Method saveTagToStack = citemStack.getClass().getMethod("save", nbtTagCompoundObject.getClass());
			saveTagToStack.invoke(citemStack, nbtTagCompoundObject);
			
			return (String) nbtTagCompoundObject.getClass().getMethod("toString").invoke(nbtTagCompoundObject);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
 
    }
    
    
    /**
     * Deserializes a string to an item stack, support both formats
     * 
     * @param string the formatted string
     * @return the deserialized ItemStack
     */
    public static ItemStack deserializeLegacyItemStack(String string) {
    	if(string.contains("{")) {
    		// string is json
    		return deserializeItemStackFromJson(string);
    	}
    	// old format
    	return deserializeItemStack(string);
    }
    
    /**
     * This method is not able to handle skulls or in general nbt tags.
     * Method is still existing for converting purposes.
     *
     * @deprecated use {@link #serializeItemStackToJson(ItemStack is)} instead.  
     */
    @Deprecated
    public static ItemStack deserializeItemStack(String string) {
        string = string.replace("&", "§");
        String[] splited = string.split("\\s+");

        String[] val = splited[0].split(":");
        ItemStack item = new ItemStack(Material.valueOf(val[0]));

        if (item.getType() == Material.PLAYER_HEAD) {
            item = new ItemStack(Material.PLAYER_HEAD, 1, (byte) 3);
        }

        ItemMeta meta = item.getItemMeta();

        if (val.length == 2) {
            item.setDurability(Short.parseShort(val[1]));
        }
        if (splited.length >= 2) {
            if (Arconix.pl().getApi().doMath().isNumeric(splited[1])) {
                item.setAmount(Integer.parseInt(splited[1]));
            }

            for (String st : splited) {
                String str = unfixLine(st);
                if (!str.contains(":")) continue;
                String[] ops = str.split(":", 2);

                String option = ops[0];
                String value = ops[1];

                if (Enchantment.getByName(option.replace(" ", "_").toUpperCase()) != null) {
                    Enchantment enchantment = Enchantment.getByName(option.replace(" ", "_").toUpperCase());
                    if (item.getType() != Material.ENCHANTED_BOOK) {
                        meta.addEnchant(enchantment, Integer.parseInt(value), true);
                    } else {
                        ((EnchantmentStorageMeta) meta).addStoredEnchant(enchantment, Integer.parseInt(value), true);
                    }
                }

                String effect = "";
                int duration = 0;
                int hit = 0;

                value = value.replace("_", " ");
                switch (option) {
                    case "title":
                        if (item.getType() == Material.WRITTEN_BOOK) {
                            ((BookMeta) meta).setTitle(value);
                        } else meta.setDisplayName(value);
                        break;
                    case "lore":
                        String[] parts = value.split("\\|");
                        ArrayList<String> lore = new ArrayList<>();
                        for (String line : parts)
                            lore.add(Arconix.pl().getApi().format().formatText(line));
                        meta.setLore(lore);
                        break;
                    case "player":
                        if (item.getType() == Material.PLAYER_HEAD) {
                            if (value.length() == 36)
                                ((SkullMeta) meta).setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(value)));
                            else
                                ((SkullMeta) meta).setOwner(value);
                        }
                        break;
                    case "author":
                        if (item.getType() == Material.WRITTEN_BOOK) {
                            ((BookMeta) meta).setAuthor(value);
                        }
                        break;
                    case "effect":
                    case "duration":
                        hit++;
                        if (option.equalsIgnoreCase("effect")) {
                            effect = value;
                        } else {
                            duration = Integer.parseInt(value);
                        }

                        if (hit == 2) {
                            PotionEffect effect2 = PotionEffectType.getByName(effect).createEffect(duration, 0);
                            ((PotionMeta) meta).addCustomEffect(effect2, false);
                        }

                        break;
                    case "id":
                        if (item.getType() == Material.WRITTEN_BOOK) {
                            if (!UltimateKits.getInstance().getDataFile().getConfig().contains("Books.pages." + value))
                                continue;
                            ConfigurationSection cs = UltimateKits.getInstance().getDataFile().getConfig().getConfigurationSection("Books.pages." + value);
                            for (String key : cs.getKeys(false)) {
                                ((BookMeta) meta).addPage(UltimateKits.getInstance().getDataFile().getConfig().getString("Books.pages." + value + "." + key));
                            }
                        }
                        break;
                    case "color":
                        switch (item.getType()) {
                            case POTION:
                                //ToDO: this
                                break;
                            case LEATHER_HELMET:
                            case LEATHER_CHESTPLATE:
                            case LEATHER_LEGGINGS:
                            case LEATHER_BOOTS:
                                ((LeatherArmorMeta) meta).setColor(Color.fromRGB(Integer.parseInt(value)));
                                break;
                        }
                        break;
                }
            }
        }
        item.setItemMeta(meta);
        return item;
    }

    public static String fixLine(String line) {
        line = line.replace(" ", "_");
        return line;
    }

    public static String unfixLine(String line) {
        line = line.replace("_", " ");
        return line;
    }

    public static String getKitFromLocation(Location location) {
        return UltimateKits.getInstance().getConfig().getString("data.block." + Arconix.pl().getApi().serialize().serializeLocation(location));
    }
}