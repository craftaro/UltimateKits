package com.songoda.ultimatekits.command.commands;

import com.songoda.ultimatekits.Lang;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.command.AbstractCommand;
import com.songoda.ultimatekits.kit.object.Kit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandPreviewKit extends AbstractCommand {

    public CommandPreviewKit() {
        super("PreviewKit", null, true, false);
    }

    @Override
    protected ReturnType runCommand(UltimateKits instance, CommandSender sender, String... args) {
        Player player = (Player) sender;
        if (args.length != 1) {
            player.sendMessage(instance.references.getPrefix() + Lang.PREVIEW_NO_KIT_SUPPLIED.getConfigValue());
            return ReturnType.FAILURE;
        }
        Kit kit = instance.getKitManager().getKit(args[0].toLowerCase().trim());
        if (kit == null) {
            player.sendMessage(instance.references.getPrefix() + Lang.KIT_DOESNT_EXIST.getConfigValue());
            return ReturnType.FAILURE;
        }
        kit.display(player, false);
        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return null;
    }

    @Override
    public String getSyntax() {
        return "/PreviewKit <kit>";
    }

    @Override
    public String getDescription() {
        return "Preview a kit.";
    }
}
