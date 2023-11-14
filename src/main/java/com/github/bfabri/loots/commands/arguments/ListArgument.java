package com.github.bfabri.loots.commands.arguments;

import com.github.bfabri.loots.Loots;
import com.github.bfabri.loots.commands.utils.CommandArgument;
import com.github.bfabri.loots.loot.Loot;
import com.github.bfabri.loots.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.stream.Collectors;

public class ListArgument extends CommandArgument {
	public ListArgument() {
		super("list", "List of Loots");
		this.permission = "loots.command.list";
		this.onlyPlayer = false;
	}

	@Override
	public String getUsage(String label) {
		return '/' + label + ' ' + this.getName();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 1) {
			sender.sendMessage(ChatColor.RED + getUsage(label));
			return true;
		}

		sender.sendMessage(Utils.translate("&cLoots&7: &f" + Loots.getInstance().getLootInterface().getLoots().values().stream().map(Loot::getName).collect(Collectors.joining(", "))));
		return true;
	}
}