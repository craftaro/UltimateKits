package com.craftaro.ultimatekits.commands;

import com.craftaro.core.commands.AbstractCommand;
import com.craftaro.core.gui.GuiManager;
import com.craftaro.ultimatekits.UltimateKits;
import com.craftaro.ultimatekits.gui.BlockEditorGui;
import com.craftaro.ultimatekits.gui.KitEditorGui;
import com.craftaro.ultimatekits.kit.Kit;
import com.craftaro.ultimatekits.kit.KitBlockData;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandEdit extends AbstractCommand {

    private final UltimateKits plugin;
    private final GuiManager guiManager;

    public CommandEdit(UltimateKits plugin, GuiManager guiManager) {
        super(CommandType.PLAYER_ONLY, "edit");
        this.plugin = plugin;
        this.guiManager = guiManager;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (args.length > 1) return ReturnType.SYNTAX_ERROR;
        final Player player = (Player) sender;

        if (args.length == 0) {
            Block block = player.getTargetBlock(null, 200);
            KitBlockData kitBlockData = plugin.getKitManager().getKit(block.getLocation());
            if (kitBlockData == null) {
                plugin.getLocale().newMessage("command.kit.nokitatblock").sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }
            guiManager.showGUI(player, new BlockEditorGui(plugin, kitBlockData));
        } else {
            String kitStr = args[0].toLowerCase().trim();
            if (plugin.getKitManager().getKit(kitStr) == null) {
                plugin.getLocale().getMessage("command.kit.kitdoesntexist").sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }

            guiManager.showGUI(player, new KitEditorGui(plugin, player, plugin.getKitManager().getKit(kitStr), null));
        }
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        List<String> tab = new ArrayList<>();
        if (args.length == 1) {
            for (Kit kit : plugin.getKitManager().getKits())
                tab.add(kit.getKey());
            return tab;
        }
        return tab;
    }

    @Override
    public String getPermissionNode() {
        return "ultimatekits.admin";
    }

    @Override
    public String getSyntax() {
        return "edit <kit>";
    }

    @Override
    public String getDescription() {
        return "Edit a kit.";
    }
}
