package com.songoda.ultimatekits.command.commands;

import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.command.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandSettings extends AbstractCommand {

    public CommandSettings(AbstractCommand parent) {
        super(parent, true, "settings");
    }

    @Override
    protected ReturnType runCommand(UltimateKits instance, CommandSender sender, String... args) {
        instance.getSettingsManager().openSettingsManager((Player) sender);
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(UltimateKits instance, CommandSender sender, String... args) {
        return new ArrayList<>();
    }

    @Override
    public String getPermissionNode() {
        return "ultimatekits.admin";
    }

    @Override
    public String getSyntax() {
        return "/KitAdmin settings";
    }

    @Override
    public String getDescription() {
        return "Edit the UltimateKits Settings.";
    }
}
