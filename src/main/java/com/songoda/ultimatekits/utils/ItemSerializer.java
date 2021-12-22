package com.songoda.ultimatekits.utils;

import com.songoda.core.compatibility.ClassMapping;
import com.songoda.core.compatibility.MethodMapping;
import com.songoda.core.compatibility.ServerVersion;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class ItemSerializer {
    // classes needed for reflections

    private static Class<?> classNBTTagCompound;

    private static Constructor<?> constructorItemStack;

    // reflected methods

    private static Method methodParseString;
    private static Method methodCreateStack;
    private static Method methodToItemStack;
    private static Method methodTobItemStack;
    private static Method methodTocItemStack;
    private static Method methodSaveTagToStack;

    static {
        try {
            Class<?> classItemStack = ClassMapping.ITEM_STACK.getClazz();
            classNBTTagCompound = ClassMapping.NBT_TAG_COMPOUND.getClazz();

            methodParseString = MethodMapping.MOJANGSON_PARSER__PARSE.getMethod(ClassMapping.MOJANGSON_PARSER.getClazz());

            if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)) {
                methodToItemStack = classItemStack.getMethod("a", classNBTTagCompound);
            } else if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_11)) {
                constructorItemStack = classItemStack.getConstructor(classNBTTagCompound);
            } else {
                methodCreateStack = classItemStack.getMethod("createStack", classNBTTagCompound);
            }
            methodTobItemStack = ClassMapping.CRAFT_ITEM_STACK.getClazz().getMethod("asBukkitCopy", classItemStack);

            methodTocItemStack = MethodMapping.CB_ITEM_STACK__AS_NMS_COPY.getMethod(ClassMapping.CRAFT_ITEM_STACK.getClazz());
            methodSaveTagToStack = MethodMapping.ITEM_STACK__SAVE.getMethod(ClassMapping.ITEM_STACK.getClazz());
        } catch (NoSuchMethodException ex) {
            ex.getStackTrace();
        }
    }

    /**
     * Deserializes a JSON String
     *
     * @param jsonString the JSON String to parse
     *
     * @return the deserialized ItemStack
     */
    public static ItemStack deserializeItemStackFromJson(String jsonString) {
        try {
            Object nbtTagCompound = methodParseString.invoke(null, jsonString);
            Object cItemStack;

            if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)) {
                cItemStack = methodToItemStack.invoke(null, nbtTagCompound);
            } else if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_11)) {
                cItemStack = constructorItemStack.newInstance(nbtTagCompound);
            } else {
                cItemStack = methodCreateStack.invoke(null, nbtTagCompound);
            }

            return (ItemStack) methodTobItemStack.invoke(null, cItemStack);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Serializes an item stack
     *
     * @param itemStack the ItemStack to parse
     *
     * @return condensed JSON String
     */
    public static String serializeItemStackToJson(ItemStack itemStack) {
        try {
            Object citemStack = methodTocItemStack.invoke(null, itemStack);
            Object nbtTagCompoundObject = classNBTTagCompound.newInstance();

            methodSaveTagToStack.invoke(citemStack, nbtTagCompoundObject);

            return nbtTagCompoundObject.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
