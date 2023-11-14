package com.github.bfabri.loots.commands.arguments;

import com.github.bfabri.loots.ConfigHandler;
import com.github.bfabri.loots.Loots;
import com.github.bfabri.loots.commands.utils.CommandArgument;
import com.github.bfabri.loots.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateArgument extends CommandArgument {
	public CreateArgument() {
		super("create", "Create a loot");
		this.permission = "loots.command.create";
		this.onlyPlayer = false;
	}

	@Override
	public String getUsage(String label) {
		return '/' + label + ' ' + this.getName() + ' ' + "<name>";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + getUsage(label));
			return true;
		}

		String name = args[1];

		if (Loots.getInstance().getLootInterface().getLoots().containsKey(name)) {
			sender.sendMessage(Utils.translate(ConfigHandler.Configs.LANG.getConfig().getString("LOOT_ALREADY_EXISTS").replace("{loot}", name)));
			return true;
		}

		Loots.getInstance().getLootInterface().addLoot(name);
		sender.sendMessage(Utils.translate(ConfigHandler.Configs.LANG.getConfig().getString("LOOT_CREATED").replace("{loot}", name)));

		if (sender instanceof Player) {
			((Player) sender).setMetadata("loot", new org.bukkit.metadata.FixedMetadataValue(Loots.getInstance(), name));
			((Player) sender).getInventory().addItem(Loots.getInstance().getLootInterface().getLoot(name).getLootItem());
		}
		return true;
	}
}