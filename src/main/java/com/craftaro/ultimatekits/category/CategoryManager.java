package com.craftaro.ultimatekits.category;

import com.craftaro.core.utils.TextUtils;
import com.craftaro.ultimatekits.UltimateKits;
import com.craftaro.ultimatekits.kit.Kit;
import org.bukkit.ChatColor;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CategoryManager {

    private final UltimateKits plugin;

    private final Map<String, Category> registeredCategories = new LinkedHashMap<>();

    public CategoryManager(UltimateKits plugin) {
        this.plugin = plugin;
    }

    public Category getCategory(String key) {
        return registeredCategories.get(key);
    }

    public Category getCategoryByName(String name) {
        return registeredCategories.values().stream()
                .filter(c -> ChatColor.stripColor(TextUtils.formatText(c.getName())).equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    public Category addCategory(String key, String name) {
        Category category = new Category(key, name);
        registeredCategories.put(key, category);
        return category;
    }

    public void removeCategory(Category category) {
        registeredCategories.remove(category.getKey());
        for (Kit kit : plugin.getKitManager().getKits())
            if (kit.getCategory() == category)
                kit.setCategory(null);
    }

    public List<Category> getCategories() {
        return new LinkedList(registeredCategories.values());
    }

    public void clearCategories() {
        registeredCategories.clear();
    }
}
