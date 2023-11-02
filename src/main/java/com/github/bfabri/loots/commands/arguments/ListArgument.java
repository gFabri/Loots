package com.github.bfabri.loots.commands.arguments;

import com.github.bfabri.armoreffects.ArmorEffects;
import com.github.bfabri.armoreffects.commands.utils.CommandArgument;
import com.github.bfabri.armoreffects.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ListArgument extends CommandArgument {
	public ListArgument() {
		super("list", "List of effects");
		this.permission = "armoreffect.command.armoreffect";
		this.onlyPlayer = true;
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

		sender.sendMessage(Utils.translate("&3List of effects&7: &b") + ArmorEffects.getListOfPotions().stream().map(String::toLowerCase).collect(Collectors.joining(",")));
		return true;
	}
}