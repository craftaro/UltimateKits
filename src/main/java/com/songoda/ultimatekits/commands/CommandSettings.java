package com.songoda.ultimatekits.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.core.configuration.editor.PluginConfigGui;
import com.songoda.core.gui.GuiManager;
import com.songoda.ultimatekits.UltimateKits;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandSettings extends AbstractCommand {

    final UltimateKits instance = UltimateKits.getInstance();
    final GuiManager guiManager;

    public CommandSettings(GuiManager guiManager) {
        super(true, "settings");
        this.guiManager = guiManager;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        guiManager.showGUI((Player) sender, new PluginConfigGui(instance));
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
        return "/KitAdmin settings";
    }

    @Override
    public String getDescription() {
        return "Edit the UltimateKits Settings.";
    }
}
