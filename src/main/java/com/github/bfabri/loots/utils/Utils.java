package com.github.bfabri.loots.utils;

import com.google.common.base.Preconditions;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;

import java.util.*;
import java.util.stream.Collectors;

public class Utils {

	private static final Map<String, String> soundByString = new HashMap<>();
	private static final Map<String, String> materialByString = new HashMap<>();

	static {
		String version = Bukkit.getServer().getBukkitVersion().split("-")[0];

		materialByString.put("STAINED", Integer.parseInt(version.split("\\.")[1]) > 18 ? "LEGACY_STAINED_GLASS_PANE" : Integer.parseInt(version.split("\\.")[1]) > 13 ? "LEGACY_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE");
		materialByString.put("GOLD_AXE", Integer.parseInt(version.split("\\.")[1]) > 18 ? "GOLDEN_AXE" : Integer.parseInt(version.split("\\.")[1]) > 13 ? "GOLDEN_AXE" : "GOLD_AXE");
		materialByString.put("BED_BLOCK", Integer.parseInt(version.split("\\.")[1]) > 18 ? "RED_BED" : Integer.parseInt(version.split("\\.")[1]) > 13 ? "RED_BED" : "BED_BLOCK");
		materialByString.put("BED_BLOCK2", Integer.parseInt(version.split("\\.")[1]) > 18 ? "RED_BED" : Integer.parseInt(version.split("\\.")[1]) > 13 ? "RED_BED" : "BED_BLOCK");
		materialByString.put("WATER", Integer.parseInt(version.split("\\.")[1]) > 18 ? "LEGACY_STATIONARY_WATER" : Integer.parseInt(version.split("\\.")[1]) > 13 ? "LEGACY_STATIONARY_WATER" : "STATIONARY_WATER");
		materialByString.put("SOUP", Integer.parseInt(version.split("\\.")[1]) > 13 ? "MUSHROOM_STEW" : Integer.parseInt(version.split("\\.")[1]) > 18 ? "MUSHROOM_STEW" : "MUSHROOM_SOUP");
		materialByString.put("ENDPORTAL", Integer.parseInt(version.split("\\.")[1]) > 18 ? "END_PORTAL_FRAME" : Integer.parseInt(version.split("\\.")[1]) > 13 ? "LEGACY_ENDER_PORTAL_FRAME" : "ENDER_PORTAL_FRAME");
		materialByString.put("LEASH", Integer.parseInt(version.split("\\.")[1]) > 18 ? "LEAD" : Integer.parseInt(version.split("\\.")[1]) > 13 ? "LEGACY_LEASH" : "LEASH");
		materialByString.put("DYE", Integer.parseInt(version.split("\\.")[1]) > 18 ? "LEGACY_INK_SACK" : Integer.parseInt(version.split("\\.")[1]) > 13 ? "LEGACY_INK_SACK" : "INK_SACK");
		materialByString.put("CLAY", Integer.parseInt(version.split("\\.")[1]) > 18 ? "LEGACY_STAINED_CLAY" : Integer.parseInt(version.split("\\.")[1]) > 13 ? "LEGACY_STAINED_CLAY" : "STAINED_CLAY");
		materialByString.put("WOOD", Integer.parseInt(version.split("\\.")[1]) > 18 ? "LEGACY_WOOD" : Integer.parseInt(version.split("\\.")[1]) > 13 ? "LEGACY_WOOD" : "WOOD");
		materialByString.put("SNOW", Integer.parseInt(version.split("\\.")[1]) > 18 ? "SNOWBALL" : Integer.parseInt(version.split("\\.")[1]) > 13 ? "LEGACY_SNOW_BALL" : "SNOW_BALL");
		materialByString.put("SKULL", Integer.parseInt(version.split("\\.")[1]) > 18 ? "SKELETON_SKULL" : Integer.parseInt(version.split("\\.")[1]) > 13 ? "LEGACY_SKULL_ITEM" : "SKULL_ITEM");
		materialByString.put("SHOVEL", Integer.parseInt(version.split("\\.")[1]) > 18 ? "DIAMOND_SHOVEL" : Integer.parseInt(version.split("\\.")[1]) > 13 ? "LEGACY_DIAMOND_SPADE" : "DIAMOND_SPADE");
		materialByString.put("MINECART", Integer.parseInt(version.split("\\.")[1]) > 18 ? "CHEST_MINECART" : Integer.parseInt(version.split("\\.")[1]) > 13 ? "CHEST_MINECART" : "STORAGE_MINECART");
		materialByString.put("TORCH", Integer.parseInt(version.split("\\.")[1]) > 18 ? "REDSTONE_TORCH" : Integer.parseInt(version.split("\\.")[1]) > 13 ? "REDSTONE_TORCH" : "REDSTONE_TORCH_ON");

		soundByString.put("BREAK", Integer.parseInt(version.split("\\.")[1]) > 13 ? "ENTITY_ITEM_BREAK" : "ITEM_BREAK");
		soundByString.put("EXPLODE", Integer.parseInt(version.split("\\.")[1]) > 13 ? "ENTITY_GENERIC_EXPLODE" : "EXPLODE");
		soundByString.put("HIT", Integer.parseInt(version.split("\\.")[1]) > 13 ? "ENTITY_ARROW_HIT" : "SUCCESSFUL_HIT");
		soundByString.put("LEVEL", Integer.parseInt(version.split("\\.")[1]) > 13 ? "ENTITY_PLAYER_LEVELUP" : "LEVEL_UP");
		soundByString.put("DRAGON", Integer.parseInt(version.split("\\.")[1]) > 13 ? "ENTITY_ENDER_DRAGON_GROWL" : "ENDERDRAGON_GROWL");
	}

	public static String translate(String input) {
		return ChatColor.translateAlternateColorCodes('&', input.replace("<", "«").replace(">", "»"));
	}

	public static Location deserializeLocation(String s) {
		String[] st = s.split(";");
		Location loc = new Location(Bukkit.getWorld(st[0]), Double.parseDouble(st[1]), Double.parseDouble(st[2]), Double.parseDouble(st[3]));
		loc.setPitch(Float.parseFloat(st[4]));
		loc.setYaw(Float.parseFloat(st[5]));
		return loc;
	}

	public static String serializeLocation(Location l) {
		String loc = l.getWorld().getName() + ";" + l.getX() + ";" + l.getY() + ";" + l.getZ() + ";" + l.getPitch() + ";" + l.getYaw();
		return loc;
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

	public static Material getMaterialByVersion(String material) {
		return Material.getMaterial(materialByString.getOrDefault(material, material));
	}

	public static Sound getSoundByVersion(String sound) {
		return Sound.valueOf(soundByString.getOrDefault(sound, sound));
	}

	public static int getVersion() {
		return Integer.parseInt(Bukkit.getServer().getBukkitVersion().split("-")[0].split("\\.")[1]);
	}

	public static boolean isOldVersion() {
		String[] split = Bukkit.getBukkitVersion().split("\\.");
		String serverVersion = split[0] + "_" + split[1] + "_R" + split[3].split("\\-")[0];
		return (!serverVersion.startsWith("1_7") || !serverVersion.startsWith("1_8"));
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
