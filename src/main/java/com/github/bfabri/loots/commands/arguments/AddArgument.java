package com.github.bfabri.loots.commands.arguments;

import com.github.bfabri.armoreffects.ArmorEffects;
import com.github.bfabri.armoreffects.ConfigHandler;
import com.github.bfabri.armoreffects.commands.utils.CommandArgument;
import com.github.bfabri.armoreffects.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class AddArgument extends CommandArgument {
	public AddArgument() {
		super("add", "Add effect to item");
		this.permission = "armoreffect.command.armoreffect";
		this.onlyPlayer = true;
	}

	@Override
	public String getUsage(String label) {
		return '/' + label + ' ' + this.getName() + ' ' + "<effect> <level>";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 3) {
			sender.sendMessage(ChatColor.RED + getUsage(label));
			return true;
		}

		Player player = (Player) sender;

		String effect = args[1];
		String level = args[2];
		ItemStack hand = player.getItemInHand();

		if (hand == null) {
			sender.sendMessage(Utils.translate(ConfigHandler.Configs.LANG.getConfig().getString("NO_ITEM_IN_HAND")));
			return true;
		}

		if (!ArmorEffects.getListOfPotions().contains(Pattern.compile("(?i)&[0-9A-FK-OR]").matcher(effect.toUpperCase()).replaceAll(""))) {
			sender.sendMessage(Utils.translate(ConfigHandler.Configs.LANG.getConfig().getString("INVALID_EFFECT")));
			return true;
		}

		if (Utils.getLevel(level) == 0) {
			sender.sendMessage(Utils.translate(ConfigHandler.Configs.LANG.getConfig().getString("INVALID_LEVEL")));
			return true;
		}

		ItemMeta meta = hand.getItemMeta();
		if (meta == null) {
			meta = Bukkit.getItemFactory().getItemMeta(hand.getType());
			if (meta == null) {
				player.sendMessage(ChatColor.RED + "This item not have lore.");
				return true;
			}
		}
		List<String> lore = new ArrayList<>();
		if (meta.hasLore()) {
			lore = meta.getLore();
		}

		for (String line : lore) {
			if (line.split(" ")[0].equalsIgnoreCase(Utils.translate(effect.replace("_", " ")))) {
				sender.sendMessage(Utils.translate(ConfigHandler.Configs.LANG.getConfig().getString("EFFECT_ALREADY_ADDED").replace("{effect}", ChatColor.stripColor(effect)).replace("{item}", hand.getType().name())));
				return true;
			}
		}
		lore.add(Utils.translate(effect.replace("_", " ") + " " + level));
		meta.setLore(lore);
		hand.setItemMeta(meta);
		sender.sendMessage(Utils.translate(ConfigHandler.Configs.LANG.getConfig().getString("ADDED_EFFECT").replace("{effect}", ChatColor.stripColor(effect)).replace("{item}", hand.getType().name())));
		return true;
	}


	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return ArmorEffects.getListOfPotions();
	}
}