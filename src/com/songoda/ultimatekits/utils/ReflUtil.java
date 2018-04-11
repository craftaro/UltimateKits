//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.songoda.ultimatekits.utils;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;

public class ReflUtil {
    private static String nmsVersion;
    private static Map<String, Class<?>> classCache = new HashMap();
    private static Table<Class<?>, String, Method> methodCache = HashBasedTable.create();
    private static Table<Class<?>, ReflUtil.MethodParams, Method> methodParamCache = HashBasedTable.create();
    private static Table<Class<?>, String, Field> fieldCache = HashBasedTable.create();
    private static Map<Class<?>, Constructor<?>> constructorCache = new HashMap();
    private static Table<Class<?>, ReflUtil.ConstructorParams, Constructor<?>> constructorParamCache = HashBasedTable.create();

    public ReflUtil() {
    }

    public static String getNMSVersion() {
        if (nmsVersion == null) {
            String name = Bukkit.getServer().getClass().getName();
            String[] parts = name.split("\\.");
            nmsVersion = parts[3];
        }

        return nmsVersion;
    }

    public static Class<?> getNMSClass(String className) {
        return getClassCached("net.minecraft.server." + getNMSVersion() + "." + className);
    }

    public static Class<?> getOBCClass(String className) {
        return getClassCached("org.bukkit.craftbukkit." + getNMSVersion() + "." + className);
    }

    public static Class<?> getClassCached(String className) {
        if (classCache.containsKey(className)) {
            return (Class)classCache.get(className);
        } else {
            try {
                Class<?> classForName = Class.forName(className);
                classCache.put(className, classForName);
                return classForName;
            } catch (ClassNotFoundException var2) {
                return null;
            }
        }
    }

    public static Method getMethodCached(Class<?> clazz, String methodName) {
        if (methodCache.contains(clazz, methodName)) {
            return (Method)methodCache.get(clazz, methodName);
        } else {
            try {
                Method method = clazz.getDeclaredMethod(methodName);
                method.setAccessible(true);
                methodCache.put(clazz, methodName, method);
                return method;
            } catch (NoSuchMethodException var3) {
                return null;
            }
        }
    }

    public static Method getMethodCached(Class<?> clazz, String methodName, Class... params) {
        ReflUtil.MethodParams methodParams = new ReflUtil.MethodParams(methodName, params);
        if (methodParamCache.contains(clazz, methodParams)) {
            return (Method)methodParamCache.get(clazz, methodParams);
        } else {
            try {
                Method method = clazz.getDeclaredMethod(methodName, params);
                method.setAccessible(true);
                methodParamCache.put(clazz, methodParams, method);
                return method;
            } catch (NoSuchMethodException var5) {
                return null;
            }
        }
    }

    public static Field getFieldCached(Class<?> clazz, String fieldName) {
        if (fieldCache.contains(clazz, fieldName)) {
            return (Field)fieldCache.get(clazz, fieldName);
        } else {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                fieldCache.put(clazz, fieldName, field);
                return field;
            } catch (NoSuchFieldException var3) {
                return null;
            }
        }
    }

    public static Constructor<?> getConstructorCached(Class<?> clazz) {
        if (constructorCache.containsKey(clazz)) {
            return (Constructor)constructorCache.get(clazz);
        } else {
            try {
                Constructor<?> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                constructorCache.put(clazz, constructor);
                return constructor;
            } catch (NoSuchMethodException var2) {
                return null;
            }
        }
    }

    public static Constructor<?> getConstructorCached(Class<?> clazz, Class... params) {
        ReflUtil.ConstructorParams constructorParams = new ReflUtil.ConstructorParams(params);
        if (constructorParamCache.contains(clazz, constructorParams)) {
            return (Constructor)constructorParamCache.get(clazz, constructorParams);
        } else {
            try {
                Constructor<?> constructor = clazz.getDeclaredConstructor(params);
                constructor.setAccessible(true);
                constructorParamCache.put(clazz, constructorParams, constructor);
                return constructor;
            } catch (NoSuchMethodException var4) {
                return null;
            }
        }
    }

    private static class ConstructorParams {
        private final Class<?>[] params;

        public ConstructorParams(Class<?>[] params) {
            this.params = params;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o != null && this.getClass() == o.getClass()) {
                ReflUtil.ConstructorParams that = (ReflUtil.ConstructorParams)o;
                return Arrays.deepEquals(this.params, that.params);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return Arrays.deepHashCode(this.params);
        }
    }

    private static class MethodParams {
        private final String name;
        private final Class<?>[] params;

        public MethodParams(String name, Class<?>[] params) {
            this.name = name;
            this.params = params;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (!(o instanceof ReflUtil.MethodParams)) {
                return false;
            } else {
                ReflUtil.MethodParams that = (ReflUtil.MethodParams)o;
                if (!that.canEqual(this)) {
                    return false;
                } else {
                    Object thisName = this.name;
                    Object thatName = that.name;
                    if (thisName == null) {
                        if (thatName == null) {
                            return Arrays.deepEquals(this.params, that.params);
                        }
                    } else if (thisName.equals(thatName)) {
                        return Arrays.deepEquals(this.params, that.params);
                    }

                    return false;
                }
            }
        }

        boolean canEqual(Object that) {
            return that instanceof ReflUtil.MethodParams;
        }

        public int hashCode() {
            int result = 1;
            Object thisName = this.name;
            result = result * 31 + (thisName == null ? 0 : thisName.hashCode());
            result = result * 31 + Arrays.deepHashCode(this.params);
            return result;
        }
    }
}
