package com.craftaro.ultimatekits.conversion.hooks;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Kits.Kit;
import com.craftaro.ultimatekits.UltimateKits;
import com.craftaro.ultimatekits.conversion.Hook;
import com.craftaro.ultimatekits.kit.type.KitContentCommand;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class CMIHook implements Hook {

    private CMI cmi;

    public CMIHook() {
        cmi = (CMI) UltimateKits.getInstance().getServer().getPluginManager().getPlugin("CMI");
    }

    public Set<ItemStack> getItems(String kitName) {
        Set<ItemStack> stacks = new HashSet<>();
        try {
            Kit kit = cmi.getKitsManager().getKit(kitName, true);

            for (ItemStack item : kit.getItems()) {
                if (item != null) stacks.add(item);
            }

            for (String command : kit.getCommands()) {
                stacks.add(new KitContentCommand(command).getItemForDisplay());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stacks;
    }

    public Set<String> getKits() {
        return cmi.getKitsManager().getKitMap().keySet();
    }

    public long getDelay(String kitName) {
        return cmi.getKitsManager().getKit(kitName, true).getDelay();
    }
}
