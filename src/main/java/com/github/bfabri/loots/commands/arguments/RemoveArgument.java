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

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RemoveArgument extends CommandArgument {
	public RemoveArgument() {
		super("remove", "Remove effect from an item");
		this.permission = "armoreffect.command.armoreffect";
		this.onlyPlayer = true;
	}

	@Override
	public String getUsage(String label) {
		return '/' + label + ' ' + this.getName();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + getUsage(label));
			return true;
		}

		Player player = (Player) sender;

		if (player.getItemInHand() != null && !player.getItemInHand().hasItemMeta() || (player.getItemInHand() != null && !player.getItemInHand().getItemMeta().hasLore())) {
			player.sendMessage(ChatColor.RED + "This item not have lore.");
			return true;
		}

		if (player.getItemInHand() == null || !player.getItemInHand().hasItemMeta() || !player.getItemInHand().getItemMeta().hasLore()) {
			sender.sendMessage(Utils.translate(ConfigHandler.Configs.LANG.getConfig().getString("NO_ITEM_IN_HAND")));
			return true;
		}

		String effect = args[1];
		ItemStack hand = player.getItemInHand();

		if (!ArmorEffects.getListOfPotions().contains(Pattern.compile("(?i)&[0-9A-FK-OR]").matcher(effect.toUpperCase()).replaceAll(""))) {
			sender.sendMessage(Utils.translate(ConfigHandler.Configs.LANG.getConfig().getString("INVALID_EFFECT")));
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
		List<String> lore = meta.getLore();
		for (String lines : lore) {
			if (ChatColor.stripColor(lines.toLowerCase()).startsWith(ChatColor.stripColor(effect.toLowerCase().replace("_", " ")))) {
				lore.remove(lines);
				meta.setLore(lore);
				hand.setItemMeta(meta);
				sender.sendMessage(Utils.translate(ConfigHandler.Configs.LANG.getConfig().getString("REMOVED_EFFECT").replace("{effect}", ChatColor.stripColor(effect)).replace("{item}", hand.getType().name())));
				return true;
			}
		}
		sender.sendMessage(Utils.translate(ConfigHandler.Configs.LANG.getConfig().getString("NO_FOUND_EFFECT").replace("{effect}", ChatColor.stripColor(effect)).replace("{item}", hand.getType().name())));
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return ArmorEffects.getListOfPotions();
	}
}