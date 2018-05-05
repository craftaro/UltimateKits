package com.songoda.ultimatekits.events;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.ultimatekits.Lang;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.KitEditor;
import com.songoda.ultimatekits.kit.object.Kit;
import com.songoda.ultimatekits.kit.object.KitEditorPlayerData;
import com.songoda.ultimatekits.utils.Debugger;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

/**
 * Created by songoda on 2/24/2017.
 */
public class ChatListeners implements Listener {

    private final UltimateKits instance;

    public ChatListeners(UltimateKits instance) {
        this.instance = instance;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent e) {
        try {
            final Player p = e.getPlayer();

            KitEditorPlayerData playerData = instance.getKitEditor().getDataFor(p);

            if (playerData.getEditorType() == KitEditorPlayerData.EditorType.NOTIN) return;

            KitEditor edit = instance.getKitEditor();
            String msg = e.getMessage().trim();
            Kit kit = playerData.getKit();
            e.setCancelled(true);

            switch (playerData.getEditorType()) {
                case PRICE:
                    if (instance.getServer().getPluginManager().getPlugin("Vault") == null) {
                        p.sendMessage(instance.references.getPrefix() + Arconix.pl().getApi().format().formatText("&8You must have &aVault &8installed to utilize economy.."));
                    } else if (!Arconix.pl().getApi().doMath().isNumeric(msg)) {
                        p.sendMessage(Arconix.pl().getApi().format().formatText("&a" + msg + " &8is not a number. Please do not include a &a$&8."));
                    } else {

                        if (kit.getLink() != null) {
                            kit.setLink(null);
                            p.sendMessage(Arconix.pl().getApi().format().formatText(instance.references.getPrefix() + "&8LINK has been removed from this kit. Note you cannot have ECO & LINK set at the same time.."));
                        }
                        Double eco = Double.parseDouble(msg);
                        kit.setPrice(eco);
                        instance.holo.updateHolograms();
                    }
                    playerData.setEditorType(KitEditorPlayerData.EditorType.NOTIN);
                    edit.selling(p);
                    break;
                case DELAY:
                    if (!Arconix.pl().getApi().doMath().isNumeric(msg)) {
                        p.sendMessage(Arconix.pl().getApi().format().formatText("&a" + msg + " &8is not a number. Please do not include a &a$&8."));
                    } else {
                        kit.setDelay(Integer.parseInt(msg));
                    }
                    playerData.setEditorType(KitEditorPlayerData.EditorType.NOTIN);
                    edit.general(p);
                    break;
                case LINK:
                    if (kit.getPrice() != 0) {
                        kit.setPrice(0);
                        p.sendMessage(Arconix.pl().getApi().format().formatText(instance.references.getPrefix() + "&8ECO has been removed from this kit. Note you cannot have ECO & LINK set at the same time.."));
                    }
                    kit.setLink(msg);
                    instance.holo.updateHolograms();
                    playerData.setEditorType(KitEditorPlayerData.EditorType.NOTIN);
                    edit.selling(p);
                    break;
                case TITLE:
                    kit.setTitle(msg);
                    instance.saveConfig();
                    instance.holo.updateHolograms();
                    p.sendMessage(Arconix.pl().getApi().format().formatText(instance.references.getPrefix() + "&8Title &5" + msg + "&8 added to Kit &a" + kit.getShowableName() + "&8."));
                    playerData.setEditorType(KitEditorPlayerData.EditorType.NOTIN);
                    edit.gui(p);
                    break;
                case COMMAND:
                    ItemStack parseStack = new ItemStack(Material.PAPER, 1);
                    ItemMeta meta = parseStack.getItemMeta();

                    ArrayList<String> lore = new ArrayList<>();

                    int index = 0;
                    while (index < msg.length()) {
                        lore.add("§a/" + msg.substring(index, Math.min(index + 30, msg.length())));
                        index += 30;
                    }
                    meta.setLore(lore);
                    meta.setDisplayName(Lang.COMMAND.getConfigValue());
                    parseStack.setItemMeta(meta);

                    p.sendMessage(Arconix.pl().getApi().format().formatText(instance.references.getPrefix() + "&8Command &5" + msg + "&8 has been added to your kit."));
                    playerData.setEditorType(KitEditorPlayerData.EditorType.NOTIN);
                    edit.openOverview(kit, p,false, parseStack);
                    break;
                case MONEY:
                    ItemStack parseStack2 = new ItemStack(Material.PAPER, 1);
                    ItemMeta meta2 = parseStack2.getItemMeta();

                    ArrayList<String> lore2 = new ArrayList<>();

                    int index2 = 0;
                    while (index2 < msg.length()) {
                        lore2.add("§a$" + msg.substring(index2, Math.min(index2 + 30, msg.length())));
                        index2 += 30;
                    }
                    meta2.setLore(lore2);
                    meta2.setDisplayName(Lang.MONEY.getConfigValue());
                    parseStack2.setItemMeta(meta2);

                    p.sendMessage(Arconix.pl().getApi().format().formatText(instance.references.getPrefix() + "&8Money &5$" + msg + "&8 has been added to your kit."));
                    playerData.setEditorType(KitEditorPlayerData.EditorType.NOTIN);
                    edit.openOverview(kit, p, false, parseStack2);
                    break;
                default:
                    e.setCancelled(false);
                    break;
            }
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    @EventHandler
    public void onCommandPreprocess(AsyncPlayerChatEvent event) {
        try {
            if (event.getMessage().equalsIgnoreCase("/kit") || event.getMessage().equalsIgnoreCase("/kit")) {
                event.setCancelled(true);
            }
        } catch (Exception e) {
            Debugger.runReport(e);
        }
    }
}