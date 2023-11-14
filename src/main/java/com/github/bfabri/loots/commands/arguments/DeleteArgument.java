package com.github.bfabri.loots.commands.arguments;

import com.github.bfabri.loots.ConfigHandler;
import com.github.bfabri.loots.Loots;
import com.github.bfabri.loots.commands.utils.CommandArgument;
import com.github.bfabri.loots.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class DeleteArgument extends CommandArgument {
	public DeleteArgument() {
		super("delete", "Delete a loot");
		this.permission = "loots.command.delete";
		this.onlyPlayer = true;
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

		if (!Loots.getInstance().getLootInterface().getLoots().containsKey(name)) {
			sender.sendMessage(Utils.translate(ConfigHandler.Configs.LANG.getConfig().getString("LOOT_DOES_NOT_EXIST").replace("{loot}", name)));
			return true;
		}
		Loots.getInstance().getLootInterface().deleteLoot(name);
		sender.sendMessage(Utils.translate(ConfigHandler.Configs.LANG.getConfig().getString("LOOT_DELETED").replace("{loot}", name)));
		return true;
	}
}