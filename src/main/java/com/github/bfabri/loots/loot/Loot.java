package com.github.bfabri.loots.loot;

import com.github.bfabri.loots.Loots;
import com.github.bfabri.loots.loot.key.Key;
import com.github.bfabri.loots.utils.CustomItem;
import com.github.bfabri.loots.utils.Utils;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Loot implements ConfigurationSerializable {

	@Getter
	@Setter
	protected String name;

	@Getter
	@Setter
	protected String displayName;

	@Getter
	@Setter
	protected Material blockType;

	@Getter
	@Setter
	protected int size;

	@Getter
	@Setter
	protected double totalPercentage;

	@Getter
	@Setter
	protected boolean hologram;

	@Getter
	@Setter
	protected boolean itemInHologram;

	@Getter
	@Setter
	protected String itemType;

	@Getter
	protected List<Map<String, Object>> locations;

	@Getter
	protected HashMap<Integer, Map<String, Object>> rewards;

	@Getter
	protected Key key;

	public Loot(String name) {
		this.name = name;
		this.displayName = Utils.translate("&7" + name);
		this.blockType = Material.CHEST;
		this.size = 27;
		this.totalPercentage = 0.0D;
		this.rewards = new HashMap<>();
		this.locations = new ArrayList<>();

		ItemStack exampleItem = new CustomItem(Material.DIAMOND_SWORD, 1, 0).setName("&9Example Item").addLore("&7This is an example item!").create();

		Rewards reward = new Rewards(exampleItem);

		reward.setPercentage(100.0D);

		this.rewards.put(0, reward.serialize());

		this.hologram = true;
		this.itemInHologram = false;
		this.itemType = "DIAMOND_SWORD";
		this.key = new Key(Material.TRIPWIRE_HOOK, (short) 0, Utils.translate("{type} &7Key"), true, new ArrayList<>(Arrays.asList("&7Gived by {player}!")));
	}

//	public Loot(Map<String, Object> map) {
//		this.name = (String) map.get("name");
//		this.displayName = (String) map.get("displayName");
//		this.blockType = Material.valueOf((String) map.get("blockType"));
//		this.size = (int) map.get("size");
//		if (this.size > 54 || this.size < 9) {
//			this.size = 9;
//		} else {
//			this.size = (int) map.get("size");
//		}
//		this.totalPercentage = (double) map.get("totalPercentage");
//		this.locations = new ArrayList<>();
//
//		this.hologram = (boolean) map.get("hologram");
//		this.itemInHologram = (boolean) map.get("itemInHologram");
//		this.itemType = (String) map.get("itemType");
//		this.key = (Key) map.get("key");
//
//		this.rewards = (HashMap<Integer, Map<String, Object>>) map.get("rewards");
//
//		if (!this.rewards.isEmpty()) {
//			this.rewards.forEach((integer, reward) -> {
//				Rewards rw = new Rewards(reward);
//				if (this.totalPercentage + rw.getPercentage() < 100.0D) {
//					this.totalPercentage += rw.getPercentage();
//				} else {
//					Bukkit.getLogger().warning("Disabled Rewards from Loot " + this.name + ", Your percentages must NOT add up to more than 100%");
//				}
//			});
//		}
//	}

	@Override
	public Map<String, Object> serialize() {
		LinkedHashMap<String, Object> map = Maps.newLinkedHashMap();
		map.put("name", this.name);
		map.put("displayName", this.displayName);
		map.put("blockType", this.blockType.name());
		map.put("size", this.size);
		map.put("totalPercentage", this.totalPercentage);
		map.put("hologram", this.hologram);
		map.put("itemInHologram", this.itemInHologram);
		map.put("itemType", this.itemType);
		map.put("key", this.key.serialize());
		map.put("locations", this.locations);
		if (rewards != null && rewards.isEmpty()) {
			map.put("rewards", this.rewards);
		}
		return map;
	}

	public ItemStack getKey(Integer amount, CommandSender sender) {
		if (sender instanceof Player) {
			return new CustomItem(key.getType(), amount, key.getData()).setName(getKey().getDisplayName().replace("{type}", getDisplayName())).addLore(key.getLore((Player) sender)).addEnchantment(key.isEnchanted(), Enchantment.DURABILITY, 10).create();
		}
		return new CustomItem(key.getType(), amount, key.getData()).setName(getKey().getDisplayName().replace("{type}", getDisplayName())).addLore(key.getLore(null)).addEnchantment(key.isEnchanted(), Enchantment.DURABILITY, 10).create();
	}

	public ItemStack getLootItem() {
		return new CustomItem(Material.valueOf(blockType.name()), 1, 0).setName("&6" + name + " Loot ").create();
	}

	public Rewards handle(Player player) {
		return handle(player, null);
	}

	public Rewards handle(Player player, Rewards reward) {
		if (reward == null) {
			reward = getRandomRewards();
		}
		ItemStack itemStack = reward.runItems(player, reward);
		if (itemStack != null && itemStack.getType() == (Utils.isOldVersion() ? Material.valueOf("STAINED_GLASS_PANE") : Material.valueOf("LEGACY_STAINED_GLASS_PANE"))) {
			handle(player);
		} else if (itemStack != null) {
			player.getInventory().addItem(itemStack);
		}
		return reward;
	}

	public Rewards getRandomRewards() {
		Rewards reward;
		if (getTotalPercentage() > 0.0D) {
			Collection<Map<String, Object>> lootRewardsList = Loots.getInstance().getLootInterface().getLoot(this.name).getRewards().values();
			double totalWeight = 0.0D;
			for (Map<String, Object> rewards : lootRewardsList) {
				totalWeight += new Rewards(rewards).getPercentage();
			}
			int randomIndex = -1;
			double random = Math.random() * totalWeight;
			for (int i = 0; i < this.rewards.size(); i++) {
				random -= new Rewards(this.rewards.get(i)).getPercentage();
				if (random <= 0.0D) {
					randomIndex = i;
					break;
				}
			}
			reward = new Rewards(this.rewards.get(randomIndex));
		} else {
			reward = new Rewards(this.rewards.get(randInt(0, getRewards().size() - 1)));
		}
		return reward;
	}

	public int randInt(int min, int max) {
		return (new Random()).nextInt(max - min + 1) + min;
	}
}