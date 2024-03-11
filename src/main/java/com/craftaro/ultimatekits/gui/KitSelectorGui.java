package com.craftaro.ultimatekits.gui;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.gui.Gui;
import com.craftaro.core.gui.GuiUtils;
import com.craftaro.core.utils.ItemUtils;
import com.craftaro.core.utils.TextUtils;
import com.craftaro.core.utils.TimeUtils;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.ultimatekits.UltimateKits;
import com.craftaro.ultimatekits.category.Category;
import com.craftaro.ultimatekits.kit.Kit;
import com.craftaro.ultimatekits.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class KitSelectorGui extends Gui {
    private final Player player;
    private final UltimateKits plugin;

    private int timer;
    private final Category category;
    private List<String> kitList;
    private boolean kitsmode = false;

    private final boolean glassless;
    private final int showPerPage;

    public KitSelectorGui(UltimateKits plugin, Player player, Category category) {
        this.player = player;
        this.plugin = plugin;
        this.category = category;
        this.glassless = Settings.DO_NOT_USE_GLASS_BORDERS.getBoolean();

        setTitle(plugin.getLocale().getMessage("interface.selector.title").getMessage());
        loadKits();
        int showPerRow = this.glassless ? 9 : 7;
        int nrows = (int) Math.ceil(this.kitList.size() / (double) showPerRow);
        setRows(this.glassless ? nrows : nrows + 2);
        this.showPerPage = showPerRow * (this.glassless ? (nrows == 6 ? 6 : 5) : 4);
        setPages(this.kitList.size() / this.showPerPage);

        setItem(0, 4, GuiUtils.createButtonItem(XMaterial.BOOK,
                plugin.getLocale().getMessage("interface.selector.details")
                        .processPlaceholder("player", player.getName()).getMessage().split("\\|")));

        if (this.pages > 1) {
            this.setNextPage(this.rows - 1, 5, GuiUtils.createButtonItem(ItemUtils.getCustomHead("1b6f1a25b6bc199946472aedb370522584ff6f4e83221e5946bd2e41b5ca13b"),
                    plugin.getLocale().getMessage("interface.button.next").getMessage()));

            this.setPrevPage(this.rows - 1, 3, GuiUtils.createButtonItem(ItemUtils.getCustomHead("3ebf907494a935e955bfcadab81beafb90fb9be49c7026ba97d798d5f1a23"),
                    plugin.getLocale().getMessage("interface.button.last").getMessage()));

            this.setOnPage(pager -> showPage());
        }

        if (!this.glassless) {
            setButton(this.rows - 1, 4, GuiUtils.createButtonItem(Settings.EXIT_ICON.getMaterial(XMaterial.OAK_DOOR),
                            UltimateKits.getInstance().getLocale().getMessage("interface.button.exit").getMessage()),
                    event -> exit());
        }

        ItemStack glass2 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_2.getMaterial());
        setDefaultItem(AIR);
        mirrorFill(0, 0, true, true, glass2);

        if (!this.glassless) {
            if (Settings.RAINBOW.getBoolean()) {
                animateGlass();
                this.timer = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                    if (this.inventory.getViewers().isEmpty()) {
                        return;
                    }
                    animateGlass();
                }, 20L, 20L);
                setOnClose(event -> Bukkit.getScheduler().cancelTask(this.timer));
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

        if (category != null) {
            setButton(0, 0, GuiUtils.createButtonItem(ItemUtils.getCustomHead("3ebf907494a935e955bfcadab81beafb90fb9be49c7026ba97d798d5f1a23"),
                            plugin.getLocale().getMessage("interface.button.back").getMessage()),
                    event -> this.guiManager.showGUI(player, new CategorySelectorGui(plugin, player)));
        }


        showPage();
    }

    private void loadKits() {
        this.kitList = this.plugin.getKitManager().getKits().stream()
                .filter(kit -> !kit.isHidden() && kit.hasPermissionToPreview(this.player)
                        && (this.category == null || kit.getCategory() == this.category))
                .map(Kit::getKey)
                .collect(Collectors.toList());
    }

    private static final Random rand = new Random();

    private void animateGlass() {
        for (int col = 1; col < 8; ++col) {
            ItemStack it;
            if ((it = getItem(0, col)) == null || it.getType() == Material.AIR || it.getType().name().contains("PANE")) {
                setItem(0, col, GuiUtils.getBorderItem(CompatibleMaterial.getGlassPaneForColor(rand.nextInt(16))));
            }
            if ((it = getItem(this.rows - 1, col)) == null || it.getType() == Material.AIR || it.getType().name().contains("PANE")) {
                setItem(this.rows - 1, col, GuiUtils.getBorderItem(CompatibleMaterial.getGlassPaneForColor(rand.nextInt(16))));
            }
        }
        for (int row = 1; row + 1 < this.rows; ++row) {
            setItem(row, 0, GuiUtils.getBorderItem(CompatibleMaterial.getGlassPaneForColor(rand.nextInt(16))));
            setItem(row, 8, GuiUtils.getBorderItem(CompatibleMaterial.getGlassPaneForColor(rand.nextInt(16))));
        }
    }

    private void showPage() {
        int index = (this.page - 1) * this.showPerPage;
        for (int row = this.glassless ? 0 : 1; row < (!this.glassless || this.pages != 1 ? this.rows - 1 : this.rows); ++row) {
            for (int col = this.glassless ? 0 : 1; col < (this.glassless ? 9 : 8); ++col) {
                if (index >= this.kitList.size()) {
                    setItem(row, col, null);
                    clearActions(row, col);
                    continue;
                }
                final String kitItem = this.kitList.get(index++);
                final Kit kit = this.plugin.getKitManager().getKit(kitItem);

                String kitTitle = kit.getTitle() != null
                        ? ChatColor.translateAlternateColorCodes('&', kit.getTitle())
                        : this.plugin.getLocale().getMessage("interface.selector.kit")
                        .processPlaceholder("kit", TextUtils.formatText(kitItem, true)).getMessage();

                setButton(row, col, GuiUtils.createButtonItem(
                                kit.getDisplayItem() != null ? kit.getDisplayItem() : XMaterial.ENCHANTED_BOOK.parseItem(), kitTitle,
                                getKitLore(kit)),
                        event -> {
                            if (event.clickType == ClickType.MIDDLE && this.player.hasPermission("ultimatekits.admin")) {
                                this.kitsmode = !this.kitsmode;
                                showPage();
                            } else if (this.kitsmode) {
                                if (event.clickType == ClickType.RIGHT) {
                                    this.plugin.getKitManager().moveKit(kit, true);
                                } else if (event.clickType == ClickType.LEFT) {
                                    this.plugin.getKitManager().moveKit(kit, false);
                                }
                                loadKits();
                                this.plugin.saveKits(false);
                                showPage();
                            } else if (event.clickType == ClickType.LEFT) {
                                plugin.getKitHandler().display(kit, player, guiManager, this);
                            } else if (event.clickType == ClickType.RIGHT) {
                                plugin.getKitHandler().buy(kit, event.player, event.manager);
                            }
                        });
            }
        }
    }

    private List<String> getKitLore(Kit kit) {
        ArrayList<String> lore = new ArrayList<>();
        if (kit.getPrice() != 0) {
            lore.add(this.plugin.getLocale().getMessage("interface.selector.aboutkitprice")
                    .processPlaceholder("price", String.valueOf(kit.getPrice()))
                    .getMessage());
        } else if (kit.getLink() != null) {
            lore.add(this.plugin.getLocale().getMessage("general.type.link").getMessage());
        }

        if (!this.kitsmode) {
            if (!this.plugin.getLocale().getMessage("interface.selector.aboutkit").getMessage().trim().equals("")) {
                String[] parts = this.plugin.getLocale().getMessage("interface.selector.aboutkit").getMessage().split("\\|");
                lore.add("");
                for (String line : parts) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', line));
                }
            }
            if (kit.hasPermissionToClaim(this.player)) {
                if (kit.getNextUse(this.player) == -1) {
                    lore.add(this.plugin.getLocale().getMessage("event.claim.once").getMessage());
                } else if (kit.getNextUse(this.player) > 0) {
                    if (!this.plugin.getLocale().getMessage("event.claim.wait").getMessage().trim().equals("")) {
                        lore.add(this.plugin.getLocale().getMessage("event.claim.wait")
                                .processPlaceholder("time", TimeUtils.makeReadable(kit.getNextUse(this.player)))
                                .getMessage());
                    }
                } else if (!this.plugin.getLocale().getMessage("event.claim.ready").getMessage().trim().equals("")) {
                    lore.add(this.plugin.getLocale().getMessage("event.claim.ready").getMessage());
                }
            } else {
                lore.add(this.plugin.getLocale().getMessage("event.claim.noaccess").getMessage());
            }
            lore.add("");
            lore.add(this.plugin.getLocale().getMessage("interface.selector.leftpreview").getMessage());
            if (kit.hasPermissionToClaim(this.player)) {
                lore.add(this.plugin.getLocale().getMessage("interface.selector.rightclaim").getMessage());
            } else if (kit.getPrice() != 0 || kit.getLink() != null) {
                lore.add(this.plugin.getLocale().getMessage("interface.selector.rightbuy").getMessage());
            }

            if (this.player.hasPermission("ultimatekits.admin")) {
                lore.add("");
                lore.add(this.plugin.getLocale().getMessage("interface.selector.adminlore").getMessage());
            }
        } else {
            lore.addAll(Arrays.asList(this.plugin.getLocale().getMessage("interface.selector.editlore").getMessage().split("\\|")));
        }
        return lore;
    }
}
