package com.songoda.ultimatekits.command;

import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.command.commands.*;
import com.songoda.ultimatekits.utils.Methods;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandManager implements CommandExecutor {

    private UltimateKits plugin;
    private TabManager tabManager;

    private List<AbstractCommand> commands = new ArrayList<>();

    public CommandManager(UltimateKits plugin) {
        this.plugin = plugin;
        this.tabManager = new TabManager(this);

        plugin.getCommand("kitadmin").setExecutor(this);
        plugin.getCommand("kit").setExecutor(this);
        plugin.getCommand("previewkit").setExecutor(this);

        addCommand(new CommandKit());
        addCommand(new CommandPreviewKit());
        AbstractCommand commandUltimateKits = addCommand(new CommandUltimateKits());

        addCommand(new CommandReload(commandUltimateKits));
        addCommand(new CommandSettings(commandUltimateKits));
        addCommand(new CommandCreatekit(commandUltimateKits));
        addCommand(new CommandEdit(commandUltimateKits));
        addCommand(new CommandKey(commandUltimateKits));
        addCommand(new CommandSet(commandUltimateKits));
        addCommand(new CommandRemove(commandUltimateKits));

        for (AbstractCommand abstractCommand : commands) {
            if (abstractCommand.getParent() != null) continue;
            plugin.getCommand(abstractCommand.getCommand()).setTabCompleter(tabManager);
        }
    }

    private AbstractCommand addCommand(AbstractCommand abstractCommand) {
        commands.add(abstractCommand);
        return abstractCommand;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        for (AbstractCommand abstractCommand : commands) {
            if (abstractCommand.getCommand() != null && abstractCommand.getCommand().equalsIgnoreCase(command.getName().toLowerCase())) {
                if (strings.length == 0 || abstractCommand.hasArgs()) {
                    processRequirements(abstractCommand, commandSender, strings);
                    return true;
                }
            } else if (strings.length != 0 && abstractCommand.getParent() != null && abstractCommand.getParent().getCommand().equalsIgnoreCase(command.getName())) {
                String cmd = strings[0];
                String cmd2 = strings.length >= 2 ? String.join(" ", strings[0], strings[1]) : null;
                for (String cmds : abstractCommand.getSubCommand()) {
                    if (cmd.equalsIgnoreCase(cmds) || (cmd2 != null && cmd2.equalsIgnoreCase(cmds))) {
                        processRequirements(abstractCommand, commandSender, strings);
                        return true;
                    }
                }
            }
        }
        plugin.getLocale().newMessage("&7The command you entered does not exist or is spelt incorrectly.").sendPrefixedMessage(commandSender);
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
                plugin.getLocale().newMessage("&cInvalid Syntax!").sendPrefixedMessage(sender);
                plugin.getLocale().newMessage("&7The valid syntax is: &6" + command.getSyntax() + "&7.").sendPrefixedMessage(sender);
            }
            return;
        }
        plugin.getLocale().getMessage("event.general.nopermission").sendPrefixedMessage(sender);
    }

    public List<AbstractCommand> getCommands() {
        return Collections.unmodifiableList(commands);
    }
}
