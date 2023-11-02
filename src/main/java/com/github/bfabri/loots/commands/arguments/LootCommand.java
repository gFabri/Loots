package com.github.bfabri.loots.commands.arguments;

import com.github.lfabril.loots.ConfigHandler;
import com.github.lfabril.loots.Loots;
import com.github.lfabril.loots.loot.Loot;
import com.github.lfabril.loots.utils.LootUtils;
import com.github.lfabril.loots.utils.Util;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LootCommand implements CommandExecutor {
  private final Loots loot;
  
  public LootCommand(Loots loot) {
    this.loot = loot;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (sender.hasPermission("loot.usage")) {
      if (args.length < 1) {
        Loots.getInstance().getMessages().getStringList("Messages.loot-help").forEach(lines -> sender.sendMessage(Util.translate(lines)));
        return true;
      } 
      if (args.length == 1 && !args[0].equalsIgnoreCase("create") && !args[0].equalsIgnoreCase("delete") && !args[0].equalsIgnoreCase("list") && !args[0].equalsIgnoreCase("displayname") && !args[0].equalsIgnoreCase("reloadHolograms") && !args[0].equalsIgnoreCase("removeHolograms") && !args[0].equalsIgnoreCase("size") && !args[0].equalsIgnoreCase("percentage") && !args[0].equalsIgnoreCase("package") && !args[0].equalsIgnoreCase("setpackage") && !args[0].equalsIgnoreCase("command") && !args[0].equalsIgnoreCase("reload")) {
        if (args[0].equalsIgnoreCase("all")) {
          LootUtils.giveLoot((Player)sender, null, true);
          sender.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.gived-all")));
        } else {
          if (LootUtils.getLoots().get(args[0].toLowerCase()) == null || 
            !(LootUtils.getLoots().get(args[0].toLowerCase()) instanceof com.github.lfabril.loots.loot.keys.LootKey)) {
            sender.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.not-exist").replace("%loot%", args[0])));
            return true;
          } 
          Loot loot = (Loot)LootUtils.getLoots().get(args[0].toLowerCase());
          LootUtils.giveLoot((Player)sender, loot, false);
          sender.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.gived").replace("%loot%", loot.getName())));
        } 
        return true;
      } 
      if (args[0].equalsIgnoreCase("create")) {
        if (args.length < 2) {
          setUsage(sender, "loot create <name>");
          return true;
        } 
        if (this.loot.getLootsConfig().isSet("Loots." + args[1])) {
          sender.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.exist").replace("%loot%", args[1])));
          return true;
        } 
        LootUtils.registerLoot(this.loot.getLootsConfig(), args[1]);
        sender.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.created").replace("%loot%", args[1])));
      } else if (args[0].equalsIgnoreCase("delete")) {
        if (args.length < 2) {
          setUsage(sender, "loot delete <name>");
          return true;
        } 
        if (!this.loot.getLootsConfig().isSet("Loots." + args[1])) {
          sender.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.not-exist").replace("%loot%", args[1])));
          return true;
        } 
        LootUtils.unregisterLoot(this.loot.getLootsConfig(), args[1]);
        sender.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.deleted").replace("%loot%", args[1])));
      } else if (args[0].equalsIgnoreCase("list")) {
        sender.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.list").replace("%loots%", this.loot.getLootsConfig().getConfigurationSection("Loots").getKeys(false).stream().map(String::toLowerCase).collect((Collector)Collectors.joining(",")))));
      } else if (args[0].equalsIgnoreCase("displayname")) {
        if (args.length < 3) {
          setUsage(sender, "loot delete <name> <displayname>");
          return true;
        } 
        String name = args[1];
        String newdisplay = args[2];
        if (!LootUtils.getLoots().containsKey(name.toLowerCase())) {
          sender.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.not-exist").replace("%loot%", args[1])));
          return true;
        } 
        LootUtils.displaynameLoot(this.loot.getLootsConfig(), name, newdisplay);
        sender.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.changed").replace("%oldloot%", name).replace("%newloot%", newdisplay)));
      } else if (args[0].equalsIgnoreCase("size")) {
        if (args.length < 3) {
          setUsage(sender, "loot size <name> <size>");
          return true;
        } 
        String name = args[1];
        int size = Integer.parseInt(args[2]);
        if (!LootUtils.getLoots().containsKey(name.toLowerCase())) {
          sender.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.not-exist").replace("%loot%", args[1])));
          return true;
        } 
        LootUtils.resize(this.loot.getLootsConfig(), name, size);
        sender.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.resized").replace("%loot%", name).replace("%size%", String.valueOf(size))));
      } else if (args[0].equalsIgnoreCase("percentage")) {
        if (args.length < 2) {
          setUsage(sender, "loot percentage <name>");
          return true;
        } 
        String name = args[1];
        if (!LootUtils.getLoots().containsKey(name.toLowerCase())) {
          sender.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.not-exist").replace("%loot%", args[1])));
          return true;
        } 
        LootUtils.openPercentageChange((Player)sender, name);
      } else if (args[0].equalsIgnoreCase("setpackage")) {
        if (args.length < 4) {
          setUsage(sender, "loot setpackage <name> <package> <slot-1>");
          return true;
        } 
        String name = args[1];
        String pack = args[2];
        int slot = Integer.parseInt(args[3]);
        if (!LootUtils.getLoots().containsKey(name.toLowerCase())) {
          sender.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.not-exist").replace("%loot%", name)));
          return true;
        } 
        if (!LootUtils.getPackages().containsKey(pack.toLowerCase())) {
          sender.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.package-not-exist").replace("%package%", pack)));
          return true;
        } 
        LootUtils.setPackage((Player)sender, name, pack, slot);
      } else if (args[0].equalsIgnoreCase("reloadHolograms")) {
        LootUtils.removeAllHolograms();
        LootUtils.loadAllHolograms();
        sender.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.hologramsreloaded")));
      } else if (args[0].equalsIgnoreCase("removeHolograms")) {
        LootUtils.removeAllHolograms();
        sender.sendMessage(ChatColor.RED + "All holograms has been removed");
      } else if (args[0].equalsIgnoreCase("package")) {
        if (args.length < 2) {
          Loots.getInstance().getMessages().getStringList("Messages.package-help").forEach(lines -> sender.sendMessage(Util.translate(lines)));
          return true;
        } 
        if (args[1].equalsIgnoreCase("create")) {
          if (args.length < 3) {
            setUsage(sender, "loot package create <name>");
            return true;
          } 
          if (this.loot.getLootsConfig().isSet("Packages." + args[2])) {
            sender.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.package-exist").replace("%package%", args[2])));
            return true;
          } 
          LootUtils.registerPackage(this.loot.getLootsConfig(), args[2]);
          sender.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.package-created").replace("%package%", args[2])));
        } else if (args[1].equalsIgnoreCase("delete")) {
          if (args.length < 3) {
            setUsage(sender, "loot package delete <name>");
            return true;
          } 
          if (!this.loot.getLootsConfig().isSet("Packages." + args[2])) {
            sender.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.package-not-exist").replace("%package%", args[2])));
            return true;
          } 
          LootUtils.unregisterPackage(this.loot.getLootsConfig(), args[2]);
          sender.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.package-deleted").replace("%package%", args[2])));
        } else if (args[1].equalsIgnoreCase("edit")) {
          if (args.length < 3) {
            setUsage(sender, "loot package edit <name>");
            return true;
          } 
          if (!this.loot.getLootsConfig().isSet("Packages." + args[2])) {
            sender.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.package-not-exist").replace("%package%", args[2])));
            return true;
          } 
          LootUtils.openEditPackage((Player)sender, args[2]);
        } else if (args[1].equalsIgnoreCase("list")) {
          sender.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.package-list").replace("%packages%", this.loot.getLootsConfig().getConfigurationSection("Packages").getKeys(false).stream().map(String::toLowerCase).collect((Collector)Collectors.joining(",")))));
        } 
      } else if (args[0].equalsIgnoreCase("command")) {
        if (args.length < 2) {
          Loots.getInstance().getMessages().getStringList("Messages.command-help").forEach(lines -> sender.sendMessage(Util.translate(lines)));
          return true;
        } 
        if (args[1].equalsIgnoreCase("add")) {
          if (args.length < 5) {
            setUsage(sender, "loot command add <name> <slot-1> <command>");
            return true;
          } 
          if (!this.loot.getLootsConfig().isSet("Loots." + args[2])) {
            sender.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.not-exist").replace("%loot%", args[2])));
            return true;
          } 
          try {
            StringBuilder builder = new StringBuilder();
            for (int i = 4; i < args.length; i++)
              builder.append(args[i]).append(" "); 
            LootUtils.addCommand(args[2], Integer.parseInt(args[3]), builder.toString(), sender);
          } catch (NumberFormatException ex) {
            sender.sendMessage(Util.translate("&cSlot incorrect or invalid!"));
          } 
        } else if (args[1].equalsIgnoreCase("remove")) {
          if (args.length < 5) {
            setUsage(sender, "loot command remove <name> <slot-1> <index>");
            return true;
          } 
          if (!this.loot.getLootsConfig().isSet("Loots." + args[2])) {
            sender.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.not-exist").replace("%loot%", args[2])));
            return true;
          } 
          try {
            LootUtils.removeCommand(args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4]), sender);
          } catch (NumberFormatException ex) {
            sender.sendMessage(Util.translate("&cSlot incorrect or invalid!"));
          } 
        } else if (args[1].equalsIgnoreCase("list")) {
          if (args.length < 4) {
            setUsage(sender, "loot command list <name> <slot-1>");
            return true;
          } 
          try {
            LootUtils.listCommands(args[2], Integer.parseInt(args[3]), sender);
          } catch (NumberFormatException ex) {
            sender.sendMessage(Util.translate("&cSlot incorrect or invalid!"));
          } 
        } 
      } else if (args[0].equalsIgnoreCase("reload")) {
        ConfigHandler.Configs.LOOTS.reloadConfig();
        ConfigHandler.Configs.MESSAGES.reloadConfig();
        LootUtils.getLoots().forEach((loot, loots) -> loots.loadLootBase());
        sender.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.reloaded")));
      } else {
        Loots.getInstance().getMessages().getStringList("Messages.loot-help").forEach(lines -> sender.sendMessage(Util.translate(lines)));
        return true;
      } 
    } else {
      sender.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.no-permission")));
    } 
    return false;
  }
  
  public void setUsage(CommandSender sender, String command) {
    sender.sendMessage(Util.translate("&7&m\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550&8[&6&lLoots&8]&7&m\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550"));
    sender.sendMessage(Util.translate("&e/" + command));
    sender.sendMessage(Util.translate("&7&m\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550"));
  }
}
