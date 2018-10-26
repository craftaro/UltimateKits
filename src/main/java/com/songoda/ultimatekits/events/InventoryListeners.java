package com.songoda.ultimatekits.events;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.ultimatekits.Lang;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.editor.BlockEditor;
import com.songoda.ultimatekits.editor.KitEditor;
import com.songoda.ultimatekits.kit.KitsGUI;
import com.songoda.ultimatekits.editor.BlockEditorPlayerData;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.editor.KitEditorPlayerData;
import com.songoda.ultimatekits.player.PlayerData;
import com.songoda.ultimatekits.utils.Debugger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;

public class InventoryListeners implements Listener {

    private final UltimateKits instance;

    public InventoryListeners(UltimateKits plugin) {
        this.instance = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        try {
            Player player = (Player) event.getWhoClicked();
            PlayerData playerData = instance.getPlayerDataManager().getPlayerAction(player);

            if (playerData.isInCrate()) {
                event.setCancelled(true);
            } else if (playerData.getGuiLocation() == PlayerData.GUILocation.BUY_FINAL) {
                if (event.getSlot() == 11) {
                    Kit kit = playerData.getInKit();
                    kit.buyWithEconomy(player);
                    player.closeInventory();
                } else if (event.getSlot() == 15) {
                    player.sendMessage(Arconix.pl().getApi().format().formatText(instance.getReferences().getPrefix() + Lang.BUYCANCELLED.getConfigValue()));
                    player.closeInventory();
                }
                event.setCancelled(true);
            } else if (playerData.getGuiLocation() == PlayerData.GUILocation.KITS) {
                event.setCancelled(true);
                if (instance.getReferences().isPlaySound())
                    player.playSound(event.getWhoClicked().getLocation(), instance.getReferences().getSound(), 10.0F, 1.0F);
                if (event.getAction() == InventoryAction.NOTHING
                        || event.getCurrentItem().getType() == Material.AIR
                        || event.getCurrentItem().getItemMeta().getDisplayName() == null) {
                    return;
                }

                ItemStack clicked = event.getCurrentItem();
                int page = playerData.getKitsPage();

                if (event.getClick() == ClickType.MIDDLE && player.hasPermission("ultimatekits.admin")) {
                    
                    playerData.setKitMode(!playerData.isKitsMode());
                    
                    KitsGUI.show(player, page);
                    return;
                }

                String kitName = clicked.getItemMeta().getDisplayName().replace(String.valueOf(ChatColor.COLOR_CHAR), "").split(":")[0];

                Kit kit = instance.getKitManager().getKit(kitName);

                if (playerData.isKitsMode()) {
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
            } else if (playerData.getGuiLocation() == PlayerData.GUILocation.DISPLAY) {
                event.setCancelled(true);
                if (instance.getReferences().isPlaySound()) {
                    ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), instance.getReferences().getSound(), 10.0F, 1.0F);
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
                    String kitName = instance.getPlayerDataManager().getPlayerAction(player).getInKit().getName();
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
                            instance.getKitEditor().openOverview(instance.getBlockEditor().getDataFor(player).getKit(), player, true, null, 0);
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
                    KitEditorPlayerData editorData = edit.getDataFor(player);
                    if (!(event.getRawSlot() > event.getView().getTopInventory().getSize() - 1)) {
                        if ((event.getSlot() > 9 && event.getSlot() < 44) && event.getSlot() != 17 && event.getSlot() != 36) {
                            if (event.getCurrentItem().getType() != Material.AIR) {
                                if (editorData.isInFuction()) {
                                    if (event.isShiftClick()) {
                                        edit.replaceItem(KitEditor.Action.CHANCE, player, event.getCurrentItem(), event.getSlot());
                                    } else if (event.isLeftClick()) {
                                        edit.replaceItem(KitEditor.Action.DISPLAY_ITEM, player, event.getCurrentItem(), event.getSlot());
                                    } else if (event.getClick() == ClickType.MIDDLE) {
                                        edit.replaceItem(KitEditor.Action.DISPLAY_NAME, player, event.getCurrentItem(), event.getSlot());
                                    } else if (event.isRightClick()) {
                                        edit.replaceItem(KitEditor.Action.DISPLAY_LORE, player, event.getCurrentItem(), event.getSlot());
                                    }
                                    event.setCancelled(true);
                                }
                            }
                        }
                    }
                    if ((event.getSlot() < 10 || event.getSlot() > 43) || event.getSlot() == 17 || event.getSlot() == 36) {
                        if (event.getInventory() != null && event.getInventory().getType() == InventoryType.CHEST) {
                            event.setCancelled(true);
                        }
                    }
                    if (event.getRawSlot() > event.getView().getTopInventory().getSize() - 1) {
                        if (editorData.isInInventory()) {
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
                                    edit.changeAnimation(player);
                                    break;
                            }
                        }
                    }

                    if (event.getSlot() == 50) {
                        if (!editorData.isInInventory()) {
                            player.getInventory().setContents(editorData.getInventory());
                            editorData.setInInventory(true);
                            player.updateInventory();
                        } else edit.getInvItems(player, editorData);
                        edit.updateInvButton(event.getInventory(), editorData);
                    }
                    if (event.getSlot() == 48) {
                        editorData.setInFunction(!editorData.isInFuction());
                        editorData.setMuteSave(true);
                        edit.openOverview(edit.getDataFor(player).getKit(), player, false, null, 0);
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
                            player.sendMessage(instance.getReferences().getPrefix() + Arconix.pl().getApi().format().formatText("&cKit destroyed successfully."));
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
                        edit.openOverview(edit.getDataFor(player).getKit(), player, false, null, 0);
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
            if (instance.getPlayerDataManager().getPlayerAction((Player)event.getWhoClicked()).getGuiLocation() != PlayerData.GUILocation.DISPLAY)
                return;
            event.setCancelled(true);
            if (instance.getReferences().isPlaySound())
                ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), instance.getReferences().getSound(), 10.0F, 1.0F);

        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    @EventHandler
    public void onInteract(InventoryInteractEvent event) {
        try {
            if (instance.getPlayerDataManager().getPlayerAction((Player)event.getWhoClicked()).getGuiLocation() != PlayerData.GUILocation.DISPLAY)
                return;
            event.setCancelled(true);
            if (instance.getReferences().isPlaySound())
                ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), instance.getReferences().getSound(), 10.0F, 1.0F);

        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        try {
            final Player player = (Player) event.getPlayer();

            PlayerData playerData = instance.getPlayerDataManager().getPlayerAction(player);

            KitEditorPlayerData kitPlayerData = instance.getKitEditor().getDataFor(player);

            playerData.setGuiLocation(PlayerData.GUILocation.NOT_IN);

            if (kitPlayerData.getEditorType() == KitEditorPlayerData.EditorType.OVERVIEW) {
                KitEditor edit = instance.getKitEditor();
                edit.saveKit(player, player.getOpenInventory().getTopInventory());
            }

            if (!kitPlayerData.isInInventory() && kitPlayerData.getInventory().length != 0) {
                player.getInventory().setContents(kitPlayerData.getInventory());
                kitPlayerData.setInInventory(true);
                player.updateInventory();
            }

            kitPlayerData.setEditorType(KitEditorPlayerData.EditorType.NOTIN);

            BlockEditorPlayerData blockPlayerData = instance.getBlockEditor().getDataFor(player);
            blockPlayerData.setEditorType(BlockEditorPlayerData.EditorType.NOTIN);

        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }
}