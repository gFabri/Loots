package com.github.bfabri.loots.loot;

import com.github.bfabri.loots.ConfigHandler;
import com.github.bfabri.loots.Loots;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LootJSON implements LootInterface {

	private final Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
	private final Type type = (new TypeToken<HashMap<String, Loot>>() {

	}).getType();

	@Getter
	private HashMap<String, Loot> loots = new HashMap<>();

	public LootJSON() {
		loadLoots();
	}

	@Override
	public Loot getLoot(String name) {
		return loots.get(name);
	}

	@Override
	public void addLoot(String name) {
		Loot loot = new Loot(name);
		this.loots.put(name, loot);
		saveLoots();
	}

	@Override
	public void deleteLoot(String name) {
		this.loots.remove(name);
		saveLoots();
	}

	public void loadLoots() {
		try {
			HashMap<String, Loot> json = gson.fromJson(new FileReader(ConfigHandler.Configs.LOOTS.getFile()), type);
			if (json != null) {
				loots = json;
			}
		} catch (FileNotFoundException e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Loots file not found");
		}

		getLoots().forEach((name, loot) -> {
			if (loot != null) {
				Iterator<Map<String, Object>> iterator = loot.getLocations().iterator();
				while (iterator.hasNext()) {
					Map<String, Object> s = iterator.next();
					Location location = Location.deserialize(s);
					try {
						Block block = location.getBlock();
						if (block.getType().equals(Material.AIR)) {
							Loots.getInstance().getLogger().warning("No block found at " + location.getBlock().getLocation().serialize() + " removing from storage");
							iterator.remove();
							if (loot.isHologram()) {
								if (loot.itemInHologram) {
									Loots.getInstance().getHologramInit().getHologramPlugin().getHologram().removeHologram(location.add(0.5D, 2.5D, 0.5D), loot);
									continue;
								}
								Loots.getInstance().getHologramInit().getHologramPlugin().getHologram().removeHologram(location.add(0.5D, 1.9D, 0.5D), loot);
								continue;
							}
						}
						block.setMetadata("loot", new FixedMetadataValue(Loots.getInstance(), loot.getName()));
					} catch (Exception ignored) {
					}
				}
			}
		});

		saveLoots();
	}

	@Override
	public void saveLoot(Loot loot) {
		loots.put(loot.getName(), loot);
		saveLoots();
	}

	public void saveLoots() {
		try (FileWriter writer = new FileWriter(ConfigHandler.Configs.LOOTS.getFile())) {
			this.gson.toJson(loots, writer);
		} catch (IOException e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "An error occurred");
		}
	}
}