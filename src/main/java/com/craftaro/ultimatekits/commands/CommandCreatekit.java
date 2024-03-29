package com.craftaro.ultimatekits.commands;

import com.craftaro.core.commands.AbstractCommand;
import com.craftaro.core.gui.GuiManager;
import com.craftaro.ultimatekits.UltimateKits;
import com.craftaro.ultimatekits.gui.KitEditorGui;
import com.craftaro.ultimatekits.kit.Kit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandCreatekit extends AbstractCommand {
    private final UltimateKits plugin;
    private final GuiManager guiManager;

    public CommandCreatekit(UltimateKits plugin, GuiManager guiManager) {
        super(CommandType.PLAYER_ONLY, "createkit");
        this.plugin = plugin;
        this.guiManager = guiManager;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;
        if (args.length != 1) {
            return ReturnType.SYNTAX_ERROR;
        }
        String kitStr = args[0].toLowerCase().trim();
        if (this.plugin.getKitManager().getKit(kitStr) != null) {
            this.plugin.getLocale().getMessage("command.kit.kitalreadyexists").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        this.plugin.getLocale().newMessage("&aThat kit doesn't exist. Creating it now.").sendPrefixedMessage(player);
        Kit kit = new Kit(kitStr);
        this.plugin.getKitManager().addKit(kit);
        this.guiManager.showGUI(player, new KitEditorGui(this.plugin, player, kit, null));
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return Collections.singletonList("name");
    }

    @Override
    public String getPermissionNode() {
        return "ultimatekits.admin";
    }

    @Override
    public String getSyntax() {
        return "createkit <name>";
    }

    @Override
    public String getDescription() {
        return "Create a kit in a GUI.";
    }
}
