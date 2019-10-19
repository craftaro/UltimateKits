package com.songoda.ultimatekits.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.core.gui.GuiManager;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.Kit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class CommandPreviewKit extends AbstractCommand {

    final UltimateKits instance = UltimateKits.getInstance();
    final GuiManager guiManager;

    public CommandPreviewKit(GuiManager guiManager) {
        super(true, "PreviewKit");
        this.guiManager = guiManager;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;
        if (args.length != 1) {
            instance.getLocale().getMessage("command.kit.nokitsupplied").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }
        Kit kit = instance.getKitManager().getKit(args[0].toLowerCase().trim());
        if (kit == null) {
            instance.getLocale().getMessage("command.kit.kitdoesntexist").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }
        kit.display(player, guiManager, null);
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        if ((sender instanceof Player) && args.length == 1) {
            final String search = args[0].toLowerCase();
            return UltimateKits.getInstance().getKitManager().getKits().stream()
                    .map(kit -> kit.getName())
                    .filter(kit -> kit.toLowerCase().startsWith(search))
                    .collect(Collectors.toList());
        }
        return null;
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
