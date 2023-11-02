package com.github.bfabri.loots.commands;

import com.github.bfabri.loots.commands.utils.CommandArgument;
import com.github.bfabri.loots.commands.utils.framework.BaseCommand;
import com.github.bfabri.loots.commands.utils.framework.CommandsUtils;
import com.github.bfabri.loots.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LootExecutor extends BaseCommand {
    private final List<CommandArgument> arguments;

    public LootExecutor() {
        super("loots", "Manage Loots commands.");
        this.arguments = new ArrayList<>();
        this.setUsage("/(command)");

//        this.arguments.add(new AddArgument());
//        this.arguments.add(new RemoveArgument());
//        this.arguments.add(new ListArgument());
        this.onlyPlayer = true;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            CommandsUtils.printUsage(sender, label, this.arguments);
            return true;
        }
        CommandArgument argument = CommandsUtils.matchArgument(args[0], sender, this.arguments);
        if (argument == null) {
            CommandsUtils.printUsage(sender, label, this.arguments);
            return true;
        }
        return argument.onCommand(sender, command, label, args);
    }
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }
        List<String> results;
        if (args.length <= 1) {
            results = CommandsUtils.getAccessibleArgumentNames(sender, this.arguments);
        } else {
            CommandArgument argument = CommandsUtils.matchArgument(args[0], sender, this.arguments);
            if (argument == null) {
                return Collections.emptyList();
            }
            results = argument.onTabComplete(sender, command, label, args);
        }
        return (results == null) ? null : Utils.getCompletions(args, results);
    }
}