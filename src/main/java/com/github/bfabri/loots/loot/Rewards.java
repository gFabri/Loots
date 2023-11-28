package com.github.bfabri.loots.loot;

import com.github.bfabri.loots.Loots;
import com.github.bfabri.loots.utils.CustomItem;
import com.github.bfabri.loots.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Rewards implements ConfigurationSerializable {

	@Getter
	private String material;

	@Getter
	private String amount;

	@Getter
	private String material_data;

	@Getter
	private List<String> lore;

	@Getter
	private String displayName;
	@Getter
	private ArrayList<String> enchantments = new ArrayList<>();

	@Getter
	private boolean useCommand;

	@Getter
	private final ArrayList<String> commands = new ArrayList<>();

	@Getter
	private boolean usePackage;

	@Getter
	private String packageName;

	@Getter
	@Setter
	private double percentage = 0.0D;

	public Rewards(org.bukkit.inventory.ItemStack stack) {
		material = stack.getType().toString();
		amount = String.valueOf(stack.getAmount());
		material_data = material.equalsIgnoreCase("POTION") ? String.valueOf(stack.getDurability()) : String.valueOf(stack.getData().getData());
		useCommand = false;
		usePackage = false;
		packageName = "None";

		if (stack.hasItemMeta()) {
			ItemMeta meta = stack.getItemMeta();
			lore = meta.hasLore() ? Utils.translate(meta.getLore()) : null;
			displayName = meta.hasDisplayName() ? Utils.translate(meta.getDisplayName()) : null;

			if (meta.hasEnchants()) {
				for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
					enchantments.add(entry.getKey().getName().toUpperCase() + ":" + entry.getValue());
				}
			}
		}
	}

	public Rewards(Map<String, Object> args) {
		material = (String) args.get("material");
		amount = (String) args.get("amount");
		material_data = (String) args.get("data");
		lore = (List<String>) args.get("lore");
		displayName = (String) args.get("displayName");
		enchantments = (ArrayList<String>) args.get("enchantments");
		useCommand = (boolean) args.get("useCommand");
		commands.addAll((List<String>) args.get("commands"));
		usePackage = (boolean) args.get("usePackage");
		packageName = (String) args.get("packageName");
		percentage = (double) args.get("percentage");
	}

	public static org.bukkit.inventory.ItemStack deserialize(Map<String, Object> args) {
		ArrayList<String> enchantments = (ArrayList<String>) args.get("enchantments");
		List<String> lore = (List<String>) args.get("lore");
		String displayName = (String) args.get("displayName");
		int amount = Integer.parseInt((String) args.get("amount"));
		short data = Short.parseShort((String) args.get("data"));
		String material = (String) args.get("material");


		return new CustomItem(Material.getMaterial(material), amount, data).setName(displayName).addLore(lore).addEnchantments(enchantments).create();
	}

	public org.bukkit.inventory.ItemStack getItemStack() {
		return new CustomItem(Material.getMaterial(material), Integer.parseInt(amount), Short.parseShort(material_data)).setName(displayName).addLore(lore).addEnchantments(enchantments).create();
	}

	public ItemStack runItems(Player player, Rewards rewards) {
		if (isUsePackage()) {
//			player.getInventory().addItem(deserialize(args));
		} else if (isUseCommand() && !getCommands().isEmpty()) {
			Bukkit.getScheduler().runTaskLater(Loots.getInstance(), () -> runCommands(player), 0L);
		} else if (!isUseCommand()) {
			return rewards.getItemStack();
		}
		return null;
	}

	private void runCommands(Player player) {
		for (String command : getCommands()) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player.getName()));
		}
	}


	@Override
	public Map<String, Object> serialize() {
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();

		map.put("material", getMaterial());
		map.put("amount", getAmount());
		map.put("data", getMaterial_data());
		map.put("useCommand", useCommand);
		map.put("commands", commands);
		map.put("usePackage", usePackage);
		map.put("packageName", packageName);
		map.put("percentage", percentage);
		if (getLore() != null) {
			map.put("lore", getLore());
		}
		if (getDisplayName() != null) {
			map.put("displayName", getDisplayName());
		}
		if (!getEnchantments().isEmpty()) {
			map.put("enchantments", getEnchantments());
		}
		return map;
	}
}