package com.songoda.ultimatekits.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.Kit;
import com.songoda.ultimatekits.kit.KitBlockData;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandSet extends AbstractCommand {

    private final UltimateKits plugin;

    public CommandSet(UltimateKits plugin) {
        super(CommandType.PLAYER_ONLY, "set");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (args.length != 1) {
            plugin.getLocale().getMessage("command.kit.nokitsupplied").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }
        Player player = (Player) sender;
        Kit kit = plugin.getKitManager().getKit(args[0].toLowerCase());
        if (kit == null) {
            plugin.getLocale().getMessage("command.kit.kitdoesntexist").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }
        Block b = player.getTargetBlock(null, 200);
        KitBlockData data = plugin.getKitManager().addKitToLocation(kit, b.getLocation());
        UltimateKits.getInstance().getDataManager().createBlockData(data);
        plugin.getLocale().newMessage("&8Kit &a" + kit.getKey() + " &8set to: &a" + b.getType().toString() + "&8.")
                .sendPrefixedMessage(sender);
        return ReturnType.SUCCESS;

    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        if (!(sender instanceof Player)) {
            return null;
        }

        if (args.length == 1) {
            List<String> tab = new ArrayList<>();
            for (Kit kit : UltimateKits.getInstance().getKitManager().getKits()) {
                tab.add(kit.getKey());
            }
            return tab;
        }
        return new ArrayList<>();
    }

    @Override
    public String getPermissionNode() {
        return "ultimatekits.admin";
    }

    @Override
    public String getSyntax() {
        return "set <kit>";
    }

    @Override
    public String getDescription() {
        return "Make the block you are looking at display a kit.";
    }
}
