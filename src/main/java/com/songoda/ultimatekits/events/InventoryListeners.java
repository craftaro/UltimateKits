package com.songoda.ultimatekits.events;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.ultimatekits.Lang;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.BlockEditor;
import com.songoda.ultimatekits.kit.KitEditor;
import com.songoda.ultimatekits.kit.KitsGUI;
import com.songoda.ultimatekits.kit.object.BlockEditorPlayerData;
import com.songoda.ultimatekits.kit.object.Kit;
import com.songoda.ultimatekits.kit.object.KitEditorPlayerData;
import com.songoda.ultimatekits.utils.Debugger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("Duplicates")
public class InventoryListeners implements Listener {

    private final UltimateKits instance;

    public InventoryListeners(UltimateKits instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        try {
            Player player = (Player) event.getWhoClicked();
            if (instance.buy.containsKey(player.getUniqueId())) {
                if (event.getSlot() == 11) {
                    Kit kit = instance.getKitManager().getKit(instance.buy.get(player.getUniqueId()));
                    kit.buyWithEconomy(player);
                    player.closeInventory();
                    instance.buy.remove(player.getUniqueId());
                } else if (event.getSlot() == 15) {
                    player.sendMessage(Arconix.pl().getApi().format().formatText(instance.references.getPrefix() + Lang.BUYCANCELLED.getConfigValue()));
                    player.closeInventory();
                    instance.buy.remove(player.getUniqueId());
                }
                event.setCancelled(true);
            } else if (instance.whereAt.containsKey(event.getWhoClicked().getUniqueId()) && instance.whereAt.get(event.getWhoClicked().getUniqueId()).equals("kits")) {
                event.setCancelled(true);
                if (instance.references.isPlaySound())
                    player.playSound(event.getWhoClicked().getLocation(), instance.references.getSound(), 10.0F, 1.0F);
                if (event.getAction() == InventoryAction.NOTHING
                        || event.getCurrentItem().getType() == Material.AIR
                        || event.getCurrentItem().getItemMeta().getDisplayName() == null) {
                    return;
                }

                ItemStack clicked = event.getCurrentItem();
                int page = instance.page.get(player.getUniqueId());

                if (event.getClick() == ClickType.MIDDLE && player.hasPermission("ultimatekits.admin")) {
                    if (instance.kitsMode.contains(player.getUniqueId())) {
                        instance.kitsMode.remove(player.getUniqueId());
                    } else {
                        instance.kitsMode.add(player.getUniqueId());
                    }
                    KitsGUI.show(player, page);
                    return;
                }

                String kitName = instance.kits.get(clicked.getItemMeta().getDisplayName());

                Kit kit = instance.getKitManager().getKit(kitName);

                if (instance.kitsMode.contains(player.getUniqueId())) {
                    if (event.getClick() == ClickType.RIGHT) {
                        instance.getKitManager().moveKit(kit, true);
                    } else if (event.getClick() == ClickType.LEFT) {
                        instance.getKitManager().moveKit(kit, false);

                    }
                    KitsGUI.show(player, page);
                    return;
                }

                if (event.getSlot() == 4) return;

                if (ChatColor.stripColor(clicked.getItemMeta().getDisplayName()).equalsIgnoreCase(ChatColor.stripColor(Lang.NEXT.getConfigValue()))) {
                    KitsGUI.show(player, page + 1);
                } else if (ChatColor.stripColor(clicked.getItemMeta().getDisplayName()).equalsIgnoreCase(ChatColor.stripColor(Lang.LAST.getConfigValue()))) {
                    KitsGUI.show(player, page - 1);
                } else if (ChatColor.stripColor(clicked.getItemMeta().getDisplayName()).equalsIgnoreCase(ChatColor.stripColor(Lang.EXIT.getConfigValue()))) {
                    player.closeInventory();
                } else if (!ChatColor.stripColor(clicked.getItemMeta().getDisplayName()).equalsIgnoreCase("")) {
                    if (!clicked.getItemMeta().hasDisplayName()) return;

                    if (event.getClick().isLeftClick()) {
                        kit.display(player, true);
                        return;
                    }

                    if (event.getClick().isRightClick()) {
                        kit.buy(player);
                        KitsGUI.show(player, page);
                    }
                }
            } else if (instance.whereAt.containsKey(event.getWhoClicked().getUniqueId()) && instance.whereAt.get(event.getWhoClicked().getUniqueId()).equals("display")) {
                event.setCancelled(true);
                if (instance.references.isPlaySound()) {
                    ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), instance.references.getSound(), 10.0F, 1.0F);
                }
                if (event.getAction() == InventoryAction.NOTHING
                        || event.getCurrentItem().getType() == Material.AIR
                        || event.getCurrentItem().getItemMeta().getDisplayName() == null) {
                    return;
                }
                ItemStack clicked = event.getCurrentItem();
                if (ChatColor.stripColor(clicked.getItemMeta().getDisplayName()).equalsIgnoreCase(ChatColor.stripColor(Lang.BACK.getConfigValue()))) {
                    KitsGUI.show(player, 1);
                }
                if (ChatColor.stripColor(clicked.getItemMeta().getDisplayName()).equalsIgnoreCase(ChatColor.stripColor(Lang.EXIT.getConfigValue()))) {
                    player.closeInventory();
                }
                if (ChatColor.stripColor(clicked.getItemMeta().getDisplayName()).equalsIgnoreCase(ChatColor.stripColor(Lang.BUYNOW.getConfigValue()))) {
                    player.closeInventory();
                    String kitName = instance.inKit.get(player.getUniqueId()).getName();
                    Kit kit = instance.getKitManager().getKit(kitName);
                    kit.buy(player);
                }
            } else if (instance.getBlockEditor().getDataFor(player).getEditorType() != BlockEditorPlayerData.EditorType.NOTIN) {
                BlockEditor edit = instance.getBlockEditor();
                if (instance.getBlockEditor().getDataFor(player).getEditorType() == BlockEditorPlayerData.EditorType.OVERVIEW) {
                    event.setCancelled(true);

                    if (event.getCurrentItem().getItemMeta().getDisplayName() == null) return;
                    ItemStack clicked = event.getCurrentItem();
                    if (ChatColor.stripColor(clicked.getItemMeta().getDisplayName()).equalsIgnoreCase(ChatColor.stripColor(Lang.EXIT.getConfigValue())))
                        player.closeInventory();
                    if ((event.getSlot() > 44 || event.getSlot() < 9) && event.getInventory().getType() == InventoryType.CHEST) {
                        event.setCancelled(true);
                    }
                    switch (event.getSlot()) {
                        case 11:
                            edit.changeDisplayType(player);
                            break;
                        case 13:
                            edit.decor(player);
                            break;
                        case 15:
                            instance.getKitEditor().openOverview(instance.getBlockEditor().getDataFor(player).getKit(), player, true, null);
                            break;
                    }
                } else if (instance.getBlockEditor().getDataFor(player).getEditorType() == BlockEditorPlayerData.EditorType.DECOR) {
                    event.setCancelled(true);
                    switch (event.getSlot()) {
                        case 10:
                            edit.toggleHologram(player);
                            break;
                        case 12:
                            edit.toggleParticles(player);
                            break;
                        case 14:
                            edit.toggleDisplayItems(player);
                            break;
                        case 16:
                            edit.toggleItemOverride(player);
                            break;
                    }
                    if (event.getCurrentItem().getItemMeta() == null || !event.getCurrentItem().getItemMeta().hasDisplayName())
                        return;
                    if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase(ChatColor.stripColor(Lang.EXIT.getConfigValue()))) {
                        player.closeInventory();
                    }
                    if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase(ChatColor.stripColor(Lang.BACK.getConfigValue()))) {
                        if (event.getInventory().getTitle().contains("Editing decor for")) {
                            instance.getBlockEditor().openOverview(player, instance.getBlockEditor().getDataFor(player).getLocation());
                        }
                    }
                }
            } else if (instance.getKitEditor().getDataFor(player).getEditorType() != KitEditorPlayerData.EditorType.NOTIN) {
                KitEditor edit = instance.getKitEditor();
                if (instance.getKitEditor().getDataFor(player).getEditorType() == KitEditorPlayerData.EditorType.OVERVIEW) {
                    KitEditorPlayerData playerData = edit.getDataFor(player);
                    if ((event.getSlot() < 10 || event.getSlot() > 43) || event.getSlot() == 17 || event.getSlot() == 36) {
                        if (event.getInventory() != null && event.getInventory().getType() == InventoryType.CHEST) {
                            event.setCancelled(true);
                        }
                    }
                        Bukkit.broadcastMessage(event.getRawSlot() + ":" + event.getView().getTopInventory().getSize());
                        Bukkit.broadcastMessage((event.getRawSlot() > event.getView().getTopInventory().getSize()) + "");
                        Bukkit.broadcastMessage(playerData.isInInventory() + "");
                        if (event.getRawSlot() > event.getView().getTopInventory().getSize()
                                && playerData.isInInventory()) {
                            event.setCancelled(false);
                        } else {
                            switch (event.getSlot()) {
                                case 9:
                                    edit.general(player);
                                    break;
                                case 10:
                                    edit.selling(player);
                                    break;
                                case 12:
                                    edit.gui(player);
                                    break;
                                case 13:
                                    edit.createCommand(player);
                                    break;
                                case 14:
                                    edit.createMoney(player);
                                    break;
                                case 17:
                                    edit.saveKit(player, player.getOpenInventory().getTopInventory());
                                    break;
                            }
                        }

                    if (event.getSlot() == 49) {
                        if (!playerData.isInInventory()) {
                            player.getInventory().setContents(playerData.getInventory());
                            playerData.setInInventory(true);
                            player.updateInventory();
                        } else edit.getInvItems(player, playerData);
                        edit.updateInvButton(event.getInventory(), playerData);
                    }
                } else if (instance.getKitEditor().getDataFor(player).getEditorType() == KitEditorPlayerData.EditorType.SELLING) {
                    event.setCancelled(true);
                    switch (event.getSlot()) {
                        case 11:
                            edit.setNoSale(player);
                            break;
                        case 13:
                            edit.editLink(player);
                            break;
                        case 15:
                            edit.editPrice(player);
                            break;
                    }
                } else if (instance.getKitEditor().getDataFor(player).getEditorType() == KitEditorPlayerData.EditorType.GUI) {
                    event.setCancelled(true);
                    switch (event.getSlot()) {
                        case 11:
                            if (event.getClick() == ClickType.RIGHT)
                                edit.setTitle(player, false);
                            else if (event.getClick() == ClickType.LEFT)
                                edit.setTitle(player, true);
                            break;
                        case 13:
                            if (event.getClick() == ClickType.LEFT)
                                edit.setKitsDisplayItem(player, true);
                            else if (event.getClick() == ClickType.RIGHT)
                                edit.setKitsDisplayItem(player, false);
                            break;
                        case 15:
                            edit.hide(player);
                            break;
                    }
                } else if (instance.getKitEditor().getDataFor(player).getEditorType() == KitEditorPlayerData.EditorType.GENERAL) {
                    event.setCancelled(true);
                    switch (event.getSlot()) {
                        case 13:
                            edit.setDelay(player);
                            break;
                        case 15:
                            instance.getKitManager().removeKit(edit.getDataFor(player).getKit());
                            player.sendMessage(instance.references.getPrefix() + Arconix.pl().getApi().format().formatText("&cKit destroyed successfully."));
                            player.closeInventory();
                            break;
                    }
                }
                if (event.getCurrentItem() == null) return;
                if (event.getCurrentItem().getItemMeta() == null || !event.getCurrentItem().getItemMeta().hasDisplayName())
                    return;
                if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase(ChatColor.stripColor(Lang.EXIT.getConfigValue()))) {
                    player.closeInventory();
                }
                if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase(ChatColor.stripColor(Lang.BACK.getConfigValue()))) {
                    if (event.getInventory().getTitle().contains("You are editing kit")) {
                        instance.getBlockEditor().openOverview(player, instance.getBlockEditor().getDataFor(player).getLocation());
                    } else {
                        edit.openOverview(edit.getDataFor(player).getKit(), player, false, null);
                    }
                }
            }


        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        try {
            if (!event.getInventory().getTitle().startsWith("Previewing")
                    || !(instance.whereAt.containsKey(event.getWhoClicked().getUniqueId()) && instance.whereAt.get(event.getWhoClicked().getUniqueId()).equals("display")))
                return;
            event.setCancelled(true);
            if (instance.references.isPlaySound())
                ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), instance.references.getSound(), 10.0F, 1.0F);

        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    @EventHandler
    public void onInteract(InventoryInteractEvent event) {
        try {
            if (!event.getInventory().getTitle().startsWith("Previewing")
                    || !(instance.whereAt.containsKey(event.getWhoClicked().getUniqueId()) && instance.whereAt.get(event.getWhoClicked().getUniqueId()).equals("display")))
                return;
            event.setCancelled(true);
            if (instance.references.isPlaySound())
                ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), instance.references.getSound(), 10.0F, 1.0F);

        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        try {
            final Player p = (Player) event.getPlayer();

            KitEditorPlayerData kitPlayerData = instance.getKitEditor().getDataFor(p);

            if (!kitPlayerData.isInInventory() && kitPlayerData.getInventory().length != 0) {
                p.getInventory().setContents(kitPlayerData.getInventory());
                kitPlayerData.setInInventory(true);
                p.updateInventory();
            }

            kitPlayerData.setEditorType(KitEditorPlayerData.EditorType.NOTIN);

            BlockEditorPlayerData blockPlayerData = instance.getBlockEditor().getDataFor(p);
            blockPlayerData.setEditorType(BlockEditorPlayerData.EditorType.NOTIN);

            instance.buy.remove(p.getUniqueId());

            if (!instance.whereAt.containsKey(p.getUniqueId())) {
                return;
            }

            instance.whereAt.remove(p.getUniqueId());

            Bukkit.getScheduler().runTaskLater(instance, () -> {
                if (!p.getOpenInventory().getTopInventory().getType().equals(InventoryType.CHEST))
                    p.closeInventory();
            }, 1L);
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }
}