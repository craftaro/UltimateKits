package com.craftaro.ultimatekits.gui;

import com.craftaro.core.gui.Gui;
import com.craftaro.core.gui.GuiUtils;
import com.craftaro.core.input.ChatPrompt;
import com.craftaro.core.utils.TextUtils;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.ultimatekits.UltimateKits;
import com.craftaro.ultimatekits.category.Category;
import com.craftaro.ultimatekits.category.CategoryManager;
import com.craftaro.ultimatekits.settings.Settings;
import com.craftaro.ultimatekits.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public class CategoryEditorGui extends Gui {
    private final UltimateKits plugin;
    private final Player player;
    private final CategoryManager categoryManager;

    public CategoryEditorGui(UltimateKits plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.categoryManager = plugin.getCategoryManager();
        setRows(3);
        setTitle("Category Editor");
        setAcceptsItems(true); // display item takes an item

        // fill glass borders
        Methods.fillGlass(this);
        mirrorFill(1, 1, false, true, getDefaultItem());

        setButton(4, GuiUtils.createButtonItem(XMaterial.GREEN_DYE, "Create Category"),
                (event) -> {
                    if (this.categoryManager.getCategories().size() >= 7) {
                        plugin.getLocale().newMessage("&cYou already have the maximum amount of categories...").sendPrefixedMessage(player);
                    } else {
                        ChatPrompt.showPrompt(event.manager.getPlugin(), event.player, "Enter a category name:", response -> {
                            String msg = response.getMessage().trim();

                            String key = msg.toUpperCase().replace(" ", "_");

                            if (this.categoryManager.getCategory(key) != null) {
                                plugin.getLocale().newMessage("&cA category with that name already exists...").sendPrefixedMessage(player);
                                return;
                            }

                            this.categoryManager.addCategory(key, msg);
                            plugin.getLocale().newMessage("&aCategory added successfully!").sendPrefixedMessage(player);

                            Bukkit.getScheduler().runTask(plugin, player::closeInventory);
                            plugin.saveKits(false);
                        }).setOnClose(() -> {
                            event.manager.showGUI(event.player, new CategoryEditorGui(plugin, event.player));
                        });
                    }
                });

        // exit button
        setButton(0, 8, GuiUtils.createButtonItem(Settings.EXIT_ICON.getMaterial(XMaterial.OAK_DOOR),
                        plugin.getLocale().getMessage("interface.button.exit").getMessage()),
                ClickType.LEFT,
                event -> exit());
        paint();
    }

    private void paint() {
        List<Category> categories = this.categoryManager.getCategories();
        for (int i = 0; i < categories.size(); i++) {
            Category category = categories.get(i);
            setButton(i + 10,
                    GuiUtils.createButtonItem(XMaterial.matchXMaterial(category.getMaterial()),
                            TextUtils.formatText(category.getName()),
                            TextUtils.formatText("&6Left click to change name"),
                            "",
                            TextUtils.formatText("&6Middle click to set material"),
                            TextUtils.formatText("&6to item in hand."),
                            "",
                            TextUtils.formatText("&cRight click to remove."),
                            TextUtils.formatText("&c(Kits will not be removed)")),
                    (event) -> {
                        if (event.clickType == ClickType.LEFT) {
                            ChatPrompt.showPrompt(event.manager.getPlugin(), event.player, "Enter a name:", response -> {
                                category.setName(response.getMessage().trim());
                                event.manager.showGUI(event.player, new CategoryEditorGui(this.plugin, event.player));
                            });
                        } else if (event.clickType == ClickType.MIDDLE) {
                            category.setMaterial(this.player.getItemInHand().getType());
                            event.manager.showGUI(event.player, new CategoryEditorGui(this.plugin, event.player));
                        } else if (event.clickType == ClickType.RIGHT) {
                            this.categoryManager.removeCategory(category);
                            event.manager.showGUI(event.player, new CategoryEditorGui(this.plugin, event.player));
                        }
                    });
        }
    }
}
