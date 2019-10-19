package com.songoda.ultimatekits.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.ultimatekits.UltimateKits;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CommandReload extends AbstractCommand {

    final UltimateKits instance = UltimateKits.getInstance();
    public CommandReload() {
        super(false, "reload");
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        instance.reloadConfig();
        instance.getLocale().getMessage("&7Configuration and Language files reloaded.").sendPrefixedMessage(sender);
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
        return "/KitAdmin reload";
    }

    @Override
    public String getDescription() {
        return "Reload the Configuration and Language files.";
    }
}
