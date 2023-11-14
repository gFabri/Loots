package com.github.bfabri.loots.loot.key;

import com.github.bfabri.loots.utils.CustomItem;
import com.github.bfabri.loots.utils.Utils;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Key implements ConfigurationSerializable {

	@Getter
	@Setter
	protected Material type;

	@Getter
	@Setter
	protected short data;

	protected List<String> lore;

	@Setter
	protected String displayName;

	@Getter
	@Setter
	protected boolean enchanted;

	public Key(Material material, short data, String name, boolean enchanted, List<String> lore) {
		if (material == null) {
			material = Material.TRIPWIRE_HOOK;
		}
		this.type = material;
		this.data = data;
		this.displayName = name;
		this.enchanted = enchanted;
		this.lore = lore;
	}

	public Key(Map<String, Object> map) {
		this.type = Material.valueOf((String) map.get("type"));
		this.data = (short) map.get("data");
		this.displayName = (String) map.get("name");
		this.enchanted = (boolean) map.get("enchanted");
		this.lore = (List<String>) map.get("lore");
	}

	public String getDisplayName() {
		return Utils.translate(this.displayName);
	}

	public List<String> getLore(Player player) {
		ArrayList<String> newLore = new ArrayList<>();
		for (String line : this.lore)
			newLore.add(Utils.translate(line.replace("{player}", (player != null) ? player.getDisplayName() : "Console")));
		return newLore;
	}
	@Override
	public Map<String, Object> serialize() {
		LinkedHashMap<String, Object> map = Maps.newLinkedHashMap();
		map.put("type", this.type.name());
		map.put("data", this.data);
		map.put("name", this.displayName);
		map.put("enchanted", this.enchanted);
		map.put("lore", this.lore);
		return map;
	}
}
