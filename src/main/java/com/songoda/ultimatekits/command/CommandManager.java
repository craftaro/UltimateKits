package com.songoda.ultimatekits.command;

import com.songoda.arconix.api.methods.formatting.TextComponent;
import com.songoda.ultimatekits.Lang;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.command.commands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandManager implements CommandExecutor {

    private UltimateKits plugin;

    private List<AbstractCommand> commands = new ArrayList<>();

    public CommandManager(UltimateKits plugin) {
        this.plugin = plugin;

        plugin.getCommand("UltimateKits").setExecutor(this);
        plugin.getCommand("PreviewKit").setExecutor(this);
        plugin.getCommand("Kits").setExecutor(this);

        AbstractCommand commandUltimateKits = addCommand(new CommandUltimateKits());

        addCommand(new CommandPreviewKit());
        addCommand(new CommandKits());

        addCommand(new CommandReload(commandUltimateKits));
        addCommand(new CommandSettings(commandUltimateKits));
        addCommand(new CommandCreatekit(commandUltimateKits));
        addCommand(new CommandEdit(commandUltimateKits));
        addCommand(new CommandKey(commandUltimateKits));
        addCommand(new CommandSet(commandUltimateKits));
        addCommand(new CommandRemove(commandUltimateKits));
    }

    private AbstractCommand addCommand(AbstractCommand abstractCommand) {
        commands.add(abstractCommand);
        return abstractCommand;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        for (AbstractCommand abstractCommand : commands) {
            if (abstractCommand.getCommand().equalsIgnoreCase(command.getName())) {
                if (strings.length == 0 || !abstractCommand.isSubCommands()) {
                    processRequirements(abstractCommand, commandSender, strings);
                    return true;
                }
            } else if (strings.length != 0 && abstractCommand.getParent() != null && abstractCommand.getParent().getCommand().equalsIgnoreCase(command.getName())) {
                String cmd = strings[0];
                if (cmd.equalsIgnoreCase(abstractCommand.getCommand())) {
                    processRequirements(abstractCommand, commandSender, strings);
                    return true;
                }
            }
        }
        commandSender.sendMessage(plugin.getReferences().getPrefix() + TextComponent.formatText("&7The command you entered does not exist or is spelt incorrectly."));
        return true;
    }

    private void processRequirements(AbstractCommand command, CommandSender sender, String[] strings) {
        if (!(sender instanceof Player) && command.isNoConsole()) {
            sender.sendMessage("You must be a player to use this command.");
            return;
        }
        if (command.getPermissionNode() == null || sender.hasPermission(command.getPermissionNode())) {
            AbstractCommand.ReturnType returnType = command.runCommand(plugin, sender, strings);
            if (returnType == AbstractCommand.ReturnType.SYNTAX_ERROR) {
                sender.sendMessage(plugin.getReferences().getPrefix() + TextComponent.formatText("&cInvalid Syntax!"));
                sender.sendMessage(plugin.getReferences().getPrefix() + TextComponent.formatText("&7The valid syntax is: &6" + command.getSyntax() + "&7."));
            }
            return;
        }
        sender.sendMessage(plugin.getReferences().getPrefix() + Lang.NO_PERM.getConfigValue());
    }

    public List<AbstractCommand> getCommands() {
        return Collections.unmodifiableList(commands);
    }
}
