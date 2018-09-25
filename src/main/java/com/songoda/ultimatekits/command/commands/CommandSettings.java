package com.songoda.ultimatekits.command.commands;

import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.command.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSettings extends AbstractCommand {

    public CommandSettings(AbstractCommand parent) {
        super("settings", parent, true, false);
    }

    @Override
    protected ReturnType runCommand(UltimateKits instance, CommandSender sender, String... args) {
        instance.getSettingsManager().openSettingsManager((Player)sender);
        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "ultimatekits.admin";
    }

    @Override
    public String getSyntax() {
        return "/uk settings";
    }

    @Override
    public String getDescription() {
        return "Edit the UltimateKits Settings.";
    }
}
