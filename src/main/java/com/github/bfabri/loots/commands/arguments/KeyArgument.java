package com.github.bfabri.loots.commands.arguments;

import com.github.bfabri.loots.ConfigHandler;
import com.github.bfabri.loots.Loots;
import com.github.bfabri.loots.commands.utils.CommandArgument;
import com.github.bfabri.loots.loot.Loot;
import com.github.bfabri.loots.loot.key.Key;
import com.github.bfabri.loots.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class KeyArgument extends CommandArgument {
	public KeyArgument() {
		super("key", "Give a key for loot");
		this.permission = "loots.command.key";
		this.onlyPlayer = true;
	}

	@Override
	public String getUsage(String label) {
		return '/' + label + ' ' + this.getName() + ' ' + "<name> <amount> <player>";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 3) {
			sender.sendMessage(ChatColor.RED + getUsage(label));
			return true;
		}
		
		String name = args[1];

		Loot loot = Loots.getInstance().getLootInterface().getLoot(name);

		String amount = args[2];

		if (loot == null) {
			sender.sendMessage(Utils.translate(ConfigHandler.Configs.LANG.getConfig().getString("LOOT_DOES_NOT_EXIST").replace("{loot}", name)));
			return true;
		}

		Key key = loot.getKey();

		if (key == null) {
			sender.sendMessage(ChatColor.RED + "Could not get key for Loot: '" + loot.getName() + "'");
			return true;
		}

		if (args.length != 3) {
			try {
				if (args[3].equalsIgnoreCase("all")) {
					Bukkit.getOnlinePlayers().forEach(onlinePlayers -> {
						if (onlinePlayers.getInventory().firstEmpty() == -1) {
							ItemStack itemStack = loot.getKey(Integer.parseInt(amount), sender);
							onlinePlayers.getWorld().dropItemNaturally(onlinePlayers.getLocation(), itemStack);
							return;
						}
						onlinePlayers.sendMessage(Utils.translate(ConfigHandler.Configs.LANG.getConfig().getString("KEY_RECEIVED").replace("{key}", loot.getName()).replace("{amount}", String.valueOf(amount))));
						ItemStack keyItem = loot.getKey(Integer.parseInt(amount), sender);
						HashMap<Integer, ItemStack> remaining = onlinePlayers.getInventory().addItem(keyItem);
						int amountLeft = 0;
						for (Map.Entry<Integer, ItemStack> item : remaining.entrySet())
							amountLeft += (item.getValue()).getAmount();
						if (amountLeft > 0) {
							Bukkit.getPlayer(args[3]).getInventory().addItem(loot.getKey(amountLeft, sender));
						}
					});
					sender.sendMessage(Utils.translate(ConfigHandler.Configs.LANG.getConfig().getString("KEY_GIVEN").replace("{key}", loot.getName()).replace("{amount}", String.valueOf(amount)).replace("{player}", "All online players")));
				} else {
					if (Bukkit.getPlayer(args[3]) == null) {
						sender.sendMessage(ChatColor.RED + "Player not found!");
						return true;
					}
					if (Bukkit.getPlayer(args[3]).getInventory().firstEmpty() == -1) {
						ItemStack itemStack = loot.getKey(Integer.parseInt(amount), sender);
						Bukkit.getPlayer(args[3]).getWorld().dropItemNaturally(Bukkit.getPlayer(args[3]).getLocation(), itemStack);
						return true;
					}
					Bukkit.getPlayer(args[3]).sendMessage(Utils.translate(ConfigHandler.Configs.LANG.getConfig().getString("KEY_RECEIVED").replace("{key}", loot.getName()).replace("{amount}", String.valueOf(amount))));
					ItemStack keyItem = loot.getKey(Integer.parseInt(amount), sender);
					HashMap<Integer, ItemStack> remaining = Bukkit.getPlayer(args[3]).getInventory().addItem(keyItem);
					int amountLeft = 0;
					for (Map.Entry<Integer, ItemStack> item : remaining.entrySet())
						amountLeft += (item.getValue()).getAmount();
					if (amountLeft > 0) {
						Bukkit.getPlayer(args[3]).getInventory().addItem(loot.getKey(amountLeft, sender));
					}
					sender.sendMessage(Utils.translate(ConfigHandler.Configs.LANG.getConfig().getString("KEY_GIVEN").replace("{key}", loot.getName()).replace("{amount}", String.valueOf(amount)).replace("{player}", Bukkit.getPlayer(args[3]).getDisplayName())));
				}
			} catch (NumberFormatException e) {
				sender.sendMessage(Utils.translate(Objects.requireNonNull(ConfigHandler.Configs.LANG.getConfig().getString("INVALID_AMOUNT"))));
				return true;
			}
		} else {
			ItemStack keyItem = loot.getKey(Integer.parseInt(amount), sender);
			if (sender instanceof Player) {
				((Player) sender).getInventory().addItem(keyItem);
			}
		}
		return true;
	}
}