package com.craftaro.ultimatekits.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public enum ArmorType {
    LEATHER_BOOTS("Boots"),
    LEATHER_CHESTPLATE("Chestplate"),
    LEATHER_HELMET("Helmet"),
    LEATHER_LEGGINGS("Leggings"),
    CHAINMAIL_BOOTS("Boots"),
    CHAINMAIL_CHESTPLATE("Chestplate"),
    CHAINMAIL_HELMET("Helmet"),
    CHAINMAIL_LEGGINGS("Leggings"),
    IRON_BOOTS("Boots"),
    IRON_CHESTPLATE("Chestplate"),
    IRON_HELMET("Helmet"),
    IRON_LEGGINGS("Leggings"),
    GOLDEN_BOOTS("Boots"),
    GOLDEN_CHESTPLATE("Chestplate"),
    GOLDEN_HELMET("Helmet"),
    GOLDEN_LEGGINGS("Leggings"),
    DIAMOND_BOOTS("Boots"),
    DIAMOND_CHESTPLATE("Chestplate"),
    DIAMOND_HELMET("Helmet"),
    DIAMOND_LEGGINGS("Leggings"),
    GOLD_BOOTS("Boots"),
    GOLD_CHESTPLATE("Chestplate"),
    GOLD_HELMET("Helmet"),
    GOLD_LEGGINGS("Leggings"),
    TURTLE_HELMET("Helmet"),
    PLAYER_HEAD("Helmet"),
    ZOMBIE_HEAD("Helmet"),
    CREEPER_HEAD("Helmet"),
    DRAGON_HEAD("Helmet"),
    SKELETON_SKULL("Helmet"),
    WITHER_SKELETON_SKULL("Helmet"),
    SKULL_ITEM("Helmet"),
    ELYTRA("Chestplate"),
    SHIELD("OffHand");

    final String slot;

    ArmorType(String slot) {
        this.slot = slot;

    }

    public boolean isHelmet() {
        return this.slot.equalsIgnoreCase("Helmet");
    }

    public boolean isChestplate() {
        return this.slot.equalsIgnoreCase("Chestplate");
    }

    public boolean isLeggings() {
        return this.slot.equalsIgnoreCase("Leggings");
    }

    public boolean isBoots() {
        return this.slot.equalsIgnoreCase("Boots");
    }

    public boolean isOffHand() {
        return this.slot.equalsIgnoreCase("OffHand");
    }

    public static boolean equip(Player player, ItemStack item) {
        try {
            ArmorType type = ArmorType.valueOf(item.getType().toString());
            boolean equipped = (type.isHelmet() && player.getInventory().getHelmet() == null)
                    || (type.isChestplate() && player.getInventory().getChestplate() == null)
                    || (type.isLeggings() && player.getInventory().getLeggings() == null)
                    || (type.isBoots() && player.getInventory().getBoots() == null)
                    || (type.isOffHand() && player.getInventory().getItemInOffHand().getType() == Material.AIR);

            if (type.isHelmet() && player.getInventory().getHelmet() == null) {
                player.getInventory().setHelmet(item);
            }
            if (type.isChestplate() && player.getInventory().getChestplate() == null) {
                player.getInventory().setChestplate(item);
            }
            if (type.isLeggings() && player.getInventory().getLeggings() == null) {
                player.getInventory().setLeggings(item);
            }
            if (type.isBoots() && player.getInventory().getBoots() == null) {
                player.getInventory().setBoots(item);
            }
            if (type.isOffHand() && player.getInventory().getItemInOffHand().getType() == Material.AIR) {
                player.getInventory().setItemInOffHand(item);
            }

            return equipped;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
