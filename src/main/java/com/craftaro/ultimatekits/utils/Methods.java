package com.craftaro.ultimatekits.utils;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.gui.Gui;
import com.craftaro.core.gui.GuiUtils;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.ultimatekits.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Methods {
    private static final Random rand = new Random();
    private static final Map<String, Location> serializeCache = new HashMap<>();

    /**
     * Deserializes a location from the string.
     *
     * @param str The string to parse.
     * @return The location that was serialized in the string.
     */
    public static Location unserializeLocation(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        if (serializeCache.containsKey(str)) {
            return serializeCache.get(str).clone();
        }
        String cacheKey = str;
        str = str.replace("y:", ":").replace("z:", ":").replace("w:", "").replace("x:", ":").replace("/", ".");
        List<String> args = Arrays.asList(str.split("\\s*:\\s*"));

        World world = Bukkit.getWorld(args.get(0));
        double x = Double.parseDouble(args.get(1));
        double y = Double.parseDouble(args.get(2));
        double z = Double.parseDouble(args.get(3));
        Location location = new Location(world, x, y, z, 0, 0);
        serializeCache.put(cacheKey, location.clone());
        return location;
    }

    public static void fillGlass(Gui gui) {
        // fill center with glass
        if (Settings.RAINBOW.getBoolean()) {
            for (int row = 0; row < gui.getRows(); ++row) {
                for (int col = row == 1 ? 2 : 3; col < (row == 1 ? 7 : 6); ++col) {
                    gui.setItem(row, col, GuiUtils.getBorderItem(CompatibleMaterial.getGlassPaneForColor(rand.nextInt(16))));
                }
            }
        } else {
            gui.setDefaultItem(GuiUtils.getBorderItem(Settings.GLASS_TYPE_1.getMaterial(XMaterial.GRAY_STAINED_GLASS_PANE)));
        }

        // decorate the edges
        ItemStack glass2 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_2.getMaterial(XMaterial.BLUE_STAINED_GLASS_PANE));
        ItemStack glass3 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_3.getMaterial(XMaterial.LIGHT_BLUE_STAINED_GLASS_PANE));

        // edges will be type 3
        gui.mirrorFill(0, 2, true, true, glass3);
        gui.mirrorFill(1, 1, false, true, glass3);

        // decorate corners with type 2
        gui.mirrorFill(0, 0, true, true, glass2);
        gui.mirrorFill(1, 0, true, true, glass2);
        gui.mirrorFill(0, 1, true, true, glass2);
    }
}
