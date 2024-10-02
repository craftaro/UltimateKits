package com.craftaro.ultimatekits.conversion.hooks;

import com.craftaro.ultimatekits.conversion.ConversionKit;
import com.craftaro.ultimatekits.conversion.Hook;
import com.craftaro.ultimatekits.kit.type.KitContentCommand;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.*;

public class CMIHook implements Hook {

    private Object cmi;
    private Method getKitsManagerMethod;
    private Method getKitMapMethod;
    private Method getKitMethod;
    private Method getItemsMethod;
    private Method getCommandsMethod;
    private Method getDelayMethod;

    public CMIHook() {
        try {
            Class<?> cmiClass = Class.forName("com.Zrips.CMI.CMI");
            this.cmi = Bukkit.getPluginManager().getPlugin("CMI");
            Class<?> kitsManagerClass = Class.forName("com.Zrips.CMI.Modules.Kits.KitsManager");
            this.getKitsManagerMethod = cmiClass.getMethod("getKitsManager");
            Object kitsManager = this.getKitsManagerMethod.invoke(this.cmi);
            this.getKitMapMethod = kitsManagerClass.getMethod("getKitMap");
            Class<?> kitClass = Class.forName("com.Zrips.CMI.Modules.Kits.Kit");
            this.getKitMethod = kitsManagerClass.getMethod("getKit", String.class, boolean.class);
            this.getItemsMethod = kitClass.getMethod("getItems");
            this.getCommandsMethod = kitClass.getMethod("getCommands");
            this.getDelayMethod = kitClass.getMethod("getDelay");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, ConversionKit> getKits() {
        Map<String, ConversionKit> kits = new LinkedHashMap<>();
        try {
            Object kitsManager = this.getKitsManagerMethod.invoke(this.cmi);
            Map<String, Object> kitMap = (Map<String, Object>) this.getKitMapMethod.invoke(kitsManager);
            for (String kitName : kitMap.keySet()) {
                Set<ItemStack> stacks = new HashSet<>();
                try {
                    Object kit = this.getKitMethod.invoke(kitsManager, kitName, true);
                    List<ItemStack> items = (List<ItemStack>) this.getItemsMethod.invoke(kit);
                    for (ItemStack item : items) {
                        if (item != null) {
                            stacks.add(item);
                        }
                    }
                    List<String> commands = (List<String>) this.getCommandsMethod.invoke(kit);
                    for (String command : commands) {
                        stacks.add(new KitContentCommand(command).getItemForDisplay());
                    }
                    long delay = (long) this.getDelayMethod.invoke(kit);
                    kits.put(kitName, new ConversionKit(stacks, delay));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return kits;
    }
}
