package com.songoda.ultimatekits.command.commands;

import com.songoda.arconix.api.methods.formatting.TextComponent;
import com.songoda.arconix.plugin.Arconix;
import com.songoda.ultimatekits.Lang;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.command.AbstractCommand;
import com.songoda.ultimatekits.utils.Methods;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandEdit extends AbstractCommand {

    public CommandEdit(AbstractCommand parent) {
        super("edit", parent, true, false);
    }

    @Override
    protected ReturnType runCommand(UltimateKits instance, CommandSender sender, String... args) {
        Player player = (Player) sender;
        Block block = player.getTargetBlock(null, 200);
        String loc = Arconix.pl().getApi().serialize().serializeLocation(block);
        if (args.length > 2) return ReturnType.SYNTAX_ERROR;

        if (args.length == 1) {
            if (instance.getConfig().getString("data.block." + loc) == null) {
                player.sendMessage(Arconix.pl().getApi().format().formatText(instance.references.getPrefix() + "&8This block does not contain a kit."));
                return  ReturnType.FAILURE;
            }
            instance.getBlockEditor().openOverview(player, block.getLocation());
        } else {
            String kitStr = args[1].toLowerCase().trim();
            if (!Methods.doesKitExist(kitStr)) {
                player.sendMessage(instance.references.getPrefix() + Lang.KIT_DOESNT_EXIST.getConfigValue(kitStr));
                return ReturnType.FAILURE;
            }

            instance.getKitEditor().openOverview(instance.getKitManager().getKit(kitStr), player, false, null);
        }
        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "ultimatekits.admin";
    }

    @Override
    public String getSyntax() {
        return "/uk edit";
    }

    @Override
    public String getDescription() {
        return "Edit a kit.";
    }
}
