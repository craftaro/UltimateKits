package com.songoda.ultimatekits.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.core.gui.GuiManager;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.gui.BlockEditorGui;
import com.songoda.ultimatekits.gui.KitEditorGui;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.kit.KitBlockData;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandEdit extends AbstractCommand {

    final UltimateKits instance = UltimateKits.getInstance();
    final GuiManager guiManager;

    public CommandEdit(GuiManager guiManager) {
        super(true, "edit");
        this.guiManager = guiManager;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (args.length > 1) return ReturnType.SYNTAX_ERROR;
        final Player player = (Player) sender;

        if (args.length == 0) {
            Block block = player.getTargetBlock(null, 200);
            KitBlockData kitBlockData = instance.getKitManager().getKit(block.getLocation());
            if (kitBlockData == null) {
                instance.getLocale().newMessage("command.kit.nokitatblock").sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }
            guiManager.showGUI(player, new BlockEditorGui(instance, kitBlockData));
        } else {
            String kitStr = args[0].toLowerCase().trim();
            if (instance.getKitManager().getKit(kitStr) == null) {
                instance.getLocale().getMessage("command.kit.kitdoesntexist").sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }

            guiManager.showGUI(player, new KitEditorGui(instance, player, instance.getKitManager().getKit(kitStr), null));
        }
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        List<String> tab = new ArrayList<>();
        if (args.length == 1) {
            for (Kit kit : UltimateKits.getInstance().getKitManager().getKits())
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
        return "/KitAdmin edit <kit>";
    }

    @Override
    public String getDescription() {
        return "Edit a kit.";
    }
}
