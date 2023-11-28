package com.github.bfabri.loots.listeners;

import com.github.bfabri.loots.Loots;
import com.github.bfabri.loots.loot.Loot;
import com.github.bfabri.loots.loot.Rewards;
import com.github.bfabri.loots.utils.CustomItem;
import com.github.bfabri.loots.utils.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryConfigListener implements Listener {

	@Getter
	private Inventory configInventory;

	public final HashMap<Player, String> chatConfig = new HashMap<>();

	public void openConfigInventory(Player player, Loot loot) {
		this.configInventory =  Bukkit.createInventory(null, 36, Utils.translate("&eEditing loot &6" + loot.getName()));

		configInventory.setItem(1, new CustomItem(loot.getBlockType(), 1, 0).setName("&7> &eIcon &c(Drag to change) &7<").create());
		configInventory.setItem(3, new CustomItem(Material.ENDER_CHEST, 1, 0).setName("&eSize&7: &c" + loot.getSize()).create());
		configInventory.setItem(5, new CustomItem(loot.getKey().getType(), 1, 0).setName("&eKey attributes").addLore("", "&7> &eDisplayName&7: " + loot.getKey().getDisplayName(),
				"&eEnchanted&7: " + loot.getKey().isEnchanted(),
				" ", "&eShift click to change option", "&aLeft click to modify").addEnchantment(loot.getKey().isEnchanted(), Enchantment.DURABILITY, 10).create());
		configInventory.setItem(7, new CustomItem(Material.NAME_TAG, 1, 0).setName("&eDisplayName&7: " + loot.getDisplayName()).create());
		configInventory.setItem(19, new CustomItem(Material.LEVER, 1, 0).setName("&eHolograms&7: &c" + loot.isHologram()).create());
		configInventory.setItem(21, new CustomItem(Material.LEVER, 1, 0).setName("&eItems Holograms&7: &c" + loot.isItemInHologram()).create());
		configInventory.setItem(23, new CustomItem(Material.valueOf(loot.getItemType()), 1, 0).setName("&7> &eItem Hologram &c(Drag to change) &7<").create());
		configInventory.setItem(25, new CustomItem(Utils.getMaterialByVersion("MINECART"), 1, 0).setName("&7> &bRewards Settings &7<").create());

		player.openInventory(configInventory);
	}

	@EventHandler
	private void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();

		if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta() || !event.getCurrentItem().getItemMeta().hasDisplayName()) {
			return;
		}

		if (event.getView().getTitle().startsWith(Utils.translate("&eEditing loot "))) {
			event.setCancelled(true);
			Loot loot = Loots.getInstance().getLootInterface().getLoot(ChatColor.stripColor(event.getView().getTitle().trim().replace("Editing loot", "").replace(" ", "")));
			if (event.getSlot() == 1) {
				if (event.getCursor() != null && event.getCursor().getType() != Material.AIR) {
					ItemMeta meta = event.getCurrentItem().getItemMeta();
					event.getCursor().setItemMeta(meta);
					event.setCurrentItem(event.getCursor());
					loot.setBlockType(event.getCursor().getType());
					event.setCursor(null);
					Loots.getInstance().getLootInterface().saveLoot(loot);
					loot.getLocations().forEach(location -> Location.deserialize(location).getBlock().setType(loot.getBlockType()));
					configInventory.setItem(1, new CustomItem(loot.getBlockType(), 1, 0).setName("&7> &eIcon &c(Drag to change) &7<").create());
				}
			} else if (event.getSlot() == 3) {
				chatConfig.put(player, "size:" + loot.getName());
				player.closeInventory();
				player.sendMessage(Utils.translate("&ePlease type size"));
			} else if (event.getSlot() == 5) {

				if (event.getCursor() != null && event.getCursor().getType() != Material.AIR) {
					ItemMeta meta = event.getCurrentItem().getItemMeta();
					event.getCursor().setItemMeta(meta);
					event.setCurrentItem(event.getCursor());
					loot.getKey().setType(event.getCursor().getType());
					loot.getKey().setData(event.getCursor().getData().getData());
					event.setCursor(null);
					Loots.getInstance().getLootInterface().saveLoot(loot);
					configInventory.setItem(5, new CustomItem(loot.getKey().getType(), 1, 0).setName("&eKey attributes").addLore("", "&7> &eDisplayName&7: " + loot.getKey().getDisplayName(),
							"&eEnchanted&7: " + loot.getKey().isEnchanted(),
							" ", "&eShift click to change option", "&aLeft click to modify").addEnchantment(loot.getKey().isEnchanted(), Enchantment.DURABILITY, 10).create());
				}

				ItemMeta meta = event.getCurrentItem().getItemMeta();
				List<String> lore = event.getCurrentItem().getItemMeta().getLore();

				String displayName = lore.get(1);
				String enchanted = lore.get(2);

				if (event.isShiftClick() && event.isLeftClick()) {
					if (displayName.contains("»")) {
						lore.set(1, Utils.translate("&eDisplayName&7: " + loot.getKey().getDisplayName()));
						lore.set(2, Utils.translate("&7» &eEnchanted&7: " + loot.getKey().isEnchanted()));
					}  else if (enchanted.contains("»")) {
						lore.set(1, Utils.translate("&7» &eDisplayName&7: " + loot.getKey().getDisplayName()));
						lore.set(2, Utils.translate("&eEnchanted&7: " + loot.getKey().isEnchanted()));
					}
					meta.setLore(lore);
					event.getCurrentItem().setItemMeta(meta);
				} else if (event.isLeftClick()) {
					if (displayName.contains("»")) {
						chatConfig.put(player, "keyDisplayName:" + loot.getName());
						player.closeInventory();
						player.sendMessage(Utils.translate("&ePlease type displayName"));
						lore.set(1, Utils.translate("&7» &eDisplayName&7: " + loot.getKey().getDisplayName()));
					} else if (enchanted.contains("»")) {
						loot.getKey().setEnchanted(!loot.getKey().isEnchanted());
						lore.set(2, Utils.translate("&7» &eEnchanted&7: " + loot.getKey().isEnchanted()));
					}
					if (loot.getKey().isEnchanted()) {
						meta.addEnchant(Enchantment.DURABILITY, 10, true);
					} else {
						meta.removeEnchant(Enchantment.DURABILITY);
					}
					meta.setLore(lore);
					event.getCurrentItem().setItemMeta(meta);
				}
				configInventory.setItem(5, event.getCurrentItem());
			} else if (event.getSlot() == 7) {
				chatConfig.put(player, "displayName:" + loot.getName());
				player.closeInventory();
				player.sendMessage(Utils.translate("&ePlease type displayName"));
			} else if (event.getSlot() == 19) {
				loot.setHologram(!loot.isHologram());
				Loots.getInstance().getLootInterface().saveLoot(loot);
				configInventory.setItem(19, new CustomItem(Material.LEVER, 1, 0).setName("&eHolograms&7: &c" + loot.isHologram()).create());
				Loots.getInstance().getHologramInit().getHologramPlugin().getHologram().removeAllHolograms();
				Loots.getInstance().getHologramInit().getHologramPlugin().getHologram().loadAllHolograms();
			} else if (event.getSlot() == 21) {
				loot.setItemInHologram(!loot.isItemInHologram());
				Loots.getInstance().getLootInterface().saveLoot(loot);
				configInventory.setItem(21, new CustomItem(Material.LEVER, 1, 0).setName("&eItems Holograms&7: &c" + loot.isItemInHologram()).create());
				Loots.getInstance().getHologramInit().getHologramPlugin().getHologram().removeAllHolograms();
				Loots.getInstance().getHologramInit().getHologramPlugin().getHologram().loadAllHolograms();
			} else if (event.getSlot() == 23) {
				if (event.getCursor() != null && event.getCursor().getType() != Material.AIR) {
					ItemMeta meta = event.getCurrentItem().getItemMeta();
					event.getCursor().setItemMeta(meta);
					event.setCurrentItem(event.getCursor());
					loot.setItemType(event.getCursor().getType().toString());
					event.setCursor(null);
					Loots.getInstance().getLootInterface().saveLoot(loot);
					configInventory.setItem(23, new CustomItem(Material.valueOf(loot.getItemType()), 1, 0).setName("&7> &eItem Hologram &c(Drag to change) &7<").create());
					Loots.getInstance().getHologramInit().getHologramPlugin().getHologram().removeAllHolograms();
					Loots.getInstance().getHologramInit().getHologramPlugin().getHologram().loadAllHolograms();
				}
			} else if (event.getSlot() == 25) {
				Inventory rewardsInventory = Loots.getInstance().getRewardsListener().getRewardsInventory();
				rewardsInventory.clear();
					for (int i = 27; i < 34; i++)
						rewardsInventory.setItem(i, (new CustomItem(Utils.getMaterialByVersion("STAINED"), 1, 0)).setName("&c.").create());
						loot.getRewards().forEach((slot, map) -> {
							if (map != null) {
								ItemStack itemStack = Rewards.deserialize(map);
								ItemMeta meta = itemStack.getItemMeta();
								List<String> lore = new ArrayList<>();
								if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore())
									lore = itemStack.getItemMeta().getLore();
								lore.add(" ");
								lore.add(Utils.translate("&eUse commands&7: " + map.get("useCommand")));
								lore.add(" ");
								lore.add(Utils.translate("&aLeft click to add commands on this item"));
								lore.add(Utils.translate("&cRight click to remove commands on this item"));
								lore.add(Utils.translate("&aShift left click to change enable or disable commands on this item"));
								lore.add(Utils.translate("&eMiddle click to view list of commands"));
								meta.setLore(lore);
								itemStack.setItemMeta(meta);
								rewardsInventory.setItem(slot.intValue(), itemStack);
							}
						});
					player.openInventory(rewardsInventory);
			}
		}
	}

	@EventHandler
	private void onChat(AsyncPlayerChatEvent event) {
		if (chatConfig.containsKey(event.getPlayer())) {
			if (event.getMessage().equalsIgnoreCase("cancel") || event.getMessage().equalsIgnoreCase("exit") || event.getMessage().equalsIgnoreCase("quit")) {
				chatConfig.remove(event.getPlayer());
				event.getPlayer().sendMessage(ChatColor.RED + "Operation has been canceled");
				event.setCancelled(true);
			} else if (chatConfig.get(event.getPlayer()).startsWith("size")) {
				event.setCancelled(true);
				Loot loot = Loots.getInstance().getLootInterface().getLoot(chatConfig.get(event.getPlayer()).split(":")[1]);
				try {
					if (isValidSize(Integer.parseInt(event.getMessage()))) {
						loot.setSize(Integer.parseInt(event.getMessage()));
						configInventory.setItem(3, new CustomItem(Material.ENDER_CHEST, 1, 0).setName("&eSize&7: &c" + loot.getSize()).create());
						event.getPlayer().openInventory(configInventory);
						Loots.getInstance().getLootInterface().saveLoot(loot);
					} else {
						event.getPlayer().sendMessage(ChatColor.RED + "Invalid size");
						event.getPlayer().openInventory(configInventory);
					}
				} catch (NumberFormatException ex) {
					event.getPlayer().sendMessage(ChatColor.RED + "Type numbers");
					event.getPlayer().openInventory(configInventory);
				}
				chatConfig.remove(event.getPlayer());
			} else if (chatConfig.get(event.getPlayer()).startsWith("displayName")) {
				event.setCancelled(true);
				Loot loot = Loots.getInstance().getLootInterface().getLoot(chatConfig.get(event.getPlayer()).split(":")[1]);
				loot.setDisplayName(Utils.translate(event.getMessage()));
				configInventory.setItem(7, new CustomItem(Material.NAME_TAG, 1, 0).setName("&eDisplayName&7: " + loot.getDisplayName()).create());
				event.getPlayer().openInventory(configInventory);
				Loots.getInstance().getLootInterface().saveLoot(loot);
				chatConfig.remove(event.getPlayer());
				Loots.getInstance().getHologramInit().getHologramPlugin().getHologram().removeAllHolograms();
				Loots.getInstance().getHologramInit().getHologramPlugin().getHologram().loadAllHolograms();
			} else if (chatConfig.get(event.getPlayer()).startsWith("keyDisplayName")) {
				event.setCancelled(true);
				Loot loot = Loots.getInstance().getLootInterface().getLoot(chatConfig.get(event.getPlayer()).split(":")[1]);
				loot.getKey().setDisplayName(Utils.translate(event.getMessage()));
				configInventory.setItem(5, new CustomItem(loot.getKey().getType(), 1, 0).setName("&eKey attributes").addLore("", "&7> &eDisplayName&7: " + loot.getKey().getDisplayName(),
						"&eEnchanted&7: " + loot.getKey().isEnchanted(),
						" ", "&eShift click to change option", "&aLeft click to modify").addEnchantment(loot.getKey().isEnchanted(), Enchantment.DURABILITY, 10).create());
				event.getPlayer().openInventory(configInventory);
				Loots.getInstance().getLootInterface().saveLoot(loot);
				chatConfig.remove(event.getPlayer());
			}
		}
	}

	public static boolean isValidSize(int size) {
		switch (size) {
			case 9:
			case 18:
			case 27:
			case 36:
			case 45:
			case 54:
				return true;
		}
		return false;
	}
}
