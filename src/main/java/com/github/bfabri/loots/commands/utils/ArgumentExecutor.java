package com.github.bfabri.loots.commands.utils;

import com.github.bfabri.loots.utils.Utils;
import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ArgumentExecutor implements CommandExecutor, TabCompleter {
    protected List<CommandArgument> arguments;
    @Getter
    protected String label;

    public ArgumentExecutor(String label) {
        this.arguments = new ArrayList<>();
        this.label = label;
    }

    public boolean containsArgument(CommandArgument argument) {
        return this.arguments.contains(argument);
    }

    public void addArgument(CommandArgument argument) {
        this.arguments.add(argument);
    }

    public void removeArgument(CommandArgument argument) {
        this.arguments.remove(argument);
    }

    public CommandArgument getArgument(String id) {
        for (CommandArgument argument : this.arguments) {
            String name = argument.getName();
            if (name.equalsIgnoreCase(id) || Arrays.asList(argument.getAliases()).contains(id.toLowerCase())) {
                return argument;
            }
        }
        return null;
    }

    public List<CommandArgument> getArguments() {
        return ImmutableList.copyOf(this.arguments);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> results = new ArrayList<String>();
        if (args.length < 2) {
            for (CommandArgument argument : this.arguments) {
                String permission = argument.getPermission();
                if (permission == null || sender.hasPermission(permission) || argument.isOnlyPlayer()) {
                    results.add(argument.getName());
                }
            }
        } else {
            CommandArgument argument2 = this.getArgument(args[0]);
            if (argument2 == null) {
                return results;
            }
            String permission2 = argument2.getPermission();
            if (permission2 == null || sender.hasPermission(permission2) || argument2.isOnlyPlayer()) {
                results = argument2.onTabComplete(sender, command, label, args);
                if (results == null) {
                    return null;
                }
            }
        }
        return Utils.getCompletions(args, results);
    }
}