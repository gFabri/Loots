package com.github.bfabri.loots.utils;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;

import java.util.*;
import java.util.stream.Collectors;

public class Utils {
	public static String translate(String input) {
		return ChatColor.translateAlternateColorCodes('&', input.replace("<", "«").replace(">", "»"));
	}

	public static List<String> translate(List<String> input) {
		List<String> newInput = new ArrayList<String>();
		input.forEach(line -> newInput.add(ChatColor.translateAlternateColorCodes('&', line)));
		return newInput;
	}

	public static Enchantment getEnchantmentFromNiceName(String name) {
		Enchantment enchantment = null;
		try {
			enchantment = Enchantment.getByName(name);
		} catch (Exception ignored) {}
		if (enchantment != null)
			return enchantment;
		switch (name.toLowerCase()) {
			case "sharpness":
				enchantment = Enchantment.DAMAGE_ALL;
				break;
			case "unbreaking":
				enchantment = Enchantment.DURABILITY;
				break;
			case "efficiency":
				enchantment = Enchantment.DIG_SPEED;
				break;
			case "protection":
				enchantment = Enchantment.PROTECTION_ENVIRONMENTAL;
				break;
			case "power":
				enchantment = Enchantment.ARROW_DAMAGE;
				break;
			case "punch":
				enchantment = Enchantment.ARROW_KNOCKBACK;
				break;
			case "infinite":
				enchantment = Enchantment.ARROW_INFINITE;
				break;
		}
		return enchantment;
	}

	public static int getVersion() {
		return Integer.parseInt(Bukkit.getServer().getBukkitVersion().split("-")[0].split("\\.")[1]);
	}

	public static List<String> getCompletions(String[] args, List<String> input) {
		return getCompletions(args, input, 80);
	}

	public static List<String> getCompletions(String[] args, String... input) {
		return getCompletions(args, 80, input);
	}

	public static List<String> getCompletions(String[] args, List<String> input, int limit) {
		Preconditions.checkNotNull(args);
		Preconditions.checkArgument(args.length != 0);
		String argument = args[args.length - 1];
		return input.stream().filter(string -> string.regionMatches(true, 0, argument, 0, argument.length())).limit(limit).collect(Collectors.toList());
	}

	public static List<String> getCompletions(String[] args, int limit, String... input) {
		Preconditions.checkNotNull(args);
		Preconditions.checkArgument(args.length != 0);
		String argument = args[args.length - 1];
		return Arrays.stream(input).collect(Collectors.toList()).stream().filter(string -> string.regionMatches(true, 0, argument, 0, argument.length())).limit(limit).collect(Collectors.toList());
	}
}
