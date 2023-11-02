package com.github.bfabri.loots.commands.arguments;

import com.github.lfabril.loots.Loots;
import com.github.lfabril.loots.utils.LootUtils;
import com.github.lfabril.loots.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class KeyCommand implements CommandExecutor {
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (sender.hasPermission("loot.key")) {
      if (args.length < 3) {
        sender.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.key-help")));
        return true;
      } 
      try {
        if (args[0].equalsIgnoreCase("all")) {
          LootUtils.giveKeyAll(args[1], Integer.valueOf(Integer.parseInt(args[2])), sender);
        } else if (Bukkit.getPlayer(args[0]) != null) {
          LootUtils.giveKey(Bukkit.getPlayer(args[0]), args[1], Integer.valueOf(Integer.parseInt(args[2])), sender);
        } else {
          sender.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.player-no-online").replace("%player%", args[0])));
          return true;
        } 
      } catch (NumberFormatException e) {
        sender.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.amount-invalid")));
        return true;
      } 
    } else {
      sender.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.no-permission")));
      return true;
    } 
    return false;
  }
}
