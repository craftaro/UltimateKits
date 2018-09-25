package com.songoda.ultimatekits.command;

import com.songoda.ultimatekits.UltimateKits;
import org.bukkit.command.CommandSender;

public abstract class AbstractCommand {

    public enum ReturnType { SUCCESS, FAILURE, SYNTAX_ERROR }

    private final AbstractCommand parent;

    private final String command;

    private final boolean noConsole;

    private final boolean subCommands;

    protected AbstractCommand(String command, AbstractCommand parent, boolean noConsole, boolean subCommands) {
        this.command = command;
        this.parent = parent;
        this.noConsole = noConsole;
        this.subCommands = subCommands;
    }

    public AbstractCommand getParent() {
        return parent;
    }

    public String getCommand() {
        return command;
    }

    public boolean isNoConsole() {
        return noConsole;
    }

    public boolean isSubCommands() {
        return subCommands;
    }

    protected abstract ReturnType runCommand(UltimateKits instance, CommandSender sender, String... args);

    public abstract String getPermissionNode();

    public abstract String getSyntax();

    public abstract String getDescription();
}
