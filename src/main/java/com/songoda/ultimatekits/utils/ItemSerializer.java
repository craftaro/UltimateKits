package com.songoda.ultimatekits.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.songoda.ultimatekits.UltimateKits;

public class ItemSerializer {
	
	// classes needed for reflections
	
	private Class<?> classMojangsonParser =  Class.forName(formatNMS("net.minecraft.server.NMS.MojangsonParser"));
	private Class<?> classItemStack = Class.forName(formatNMS("net.minecraft.server.NMS.ItemStack"));
	private Class<?> classCraftItemStack = Class.forName(formatNMS("org.bukkit.craftbukkit.NMS.inventory.CraftItemStack"));
	private Class<?> classNBTTagCompound = Class.forName(formatNMS("net.minecraft.server.NMS.NBTTagCompound"));
	private Class<?> classBukkitItemStack = Class.forName("org.bukkit.inventory.ItemStack");
	
	// reflected methods
	
	private Method methodParseString;
	private Method methodToItemStack;
	private Method methodTobItemStack;
	private Method methodTocItemStack;
	private Method methodSaveTagToStack;
	private Method methodToString;
	
	/**
	 * Initializes all reflection methods
	 * 
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws ClassNotFoundException
	 */
	public ItemSerializer() throws NoSuchMethodException, SecurityException, ClassNotFoundException {
		methodParseString = classMojangsonParser.getMethod("parse", String.class);
		methodToItemStack = classItemStack.getMethod("a", classNBTTagCompound);
		methodTobItemStack = classCraftItemStack.getMethod("asBukkitCopy", classItemStack);
		
		methodTocItemStack = classCraftItemStack.getDeclaredMethod("asNMSCopy", classBukkitItemStack);
		methodSaveTagToStack = classItemStack.getMethod("save", classNBTTagCompound);
		methodToString = classNBTTagCompound.getMethod("toString");
	}
	
	 /**
     * Inserts the version declaration for any string containing NMS
     * 
     * @param s the string to format, must contain NMS.
     * @return formatted string
     */
    private String formatNMS(String s) {
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
    public ItemStack deserializeItemStackFromJson(String jsonString) {
        try {
        	Object nbtTagCompound = methodParseString.invoke(null, jsonString);
        	Object citemStack = methodToItemStack.invoke(null, nbtTagCompound);
        	
        	return (ItemStack) methodTobItemStack.invoke(null, citemStack);
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
    public String serializeItemStackToJson(ItemStack itemStack) {
    	try {
			Object citemStack = methodTocItemStack.invoke(null, itemStack);
			Object nbtTagCompoundObject = classNBTTagCompound.newInstance();
			
			methodSaveTagToStack.invoke(citemStack, nbtTagCompoundObject);
			
			return (String) methodToString.invoke(nbtTagCompoundObject);
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
    public ItemStack deserializeLegacyItemStack(String string) {
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
    public ItemStack deserializeItemStack(String string) {
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
            if (Methods.isNumeric(splited[1])) {
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
                            lore.add(Methods.formatText(line));
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
    
    private String fixLine(String line) {
        line = line.replace(" ", "_");
        return line;
    }

    private String unfixLine(String line) {
        line = line.replace("_", " ");
        return line;
    }
}
