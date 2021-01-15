package com.songoda.ultimatekits.gui;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.utils.TextUtils;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.category.Category;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

public class CategorySelectorGui extends Gui {

    private int timer;
    private static final Random rand = new Random();

    public CategorySelectorGui(UltimateKits plugin, Player player) {
        boolean glassless = Settings.DO_NOT_USE_GLASS_BORDERS.getBoolean();

        Set<Category> categories = new LinkedHashSet<>();

        for (Kit kit : plugin.getKitManager().getKits())
            if (kit.hasPermissionToPreview(player) && kit.getCategory() != null)
                categories.add(kit.getCategory());

        setTitle(plugin.getLocale().getMessage("interface.categoryselector.title").getMessage());

        int showPerRow = glassless ? 9 : 7;
        int nrows = (int) Math.ceil(categories.size() / (double) showPerRow);
        setRows(glassless ? nrows : nrows + 2);

        setItem(0, 4, GuiUtils.createButtonItem(CompatibleMaterial.BOOK,
                plugin.getLocale().getMessage("interface.categoryselector.details")
                        .processPlaceholder("player", player.getName()).getMessage().split("\\|")));

        if (!glassless) {
            setButton(rows - 1, 4, GuiUtils.createButtonItem(Settings.EXIT_ICON.getMaterial(CompatibleMaterial.OAK_DOOR),
                    UltimateKits.getInstance().getLocale().getMessage("interface.button.exit").getMessage()),
                    event -> exit());
        }

        ItemStack glass2 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_2.getMaterial());
        setDefaultItem(AIR);
        mirrorFill(0, 0, true, true, glass2);

        if (!glassless) {
            if (Settings.RAINBOW.getBoolean()) {
                animateGlass();
                timer = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                    if (inventory.getViewers().isEmpty()) return;
                    animateGlass();
                }, 20L, 20L);
                setOnClose(event -> Bukkit.getScheduler().cancelTask(timer));
            } else {
                ItemStack glass1 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_1.getMaterial());
                ItemStack glass3 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_3.getMaterial());
                mirrorFill(0, 0, true, true, glass2);
                mirrorFill(1, 0, true, true, glass2);
                mirrorFill(0, 1, true, true, glass2);
                mirrorFill(0, 2, true, true, glass3);
                mirrorFill(0, 3, false, true, glass1);
            }
        }

        int i = 10;
        for (Category category : categories) {
            setButton(i, GuiUtils.createButtonItem(CompatibleMaterial.getMaterial(category.getMaterial()),
                    TextUtils.formatText(category.getName()),
                    "",
                    plugin.getLocale().getMessage("interface.categoryselector.view").getMessage()),
                    event -> {
                        guiManager.showGUI(player, new KitSelectorGui(plugin, player, category));
                    });
            i++;
        }
    }


    private void animateGlass() {
        for (int col = 1; col < 8; ++col) {
            ItemStack it;
            if ((it = getItem(0, col)) == null || it.getType() == Material.AIR || it.getType().name().contains("PANE"))
                setItem(0, col, GuiUtils.getBorderItem(CompatibleMaterial.getGlassPaneColor(rand.nextInt(16))));
            if ((it = getItem(rows - 1, col)) == null || it.getType() == Material.AIR || it.getType().name().contains("PANE"))
                setItem(rows - 1, col, GuiUtils.getBorderItem(CompatibleMaterial.getGlassPaneColor(rand.nextInt(16))));
        }
        for (int row = 1; row + 1 < rows; ++row) {
            setItem(row, 0, GuiUtils.getBorderItem(CompatibleMaterial.getGlassPaneColor(rand.nextInt(16))));
            setItem(row, 8, GuiUtils.getBorderItem(CompatibleMaterial.getGlassPaneColor(rand.nextInt(16))));
        }
    }
}
