package com.songoda.ultimatekits.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.core.gui.GuiManager;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.Kit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandPreviewKit extends AbstractCommand {

    private final UltimateKits plugin;
    private final GuiManager guiManager;

    public CommandPreviewKit(UltimateKits plugin, GuiManager guiManager) {
        super(CommandType.PLAYER_ONLY, "PreviewKit");
        this.plugin = plugin;
        this.guiManager = guiManager;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;
        if (args.length != 1) {
            plugin.getLocale().getMessage("command.kit.nokitsupplied").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }
        Kit kit = plugin.getKitManager().getKit(args[0].toLowerCase().trim());
        if (kit == null) {
            plugin.getLocale().getMessage("command.kit.kitdoesntexist").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }
        kit.display(player, guiManager, null);
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        if (!(sender instanceof Player)) {
            return null;
        }

        if (args.length == 2) {
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
