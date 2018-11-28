package com.songoda.ultimatekits.utils;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.ultimatekits.UltimateKits;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.block.Banner;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * Created by songoda on 2/24/2017.
 */
public class Methods {


    public static ItemStack getGlass() {
        UltimateKits plugin = UltimateKits.getInstance();
        return Arconix.pl().getApi().getGUI().getGlass(plugin.getConfig().getBoolean("Interfaces.Replace Glass Type 1 With Rainbow Glass"), plugin.getConfig().getInt("Interfaces.Glass Type 1"));
    }

    public static ItemStack getBackgroundGlass(boolean type) {
        UltimateKits plugin = UltimateKits.getInstance();
        if (type)
            return Arconix.pl().getApi().getGUI().getGlass(false, plugin.getConfig().getInt("Interfaces.Glass Type 2"));
        else
            return Arconix.pl().getApi().getGUI().getGlass(false, plugin.getConfig().getInt("Interfaces.Glass Type 3"));
    }

    public static void fillGlass(Inventory i) {
        int nu = 0;
        while (nu != 27) {
            ItemStack glass = getGlass();
            i.setItem(nu, glass);
            nu++;
        }
    }

    public static boolean canGiveKit(Player player) {
        try {
            if (player.hasPermission("ultimatekits.cangive")) return true;

            if (player.hasPermission("essentials.kit.others")) return true;
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return false;
    }

    public static boolean pay(Player p, double amount) {
        if (UltimateKits.getInstance().getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = UltimateKits.getInstance().getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);

        net.milkbowl.vault.economy.Economy econ = rsp.getProvider();

        econ.depositPlayer(p, amount);
        return true;
    }

    public static String serializeItemStack(ItemStack item) {
        StringBuilder str = new StringBuilder(item.getType().name());
        if (item.getDurability() != 0)
            str.append(":").append(item.getDurability()).append(" ");
        else
            str.append(" ");

        str.append(item.getAmount()).append(" ");

        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName())
                str.append("title:").append(fixLine(meta.getDisplayName())).append(" ");
            if (meta.hasLore()) {
                str.append("lore:");
                int num = 0;
                for (String line : meta.getLore()) {
                    num++;
                    str.append(fixLine(line));
                    if (meta.getLore().size() != num)
                        str.append("|");
                }
                str.append(" ");
            }

            for (Enchantment ench : item.getEnchantments().keySet()) {
                str.append(ench.getName()).append(":").append(item.getEnchantmentLevel(ench)).append(" ");
            }

            Set<ItemFlag> flags = meta.getItemFlags();
            if (flags != null && !flags.isEmpty()) {
                str.append("itemflags:");
                boolean first = true;
                for (ItemFlag flag : flags) {
                    if (!first) {
                        str.append(",");
                    }
                    str.append(flag.name());
                    first = false;
                }
            }
        }

        try {
            switch (item.getType()) {
                case WRITTEN_BOOK:
                    BookMeta bookMeta = (BookMeta) item.getItemMeta();
                    if (bookMeta.hasTitle()) {
                        str.append("title:").append(bookMeta.getTitle().replace(" ", "_")).append(" ");
                    }
                    if (bookMeta.hasAuthor()) {
                        str.append("author:").append(bookMeta.getAuthor()).append(" ");
                    }
                    if (bookMeta.hasPages()) {
                        StringBuilder title = new StringBuilder(bookMeta.getAuthor());
                        int num = 0;
                        while (UltimateKits.getInstance().getDataFile().getConfig().contains("Books.pages." + title)) {
                            title.append(num);
                        }
                        str.append("id:").append(bookMeta.getAuthor()).append(" ");
                        int pNum = 0;
                        for (String page : bookMeta.getPages()) {
                            pNum++;
                            UltimateKits.getInstance().getDataFile().getConfig().set("Books.pages." + title + "." + pNum, page);
                        }
                    }
                    break;
                case ENCHANTED_BOOK:
                    EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) item.getItemMeta();
                    for (Enchantment e : enchantmentStorageMeta.getStoredEnchants().keySet()) {
                        str.append(e.getName().toLowerCase()).append(":").append(enchantmentStorageMeta.getStoredEnchantLevel(e)).append(" ");
                    }
                    break;
                case FIREWORK_ROCKET:
                    FireworkMeta fireworkMeta = (FireworkMeta) item.getItemMeta();
                    if (fireworkMeta.hasEffects()) {
                        for (FireworkEffect effect : fireworkMeta.getEffects()) {
                            if (effect.getColors() != null && !effect.getColors().isEmpty()) {
                                str.append("color:");
                                boolean first = true;
                                for (Color c : effect.getColors()) {
                                    if (!first) {
                                        str.append(",");
                                    }
                                    str.append(c.asRGB());
                                    first = false;
                                }
                                str.append(" ");
                            }

                            str.append("shape: ").append(effect.getType().name()).append(" ");
                            if (effect.getFadeColors() != null && !effect.getFadeColors().isEmpty()) {
                                str.append("fade:");
                                boolean first = true;
                                for (Color c : effect.getFadeColors()) {
                                    if (!first) {
                                        str.append(",");
                                    }
                                    str.append(c.asRGB());
                                    first = false;
                                }
                                str.append(" ");
                            }
                        }
                        str.append("power: ").append(fireworkMeta.getPower()).append(" ");
                    }
                    break;
                case POTION:
                    PotionMeta potion = ((PotionMeta) item.getItemMeta());
                    if (potion.hasColor()) {
                        str.append("color:").append(potion.getColor().asRGB()).append(" ");
                    }
                    if (potion.getBasePotionData() != null
                            && potion.getBasePotionData().getType() != null
                            && potion.getBasePotionData().getType().getEffectType() != null) {
                        PotionEffectType e = potion.getBasePotionData().getType().getEffectType();
                        str.append("effect:").append(e.getName().toLowerCase()).append(" ").append("duration:").append(e.getDurationModifier()).append(" ");
                    }
                    break;
                case PLAYER_HEAD:
                    SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
                    if (skullMeta != null && skullMeta.hasOwner()) {
                        str.append("player:").append(skullMeta.getOwningPlayer().getUniqueId().toString()).append(" ");
                    }
                    break;
                case LEATHER_HELMET:
                case LEATHER_CHESTPLATE:
                case LEATHER_LEGGINGS:
                case LEATHER_BOOTS:
                    LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) item.getItemMeta();
                    int rgb = leatherArmorMeta.getColor().asRGB();
                    str.append("color:").append(rgb).append(" ");
                    break;
                case LEGACY_BANNER: //ToDO: shouldnt be done like this, but i dont have time to do this corecctly.
                    BannerMeta bannerMeta = (BannerMeta) item.getItemMeta();
                    if (bannerMeta != null) {
                        int basecolor = bannerMeta.getBaseColor().getColor().asRGB();
                        str.append("basecolor:").append(basecolor).append(" ");
                        for (org.bukkit.block.banner.Pattern p : bannerMeta.getPatterns()) {
                            String type = p.getPattern().getIdentifier();
                            int color = p.getColor().getColor().asRGB();
                            str.append(type).append(",").append(color).append(" ");
                        }
                    }
                    break;
                case SHIELD:
                    BlockStateMeta shieldMeta = (BlockStateMeta) item.getItemMeta();
                    Banner shieldBannerMeta = (Banner) shieldMeta.getBlockState();
                    int basecolor = 0;
                    if (shieldBannerMeta.getBaseColor() != null) {
                        basecolor = shieldBannerMeta.getBaseColor().getColor().asRGB();
                    }
                    str.append("basecolor:").append(basecolor).append(" ");
                    for (org.bukkit.block.banner.Pattern p : shieldBannerMeta.getPatterns()) {
                        String type = p.getPattern().getIdentifier();
                        int color = p.getColor().getColor().asRGB();
                        str.append(type).append(",").append(color).append(" ");
                    }
                    break;
            }
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return str.toString().replace("§", "&").trim();
    }

    public static ItemStack deserializeItemStack(String string) {
        string = string.replace("&", "§");
        String[] splited = string.split("\\s+");

        String[] val = splited[0].split(":");
        ItemStack item = new ItemStack(Material.valueOf(val[0]));

        if (item.getType() == Material.PLAYER_HEAD) {
            item = new ItemStack(Material.PLAYER_HEAD, 1, (byte) 3);
        }

        ItemMeta meta = item.getItemMeta();

        if (val.length == 2) {
            item.setDurability(Short.parseShort(val[1]));
        }
        if (splited.length >= 2) {
            if (Arconix.pl().getApi().doMath().isNumeric(splited[1])) {
                item.setAmount(Integer.parseInt(splited[1]));
            }

            for (String st : splited) {
                String str = unfixLine(st);
                if (!str.contains(":")) continue;
                String[] ops = str.split(":", 2);

                String option = ops[0];
                String value = ops[1];

                if (Enchantment.getByName(option.replace(" ", "_").toUpperCase()) != null) {
                    Enchantment enchantment = Enchantment.getByName(option.replace(" ", "_").toUpperCase());
                    if (item.getType() != Material.ENCHANTED_BOOK) {
                        meta.addEnchant(enchantment, Integer.parseInt(value), true);
                    } else {
                        ((EnchantmentStorageMeta) meta).addStoredEnchant(enchantment, Integer.parseInt(value), true);
                    }
                }

                String effect = "";
                int duration = 0;
                int hit = 0;

                value = value.replace("_", " ");
                switch (option) {
                    case "title":
                        if (item.getType() == Material.WRITTEN_BOOK) {
                            ((BookMeta) meta).setTitle(value);
                        } else meta.setDisplayName(value);
                        break;
                    case "lore":
                        String[] parts = value.split("\\|");
                        ArrayList<String> lore = new ArrayList<>();
                        for (String line : parts)
                            lore.add(Arconix.pl().getApi().format().formatText(line));
                        meta.setLore(lore);
                        break;
                    case "player":
                        if (item.getType() == Material.PLAYER_HEAD) {
                            if (value.length() == 36)
                                ((SkullMeta) meta).setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(value)));
                            else
                                ((SkullMeta) meta).setOwner(value);
                        }
                        break;
                    case "author":
                        if (item.getType() == Material.WRITTEN_BOOK) {
                            ((BookMeta) meta).setAuthor(value);
                        }
                        break;
                    case "effect":
                    case "duration":
                        hit++;
                        if (option.equalsIgnoreCase("effect")) {
                            effect = value;
                        } else {
                            duration = Integer.parseInt(value);
                        }

                        if (hit == 2) {
                            PotionEffect effect2 = PotionEffectType.getByName(effect).createEffect(duration, 0);
                            ((PotionMeta) meta).addCustomEffect(effect2, false);
                        }

                        break;
                    case "id":
                        if (item.getType() == Material.WRITTEN_BOOK) {
                            if (!UltimateKits.getInstance().getDataFile().getConfig().contains("Books.pages." + value))
                                continue;
                            ConfigurationSection cs = UltimateKits.getInstance().getDataFile().getConfig().getConfigurationSection("Books.pages." + value);
                            for (String key : cs.getKeys(false)) {
                                ((BookMeta) meta).addPage(UltimateKits.getInstance().getDataFile().getConfig().getString("Books.pages." + value + "." + key));
                            }
                        }
                        break;
                    case "color":
                        switch (item.getType()) {
                            case POTION:
                                //ToDO: this
                                break;
                            case LEATHER_HELMET:
                            case LEATHER_CHESTPLATE:
                            case LEATHER_LEGGINGS:
                            case LEATHER_BOOTS:
                                ((LeatherArmorMeta) meta).setColor(Color.fromRGB(Integer.parseInt(value)));
                                break;
                        }
                        break;
                }
            }
        }
        item.setItemMeta(meta);
        return item;
    }

    public static String fixLine(String line) {
        line = line.replace(" ", "_");
        return line;
    }

    public static String unfixLine(String line) {
        line = line.replace("_", " ");
        return line;
    }

    public static String getKitFromLocation(Location location) {
        return UltimateKits.getInstance().getConfig().getString("data.block." + Arconix.pl().getApi().serialize().serializeLocation(location));
    }
}