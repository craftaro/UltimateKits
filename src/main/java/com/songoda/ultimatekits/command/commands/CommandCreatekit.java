package com.songoda.ultimatekits.command.commands;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.ultimatekits.Lang;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.command.AbstractCommand;
import com.songoda.ultimatekits.kit.Kit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCreatekit extends AbstractCommand {

    public CommandCreatekit(AbstractCommand parent) {
        super("createkit", parent, true, false);
    }

    @Override
    protected ReturnType runCommand(UltimateKits instance, CommandSender sender, String... args) {
        Player player = (Player) sender;
        if (args.length != 2) return ReturnType.SYNTAX_ERROR;
        String kitStr = args[1].toLowerCase();
        if (instance.getKitManager().getKit(kitStr) != null) {
            player.sendMessage(instance.getReferences().getPrefix() + Lang.KIT_ALREADY_EXISTS.getConfigValue(kitStr));
            return ReturnType.FAILURE;
        }

        player.sendMessage(UltimateKits.getInstance().getReferences().getPrefix() + Arconix.pl().getApi().format().formatText("&aThat kit doesn't exist. Creating it now."));
        Kit kit = new Kit(kitStr.trim());
        UltimateKits.getInstance().getKitManager().addKit(kit);
        instance.getKitEditor().openOverview(kit, player, false, null, 0);
        return ReturnType.SUCCESS;
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
