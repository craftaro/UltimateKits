package com.songoda.ultimatekits.kit;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.ultimatekits.Lang;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.object.BlockEditorPlayerData;
import com.songoda.ultimatekits.kit.object.Kit;
import com.songoda.ultimatekits.kit.object.KitBlockData;
import com.songoda.ultimatekits.kit.object.KitType;
import com.songoda.ultimatekits.utils.Debugger;
import com.songoda.ultimatekits.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by songoda on 3/3/2017.
 */
public class BlockEditor {

    private final Map<UUID, BlockEditorPlayerData> blockPlayerData = new HashMap<>();

    private UltimateKits instance;

    public BlockEditor(UltimateKits instance) {
        this.instance = instance;
    }

    public void openOverview(Player player, Location location) {
        try {
            BlockEditorPlayerData playerData = getDataFor(player);
            playerData.setLocation(location);
            KitBlockData kitBlockData = instance.getKitManager().getKit(location);
            playerData.setKitBlockData(kitBlockData);

            Inventory i = Bukkit.createInventory(null, 27, Arconix.pl().getApi().format().formatText("&8This contains &a" + Arconix.pl().getApi().format().formatTitle(playerData.getKit().getShowableName())));

            Methods.fillGlass(i);

            i.setItem(0, Methods.getBackgroundGlass(true));
            i.setItem(1, Methods.getBackgroundGlass(true));
            i.setItem(2, Methods.getBackgroundGlass(false));
            i.setItem(6, Methods.getBackgroundGlass(false));
            i.setItem(7, Methods.getBackgroundGlass(true));
            i.setItem(8, Methods.getBackgroundGlass(true));
            i.setItem(9, Methods.getBackgroundGlass(true));
            i.setItem(10, Methods.getBackgroundGlass(false));
            i.setItem(16, Methods.getBackgroundGlass(false));
            i.setItem(17, Methods.getBackgroundGlass(true));
            i.setItem(18, Methods.getBackgroundGlass(true));
            i.setItem(19, Methods.getBackgroundGlass(true));
            i.setItem(20, Methods.getBackgroundGlass(false));
            i.setItem(24, Methods.getBackgroundGlass(false));
            i.setItem(25, Methods.getBackgroundGlass(true));
            i.setItem(26, Methods.getBackgroundGlass(true));

            ItemStack exit = new ItemStack(Material.valueOf(UltimateKits.getInstance().getConfig().getString("Interfaces.Exit Icon")), 1);
            ItemMeta exitmeta = exit.getItemMeta();
            exitmeta.setDisplayName(Lang.EXIT.getConfigValue());
            exit.setItemMeta(exitmeta);
            i.setItem(8, exit);

            ItemStack alli = new ItemStack(Material.COMPARATOR);
            ItemMeta allmeta = alli.getItemMeta();
            allmeta.setDisplayName(Arconix.pl().getApi().format().formatText("&5&lSwitch kit type"));
            ArrayList<String> lore = new ArrayList<>();
            lore.add(Arconix.pl().getApi().format().formatText("&7Click to swap this kit blocks function."));
            lore.add("");

            if (kitBlockData.getType() == KitType.PREVIEW) {
                lore.add(Arconix.pl().getApi().format().formatText("&6Preview"));
                lore.add(Arconix.pl().getApi().format().formatText("&7Crate"));
                lore.add(Arconix.pl().getApi().format().formatText("&7Claim"));
            } else if (kitBlockData.getType() == KitType.CRATE) {
                lore.add(Arconix.pl().getApi().format().formatText("&7Preview"));
                lore.add(Arconix.pl().getApi().format().formatText("&6Crate"));
                lore.add(Arconix.pl().getApi().format().formatText("&7Claim"));
            } else if (kitBlockData.getType() == KitType.CLAIM) {
                lore.add(Arconix.pl().getApi().format().formatText("&7Preview"));
                lore.add(Arconix.pl().getApi().format().formatText("&7Crate"));
                lore.add(Arconix.pl().getApi().format().formatText("&6Claim"));
            }
            allmeta.setLore(lore);
            alli.setItemMeta(allmeta);

            i.setItem(11, alli);

            alli = new ItemStack(Material.POPPY);
            allmeta = alli.getItemMeta();
            allmeta.setDisplayName(Arconix.pl().getApi().format().formatText("&9&lDecor Options"));
            lore = new ArrayList<>();
            lore.add(Arconix.pl().getApi().format().formatText("&7Click to edit the decoration"));
            lore.add(Arconix.pl().getApi().format().formatText("&7options for this kit."));
            allmeta.setLore(lore);
            alli.setItemMeta(allmeta);

            i.setItem(13, alli);

            alli = new ItemStack(Material.DIAMOND_PICKAXE);
            allmeta = alli.getItemMeta();
            allmeta.setDisplayName(Arconix.pl().getApi().format().formatText("&a&lEdit kit"));
            lore = new ArrayList<>();
            lore.add(Arconix.pl().getApi().format().formatText("&7Click to edit the kit"));
            lore.add(Arconix.pl().getApi().format().formatText("&7contained in this block."));
            allmeta.setLore(lore);
            alli.setItemMeta(allmeta);

            i.setItem(15, alli);

            player.openInventory(i);
            playerData.setEditorType(BlockEditorPlayerData.EditorType.OVERVIEW);
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public void decor(Player player) {
        try {
            BlockEditorPlayerData playerData = getDataFor(player);

            KitBlockData kitBlockData = playerData.getKitBlockData();
            Kit kit = kitBlockData.getKit();

            Inventory i = Bukkit.createInventory(null, 27, Arconix.pl().getApi().format().formatText("&8Editing decor for &a" + Arconix.pl().getApi().format().formatTitle(kit.getShowableName()) + "&8."));

            Methods.fillGlass(i);

            i.setItem(0, Methods.getBackgroundGlass(true));
            i.setItem(1, Methods.getBackgroundGlass(true));
            i.setItem(2, Methods.getBackgroundGlass(false));
            i.setItem(6, Methods.getBackgroundGlass(false));
            i.setItem(7, Methods.getBackgroundGlass(true));
            i.setItem(8, Methods.getBackgroundGlass(true));
            i.setItem(9, Methods.getBackgroundGlass(true));
            i.setItem(10, Methods.getBackgroundGlass(false));
            i.setItem(16, Methods.getBackgroundGlass(false));
            i.setItem(17, Methods.getBackgroundGlass(true));
            i.setItem(18, Methods.getBackgroundGlass(true));
            i.setItem(19, Methods.getBackgroundGlass(true));
            i.setItem(20, Methods.getBackgroundGlass(false));
            i.setItem(24, Methods.getBackgroundGlass(false));
            i.setItem(25, Methods.getBackgroundGlass(true));
            i.setItem(26, Methods.getBackgroundGlass(true));

            ItemStack exit = new ItemStack(Material.valueOf(UltimateKits.getInstance().getConfig().getString("Interfaces.Exit Icon")), 1);
            ItemMeta exitmeta = exit.getItemMeta();
            exitmeta.setDisplayName(Lang.EXIT.getConfigValue());
            exit.setItemMeta(exitmeta);


            ItemStack head2 = new ItemStack(Material.PLAYER_HEAD, 1, (byte) 3);
            ItemStack back = Arconix.pl().getApi().getGUI().addTexture(head2, "http://textures.minecraft.net/texture/3ebf907494a935e955bfcadab81beafb90fb9be49c7026ba97d798d5f1a23");
            SkullMeta skull2Meta = (SkullMeta) back.getItemMeta();
            back.setDurability((short) 3);
            skull2Meta.setDisplayName(Lang.BACK.getConfigValue());
            back.setItemMeta(skull2Meta);

            i.setItem(0, back);
            i.setItem(8, exit);

            ItemStack alli = new ItemStack(Material.SIGN);
            ItemMeta allmeta = alli.getItemMeta();
            allmeta.setDisplayName(Arconix.pl().getApi().format().formatText("&9&lToggle Holograms"));
            ArrayList<String> lore = new ArrayList<>();
            if (kitBlockData.showHologram()) {
                lore.add(Arconix.pl().getApi().format().formatText("&7Currently: &aEnabled&7."));
            } else {
                lore.add(Arconix.pl().getApi().format().formatText("&7Currently &cDisabled&7."));
            }
            allmeta.setLore(lore);
            alli.setItemMeta(allmeta);

            i.setItem(10, alli);

            alli = new ItemStack(Material.POTION);
            allmeta = alli.getItemMeta();
            allmeta.setDisplayName(Arconix.pl().getApi().format().formatText("&9&lToggle Particles"));
            lore = new ArrayList<>();
            if (kitBlockData.hasParticles()) {
                lore.add(Arconix.pl().getApi().format().formatText("&7Currently: &aEnabled&7."));
            } else {
                lore.add(Arconix.pl().getApi().format().formatText("&7Currently &cDisabled&7."));
            }
            allmeta.setLore(lore);
            alli.setItemMeta(allmeta);

            i.setItem(12, alli);

            alli = new ItemStack(Material.GRASS);
            allmeta = alli.getItemMeta();
            allmeta.setDisplayName(Arconix.pl().getApi().format().formatText("&9&lToggle DisplayItems"));
            lore = new ArrayList<>();
            if (kitBlockData.isDisplayingItems()) {
                lore.add(Arconix.pl().getApi().format().formatText("&7Currently: &aEnabled&7."));
            } else {
                lore.add(Arconix.pl().getApi().format().formatText("&7Currently &cDisabled&7."));
            }
            allmeta.setLore(lore);
            alli.setItemMeta(allmeta);

            i.setItem(14, alli);

            alli = new ItemStack(Material.BEACON);
            if (kit.getDisplayItem() != null) {
                alli.setType(kit.getDisplayItem());
            }
            allmeta = alli.getItemMeta();
            allmeta.setDisplayName(Arconix.pl().getApi().format().formatText("&9&lToggle DisplayItem Override"));
            lore = new ArrayList<>();
            if (kitBlockData.isItemOverride()) {
                lore.add(Arconix.pl().getApi().format().formatText("&7Currently: &aEnabled&7."));
            } else {
                lore.add(Arconix.pl().getApi().format().formatText("&7Currently &cDisabled&7."));
            }
            lore.add("");
            lore.add(Arconix.pl().getApi().format().formatText("&7Enabling this option will "));
            lore.add(Arconix.pl().getApi().format().formatText("&7override the DisplayItems"));
            lore.add(Arconix.pl().getApi().format().formatText("&7above your kit to the single"));
            lore.add(Arconix.pl().getApi().format().formatText("&7DisplayItem set in this kit"));
            lore.add(Arconix.pl().getApi().format().formatText("&7GUI options."));
            allmeta.setLore(lore);
            alli.setItemMeta(allmeta);

            i.setItem(16, alli);

            player.openInventory(i);
            playerData.setEditorType(BlockEditorPlayerData.EditorType.DECOR);
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }


    public void toggleHologram(Player player) {
        try {
            KitBlockData kitBlockData = getDataFor(player).getKitBlockData();
            if (kitBlockData.showHologram()) {
                kitBlockData.setShowHologram(false);
            } else {
                kitBlockData.setShowHologram(true);
            }
            UltimateKits.getInstance().holo.updateHolograms();
            decor(player);
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public void toggleParticles(Player player) {
        try {
            KitBlockData kitBlockData = getDataFor(player).getKitBlockData();
            if (kitBlockData.hasParticles()) {
                kitBlockData.setHasParticles(false);
            } else {
                kitBlockData.setHasParticles(true);
            }
            decor(player);
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public void toggleDisplayItems(Player player) {
        try {
            KitBlockData kitBlockData = getDataFor(player).getKitBlockData();

            boolean isHolo = kitBlockData.showHologram();

            if (isHolo) {
                kitBlockData.setShowHologram(false);
                instance.holo.updateHolograms();
            }

            if (kitBlockData.isDisplayingItems()) {
                kitBlockData.setDisplayingItems(false);
            } else {
                kitBlockData.setDisplayingItems(true);
            }
            decor(player);
            if (isHolo) {
                kitBlockData.setShowHologram(true);
                UltimateKits.getInstance().holo.updateHolograms();
            }
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }


    public void toggleItemOverride(Player player) {
        try {
            KitBlockData kitBlockData = getDataFor(player).getKitBlockData();

            if (kitBlockData.isItemOverride()) {
                kitBlockData.setItemOverride(false);
            } else {
                kitBlockData.setItemOverride(true);
            }
            decor(player);
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }


    public void changeDisplayType(Player player) {
        try {
            UltimateKits instance = UltimateKits.getInstance();
            BlockEditorPlayerData playerData = getDataFor(player);
            KitBlockData kitBlockData = playerData.getKitBlockData();

            if (kitBlockData.getType() == KitType.PREVIEW) kitBlockData.setType(KitType.CRATE);
            else if (kitBlockData.getType() == KitType.CRATE) kitBlockData.setType(KitType.CLAIM);
            else if (kitBlockData.getType() == KitType.CLAIM)  kitBlockData.setType(KitType.PREVIEW);

            instance.saveConfig();
            instance.holo.updateHolograms();
            openOverview(player, playerData.getLocation());
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }


    public BlockEditorPlayerData getDataFor(Player player) {
        return blockPlayerData.computeIfAbsent(player.getUniqueId(), uuid -> new BlockEditorPlayerData());
    }

}
