package com.songoda.ultimatekits.command.commands;

import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.command.AbstractCommand;
import com.songoda.ultimatekits.gui.GUIKitEditor;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.utils.Methods;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandCreatekit extends AbstractCommand {

    public CommandCreatekit(AbstractCommand parent) {
        super(parent, true, "createkit");
    }

    @Override
    protected ReturnType runCommand(UltimateKits instance, CommandSender sender, String... args) {
        Player player = (Player) sender;
        if (args.length != 2) return ReturnType.SYNTAX_ERROR;
        String kitStr = args[1].toLowerCase();
        if (instance.getKitManager().getKit(kitStr) != null) {
            instance.getLocale().getMessage("command.kit.kitalreadyexists").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        instance.getLocale().newMessage("&aThat kit doesn't exist. Creating it now.").sendPrefixedMessage(player);
        Kit kit = new Kit(kitStr.trim());
        UltimateKits.getInstance().getKitManager().addKit(kit);
        new GUIKitEditor(instance, player, kit, null, null, 0);
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(UltimateKits instance, CommandSender sender, String... args) {
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
