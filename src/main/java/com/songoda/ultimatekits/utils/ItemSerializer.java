package com.songoda.ultimatekits.utils;

import java.lang.reflect.Constructor;
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

	private Constructor<?> constructorItemStack;

	// reflected methods

	private Method methodParseString;
	private Method methodCreateStack;
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
		if (UltimateKits.getInstance().isServerVersionAtLeast(ServerVersion.V1_11))
			constructorItemStack = classItemStack.getConstructor(classNBTTagCompound);
		else
			methodCreateStack = classItemStack.getMethod("createStack", classNBTTagCompound);
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
			Object citemStack = UltimateKits.getInstance().isServerVersionAtLeast(ServerVersion.V1_11) ? constructorItemStack.newInstance(nbtTagCompound) : methodCreateStack.invoke(null, nbtTagCompound);

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
}