package com.songoda.ultimatekits.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.core.gui.GuiManager;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.gui.CategoryEditorGui;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandCategories extends AbstractCommand {

    final UltimateKits plugin = UltimateKits.getInstance();
    final GuiManager guiManager;

    public CommandCategories(GuiManager guiManager) {
        super(CommandType.PLAYER_ONLY, "categories");
        this.guiManager = guiManager;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        guiManager.showGUI((Player) sender, new CategoryEditorGui(plugin, (Player) sender));
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "ultimatekits.admin";
    }

    @Override
    public String getSyntax() {
        return "categories";
    }

    @Override
    public String getDescription() {
        return "Open the category editor.";
    }
}
