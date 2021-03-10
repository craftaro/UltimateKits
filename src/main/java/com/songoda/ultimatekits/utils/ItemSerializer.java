package com.songoda.ultimatekits.utils;

import com.songoda.core.compatibility.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class ItemSerializer {

    // classes needed for reflections

    private static Class<?> classMojangsonParser;
    private static Class<?> classItemStack;
    private static Class<?> classCraftItemStack;
    private static Class<?> classNBTTagCompound;
    private static Class<?> classBukkitItemStack;

    private static Constructor<?> constructorItemStack;

    // reflected methods

    private static Method methodParseString;
    private static Method methodCreateStack;
    private static Method methodToItemStack;
    private static Method methodTobItemStack;
    private static Method methodTocItemStack;
    private static Method methodSaveTagToStack;
    private static Method methodToString;

    /**
     * Initializes all reflection methods
     *
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws ClassNotFoundException
     */
    static  {
        try {
            classMojangsonParser = Class.forName(formatNMS("net.minecraft.server.NMS.MojangsonParser"));
            classItemStack = Class.forName(formatNMS("net.minecraft.server.NMS.ItemStack"));
            classCraftItemStack = Class.forName(formatNMS("org.bukkit.craftbukkit.NMS.inventory.CraftItemStack"));
            classNBTTagCompound = Class.forName(formatNMS("net.minecraft.server.NMS.NBTTagCompound"));
            classBukkitItemStack = Class.forName("org.bukkit.inventory.ItemStack");
            methodParseString = classMojangsonParser.getMethod("parse", String.class);

            if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13))
                methodToItemStack = classItemStack.getMethod("a", classNBTTagCompound);
            else if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_11))
                constructorItemStack = classItemStack.getConstructor(classNBTTagCompound);
            else
                methodCreateStack = classItemStack.getMethod("createStack", classNBTTagCompound);
            methodTobItemStack = classCraftItemStack.getMethod("asBukkitCopy", classItemStack);

            methodTocItemStack = classCraftItemStack.getDeclaredMethod("asNMSCopy", classBukkitItemStack);
            methodSaveTagToStack = classItemStack.getMethod("save", classNBTTagCompound);
            methodToString = classNBTTagCompound.getMethod("toString");
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            e.getStackTrace();
        }
    }

    /**
     * Inserts the version declaration for any string containing NMS
     *
     * @param s the string to format, must contain NMS.
     * @return formatted string
     */
    private static String formatNMS(String s) {
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
            Object nbtTagCompound = methodParseString.invoke(null, jsonString);
            Object citemStack;

            if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13))
                citemStack = methodToItemStack.invoke(null, nbtTagCompound);
            else if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_11))
                citemStack = constructorItemStack.newInstance(nbtTagCompound);
            else
                citemStack = methodCreateStack.invoke(null, nbtTagCompound);

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
    public static String serializeItemStackToJson(ItemStack itemStack) {
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