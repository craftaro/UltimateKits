package com.songoda.ultimatekits.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.core.gui.GuiManager;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.gui.KitEditorGui;
import com.songoda.ultimatekits.kit.Kit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class CommandCreatekit extends AbstractCommand {

    final UltimateKits instance = UltimateKits.getInstance();
    final GuiManager guiManager;

    public CommandCreatekit(GuiManager guiManager) {
        super(true, "createkit");
        this.guiManager = guiManager;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;
        if (args.length != 1) {
            return ReturnType.SYNTAX_ERROR;
        }
        String kitStr = args[0].toLowerCase();
        if (instance.getKitManager().getKit(kitStr) != null) {
            instance.getLocale().getMessage("command.kit.kitalreadyexists").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        instance.getLocale().newMessage("&aThat kit doesn't exist. Creating it now.").sendPrefixedMessage(player);
        Kit kit = new Kit(kitStr.trim());
        UltimateKits.getInstance().getKitManager().addKit(kit);
        new KitEditorGui(instance, player, kit, null, null, 0);
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return Arrays.asList("name");
    }

    @Override
    public String getPermissionNode() {
        return "ultimatekits.admin";
    }

    @Override
    public String getSyntax() {
        return "/KitAdmin createkit <name>";
    }

    @Override
    public String getDescription() {
        return "Create a kit in a GUI.";
    }
}
