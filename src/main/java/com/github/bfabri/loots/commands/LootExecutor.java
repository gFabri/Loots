package com.github.bfabri.loots.commands;

import com.github.bfabri.loots.ConfigHandler;
import com.github.bfabri.loots.Loots;
import com.github.bfabri.loots.commands.arguments.CreateArgument;
import com.github.bfabri.loots.commands.arguments.DeleteArgument;
import com.github.bfabri.loots.commands.arguments.KeyArgument;
import com.github.bfabri.loots.commands.arguments.ListArgument;
import com.github.bfabri.loots.commands.utils.CommandArgument;
import com.github.bfabri.loots.commands.utils.framework.BaseCommand;
import com.github.bfabri.loots.commands.utils.framework.CommandsUtils;
import com.github.bfabri.loots.loot.Loot;
import com.github.bfabri.loots.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LootExecutor extends BaseCommand {
    private final List<CommandArgument> arguments;

    public LootExecutor() {
        super("loots", "Manage Loots commands.");
        this.arguments = new ArrayList<>();
        this.setUsage("/(command)");

        this.arguments.add(new CreateArgument());
        this.arguments.add(new DeleteArgument());
        this.arguments.add(new ListArgument());
        this.arguments.add(new KeyArgument());
        this.onlyPlayer = true;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            CommandsUtils.printUsageWithoutCommands(sender, label, this.arguments);
            return true;
        }
        CommandArgument argument = CommandsUtils.matchArgument(args[0], sender, this.arguments);
        if (args.length == 1 && argument == null) {
            Loot loot = Loots.getInstance().getLootInterface().getLoot(args[0]);
            if (args[0].equalsIgnoreCase("all")) {
                Loots.getInstance().getLootInterface().getLoots().forEach((lootName, loots) -> ((Player) sender).getInventory().addItem(loots.getLootItem()));
                sender.sendMessage(Utils.translate(Objects.requireNonNull(ConfigHandler.Configs.LANG.getConfig().getString("LOOT_ALL_RECEIVED"))));
                return true;
            } else if (loot != null) {
                ((Player) sender).getInventory().addItem(loot.getLootItem());
                sender.sendMessage(Utils.translate(Objects.requireNonNull(ConfigHandler.Configs.LANG.getConfig().getString("LOOT_RECEIVED")).replace("{loot}", loot.getName())));
                return true;
            }
        }
        if (argument == null) {
            CommandsUtils.printUsageWithoutCommands(sender, label, this.arguments);
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