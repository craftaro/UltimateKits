package com.craftaro.ultimatekits.conversion.hooks;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Kits.Kit;
import com.craftaro.ultimatekits.conversion.Hook;
import com.craftaro.ultimatekits.kit.type.KitContentCommand;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class CMIHook implements Hook {
    private final CMI cmi;

    public CMIHook() {
        this.cmi = (CMI) Bukkit.getPluginManager().getPlugin("CMI");
    }

    public Set<ItemStack> getItems(String kitName) {
        Set<ItemStack> stacks = new HashSet<>();
        try {
            Kit kit = this.cmi.getKitsManager().getKit(kitName, true);

            for (ItemStack item : kit.getItems()) {
                if (item != null) {
                    stacks.add(item);
                }
            }

            for (String command : kit.getCommands()) {
                stacks.add(new KitContentCommand(command).getItemForDisplay());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stacks;
    }

    public Set<String> getKits() {
        return this.cmi.getKitsManager().getKitMap().keySet();
    }

    public long getDelay(String kitName) {
        return this.cmi.getKitsManager().getKit(kitName, true).getDelay();
    }
}
