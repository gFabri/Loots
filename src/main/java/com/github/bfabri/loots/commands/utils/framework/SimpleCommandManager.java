package com.github.bfabri.loots.commands.utils.framework;

import com.github.bfabri.loots.Loots;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
public class SimpleCommandManager implements CommandManager {
    private final Map<String, BaseCommand> commandMap;

    public SimpleCommandManager(Loots plugin) {
        commandMap = new HashMap<>();
        ConsoleCommandSender console = plugin.getServer().getConsoleSender();
        new BukkitRunnable() {
            @Override
            public void run() {
                Collection<BaseCommand> commands = commandMap.values();
                for (BaseCommand command : commands) {
                    String commandName = command.getName();
                    PluginCommand pluginCommand = plugin.getCommand(commandName);
                    if (pluginCommand == null) {
                        console.sendMessage('[' + plugin.getName() + "] " + "Failed to register command '" + commandName + "'.");
                        console.sendMessage('[' + plugin.getName() + "] " + "Reason: Undefined in plugin.yml.");
                    } else {
                        pluginCommand.setAliases(Arrays.asList(command.getAliases()));
                        pluginCommand.setDescription(command.getDescription());
                        pluginCommand.setExecutor(command);
                        pluginCommand.setTabCompleter(command);
                        pluginCommand.setUsage(command.getUsage());
                        pluginCommand.setPermission("loots.command." + command.getName());
                        pluginCommand.setPermissionMessage(ChatColor.translateAlternateColorCodes('&',  "&4You do not have permission to use this command."));
                    }
                }
            }
        }.runTask(plugin);
    }


    @Override
    public boolean containsCommand(BaseCommand command) {
        return this.commandMap.containsValue(command);
    }

    @Override
    public void registerAll(BaseCommandModule module) {
        if (module.isEnabled()) {
            Set<BaseCommand> commands = module.getCommands();
            for (BaseCommand command : commands) {
                this.commandMap.put(command.getName(), command);
            }
        }
    }

    @Override
    public void registerCommand(BaseCommand command) {
        this.commandMap.put(command.getName(), command);
    }

    @Override
    public void registerCommands(BaseCommand[] commands) {
        for (BaseCommand command : commands) {
            this.commandMap.put(command.getName(), command);
        }
    }

    @Override
    public void unregisterCommand(BaseCommand command) {
        this.commandMap.values().remove(command);
    }

    @Override
    public BaseCommand getCommand(String id) {
        return this.commandMap.get(id);
    }
}