package com.github.bfabri.loots.listeners;

import com.github.bfabri.loots.loot.Rewards;
import com.github.bfabri.loots.utils.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class RewardsListener implements Listener {

	@Getter
	private final Inventory rewardsInventory = Bukkit.createInventory(null, 36, Utils.translate("&7> &bRewards Settings &7<"));

	private boolean editableRewards = false;

	private final HashMap<Player, String> chatConfig = new HashMap<>();
//	@EventHandler
//	private void onClickInRewards(InventoryClickEvent event) {
//		Player player = (Player) event.getWhoClicked();
//		if (event.getCurrentItem() == null)
//			return;
//		if (event.getView().getTitle().equalsIgnoreCase(Utils.translate("&7> &bRewards Settings &7<"))) {
//			event.setCancelled(!this.editableRewards);
//			if (event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName())
//				if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(Utils.translate("&7> &cSave &7<"))) {
//					event.setCancelled(true);
//					HashMap<Integer, Map<String, Object>> item = new HashMap<>();
//					for (int i = 0; i < this.rewardsInventory.getSize(); i++) {
//						if (this.rewardsInventory.getItem(i) != null) {
//							ItemStack itemStack = this.rewardsInventory.getItem(i);
//							if (itemStack.getType() != Material.AIR) {
//								if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName() && itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.translate("&7> &cSave &7<")))
//									continue;
//								if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName() && itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.translate("&c.")))
//									continue;
//								if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName() && itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.translate("&7> &cEdit items &7<")))
//									continue;
//							}
//							Rewards itemWork = new Rewards(itemStack);
//							if (haveConfigLore(itemWork)) {
//								itemWork.getLore().remove(itemWork.getLore().size() - 1);
//								itemWork.getLore().remove(itemWork.getLore().size() - 1);
//								itemWork.getLore().remove(itemWork.getLore().size() - 1);
//								itemWork.getLore().remove(itemWork.getLore().size() - 1);
//								itemWork.getLore().remove(itemWork.getLore().size() - 1);
//								itemWork.getLore().remove(itemWork.getLore().size() - 1);
//								itemWork.getLore().remove(itemWork.getLore().size() - 1);
//							}
//							item.put(Integer.valueOf(i), itemWork.serialize());
//							Loots.getInstance().getRewardsManager().getRewards().put("Rewards" + getTempGameEditing().toUpperCase(), item);
//						} else {
//							item.put(Integer.valueOf(i), null);
//							Loots.getInstance().getRewardsManager().getRewards().put("Rewards" + getTempGameEditing().toUpperCase(), item);
//						}
//						Loots.getInstance().getRewardsManager().save();
//						continue;
//					}
//					player.closeInventory();
//					player.openInventory(this.configInventory);
//				} else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(Utils.translate("&c."))) {
//					event.setCancelled(true);
//				} else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(Utils.translate("&7> &cEdit items &7<"))) {
//					event.setCancelled(true);
//					this.editableRewards = !this.editableRewards;
//					if (this.editableRewards) {
//						for (int i = 0; i < this.rewardsInventory.getSize(); i++) {
//							if (this.rewardsInventory.getItem(i) != null &&
//									this.rewardsInventory.getItem(i).hasItemMeta() && this.rewardsInventory.getItem(i).getItemMeta().hasLore()) {
//								ItemStack stack = this.rewardsInventory.getItem(i);
//								if (stack.getType() != Material.AIR) {
//									if (stack.hasItemMeta() && stack.getItemMeta().hasDisplayName() && stack.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.translate("&7> &cSave &7<")))
//										continue;
//									if (stack.hasItemMeta() && stack.getItemMeta().hasDisplayName() && stack.getItemMeta().getDisplayName().startsWith(Utils.translate("&7> &eAmount of Rewards")))
//										continue;
//									if (stack.hasItemMeta() && stack.getItemMeta().hasDisplayName() && stack.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.translate("&c.")))
//										continue;
//								}
//								ItemMeta meta = stack.getItemMeta();
//								List<String> lore = meta.getLore();
//								if (stack.hasItemMeta() && stack.getItemMeta().hasLore())
//									lore = stack.getItemMeta().getLore();
//								lore.remove(lore.size() - 1);
//								lore.remove(lore.size() - 1);
//								lore.remove(lore.size() - 1);
//								lore.remove(lore.size() - 1);
//								lore.remove(lore.size() - 1);
//								lore.remove(lore.size() - 1);
//								lore.remove(lore.size() - 1);
//								meta.setLore(lore);
//								stack.setItemMeta(meta);
//								this.rewardsInventory.setItem(i, stack);
//							}
//							continue;
//						}
//						this.rewardsInventory.setItem(34, (new CustomItem(GameUtils.getMaterialByVersion("TORCH"), 1, 0)).setName("&7> &cEdit items &7<").create());
//					} else {
//						for (int i = 0; i < this.rewardsInventory.getSize(); i++) {
//							if (this.rewardsInventory.getItem(i) != null) {
//								ItemStack stack = this.rewardsInventory.getItem(i);
//								if (stack.getType() != Material.AIR) {
//									if (stack.hasItemMeta() && stack.getItemMeta().hasDisplayName() && stack.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.translate("&7> &cSave &7<")))
//										continue;
//									if (stack.hasItemMeta() && stack.getItemMeta().hasDisplayName() && stack.getItemMeta().getDisplayName().startsWith(Utils.translate("&7> &eAmount of Rewards")))
//										continue;
//									if (stack.hasItemMeta() && stack.getItemMeta().hasDisplayName() && stack.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.translate("&c.")))
//										continue;
//								}
//								ItemMeta meta = stack.getItemMeta();
//								boolean useCommand = false;
//								if (Hosts.getInstance().getRewardsManager().getRewards().get("Rewards" + getTempGameEditing().toUpperCase()) != null && (
//										(HashMap) Hosts.getInstance().getRewardsManager().getRewards().get("Rewards" + getTempGameEditing().toUpperCase())).get(Integer.valueOf(i)) != null)
//									useCommand = ((Boolean) ((Map) ((HashMap) Hosts.getInstance().getRewardsManager().getRewards().get("Rewards" + getTempGameEditing().toUpperCase())).get(Integer.valueOf(i))).get("useCommand")).booleanValue();
//								List<String> lore = new ArrayList<>();
//								if (stack.hasItemMeta() && stack.getItemMeta().hasLore())
//									lore = stack.getItemMeta().getLore();
//								lore.add(" ");
//								lore.add(Utils.translate("&eUse commands&7: " + useCommand));
//								lore.add(" ");
//								lore.add(Utils.translate("&aLeft click to add commands on this item"));
//								lore.add(Utils.translate("&cRight click to remove commands on this item"));
//								lore.add(Utils.translate("&aShift left click to change enable or disable commands on this item"));
//								lore.add(Utils.translate("&eMiddle click to view list of commands"));
//								meta.setLore(lore);
//								this.rewardsInventory.getItem(i).setItemMeta(meta);
//								this.rewardsInventory.setItem(i, stack);
//							}
//							continue;
//						}
//						this.rewardsInventory.setItem(34, (new CustomItem(Material.TORCH, 1, 0)).setName("&7> &cEdit items &7<").create());
//						Hosts.getInstance().getRewardsManager().save();
//					}
//				}
//			if (!this.editableRewards) {
//				if (event.getCurrentItem().getType() == Material.AIR)
//					return;
//				if (event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName() && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(Utils.translate("&7> &cSave &7<")))
//					return;
//				if (event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName() && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(Utils.translate("&c.")))
//					return;
//				if (event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName() && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(Utils.translate("&7> &cEdit items &7<")))
//					return;
//				if (event.isShiftClick() && event.isLeftClick() && event.getClickedInventory() != player.getInventory()) {
//					if (((HashMap) Hosts.getInstance().getRewardsManager().getRewards().get("Rewards" + getTempGameEditing().toUpperCase())).get(Integer.valueOf(event.getSlot())) == null) {
//						player.sendMessage(ChatColor.RED + "Save first");
//						return;
//					}
//					boolean useCommand = ((Boolean) ((Map) ((HashMap) Hosts.getInstance().getRewardsManager().getRewards().get("Rewards" + getTempGameEditing().toUpperCase())).get(Integer.valueOf(event.getSlot()))).get("useCommand")).booleanValue();
//					((Map<String, Boolean>) ((HashMap) Hosts.getInstance().getRewardsManager().getRewards().get("Rewards" + getTempGameEditing().toUpperCase())).get(Integer.valueOf(event.getSlot()))).put("useCommand", Boolean.valueOf(!useCommand));
//					Hosts.getInstance().getRewardsManager().save();
//					ItemMeta meta = event.getCurrentItem().getItemMeta();
//					List<String> lore = new ArrayList<>();
//					if (event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasLore())
//						lore = event.getCurrentItem().getItemMeta().getLore();
//					int index = 0;
//					for (String lines : lore) {
//						if (lines.startsWith(Utils.translate("&eUse commands")))
//							lore.set(index, Utils.translate("&eUse commands&7: " + (!useCommand ? 1 : 0)));
//						index++;
//					}
//					meta.setLore(lore);
//					event.getCurrentItem().setItemMeta(meta);
//				} else if (event.isLeftClick() && event.getClickedInventory() != player.getInventory()) {
//					if (((HashMap) Hosts.getInstance().getRewardsManager().getRewards().get("Rewards" + getTempGameEditing().toUpperCase())).get(Integer.valueOf(event.getSlot())) == null) {
//						player.sendMessage(ChatColor.RED + "Save first");
//						return;
//					}
//					boolean useCommand = ((Boolean) ((Map) ((HashMap) Hosts.getInstance().getRewardsManager().getRewards().get("Rewards" + getTempGameEditing().toUpperCase())).get(Integer.valueOf(event.getSlot()))).get("useCommand")).booleanValue();
//					if (useCommand) {
//						this.chatConfig.put(player, "commandAddRewardI" + event.getSlot());
//						player.sendMessage(Utils.translate("&ePlease input value"));
//						player.closeInventory();
//					} else {
//						player.sendMessage(ChatColor.RED + "This feature is disabled!");
//					}
//				} else if (event.isRightClick() && event.getClickedInventory() != player.getInventory()) {
//					if (((HashMap) Hosts.getInstance().getRewardsManager().getRewards().get("Rewards" + getTempGameEditing().toUpperCase())).get(Integer.valueOf(event.getSlot())) == null) {
//						player.sendMessage(ChatColor.RED + "Save first");
//						return;
//					}
//					boolean useCommand = ((Boolean) ((Map) ((HashMap) Hosts.getInstance().getRewardsManager().getRewards().get("Rewards" + getTempGameEditing().toUpperCase())).get(Integer.valueOf(event.getSlot()))).get("useCommand")).booleanValue();
//					if (useCommand) {
//						this.chatConfig.put(player, "commandRemoveRewardI" + event.getSlot());
//						player.sendMessage(Utils.translate("&ePlease type index"));
//						player.closeInventory();
//					} else {
//						player.sendMessage(ChatColor.RED + "This feature is disabled!");
//					}
//				} else if (event.getClick().equals(ClickType.MIDDLE)) {
//					if (((HashMap) Hosts.getInstance().getRewardsManager().getRewards().get("Rewards" + getTempGameEditing().toUpperCase())).get(Integer.valueOf(event.getSlot())) == null) {
//						player.sendMessage(ChatColor.RED + "Save first");
//						return;
//					}
//					boolean useCommand = ((Boolean) ((Map) ((HashMap) Hosts.getInstance().getRewardsManager().getRewards().get("Rewards" + getTempGameEditing().toUpperCase())).get(Integer.valueOf(event.getSlot()))).get("useCommand")).booleanValue();
//					if (useCommand) {
//						ArrayList<String> commands = (ArrayList<String>) ((Map) ((HashMap) Hosts.getInstance().getRewardsManager().getRewards().get("Rewards" + getTempGameEditing().toUpperCase())).get(Integer.valueOf(event.getSlot()))).get("commands");
//						player.sendMessage(Utils.translate("&e" + (String) commands.stream().collect(Collectors.joining("&7, &e"))));
//					} else {
//						player.sendMessage(ChatColor.RED + "This feature is disabled!");
//					}
//				}
//			}
//		}
//	}
//
//	@EventHandler
//	private void onChat(AsyncPlayerChatEvent event) {
//		if (this.chatConfig.containsKey(event.getPlayer()))
//			if (event.getMessage().equalsIgnoreCase("cancel") || event.getMessage().equalsIgnoreCase("exit") || event.getMessage().equalsIgnoreCase("quit")) {
//				this.chatConfig.remove(event.getPlayer());
//				event.getPlayer().sendMessage(ChatColor.RED + "Operation has been canceled");
//				event.setCancelled(true);
//			}  else if (((String)this.chatConfig.get(event.getPlayer())).startsWith("commandAddReward")) {
//				event.setCancelled(true);
//				int slot = Integer.parseInt(((String)this.chatConfig.get(event.getPlayer())).split("I")[1]);
//				ArrayList<String> commands = (ArrayList<String>)((Map)((HashMap)Hosts.getInstance().getRewardsManager().getRewards().get("Rewards" + getTempGameEditing().toUpperCase())).get(Integer.valueOf(slot))).get("commands");
//				commands.add(event.getMessage().replace("{player}", event.getPlayer().getName()));
//				Hosts.getInstance().getRewardsManager().save();
//				event.getPlayer().openInventory(this.rewardsInventory);
//				this.chatConfig.remove(event.getPlayer());
//			} else if (((String)this.chatConfig.get(event.getPlayer())).startsWith("commandRemoveReward")) {
//				event.setCancelled(true);
//				int slot = Integer.parseInt(((String)this.chatConfig.get(event.getPlayer())).split("I")[1]);
//				ArrayList<String> commands = (ArrayList<String>)((Map)((HashMap)Hosts.getInstance().getRewardsManager().getRewards().get("Rewards" + getTempGameEditing().toUpperCase())).get(Integer.valueOf(slot))).get("commands");
//				try {
//					commands.remove(Integer.parseInt(event.getMessage()));
//					Hosts.getInstance().getRewardsManager().save();
//					event.getPlayer().openInventory(this.rewardsInventory);
//				} catch (NumberFormatException e) {
//					event.getPlayer().sendMessage(ChatColor.RED + "Type numbers");
//				} catch (IndexOutOfBoundsException e) {
//					event.getPlayer().sendMessage(ChatColor.RED + "Type valid index");
//				}
//				Hosts.getInstance().getRewardsManager().save();
//				this.chatConfig.remove(event.getPlayer());
//			}
//	}
//
//	@EventHandler
//	private void onInventoryClose(InventoryCloseEvent event) {
//		  if (event.getView().getTitle().startsWith(Utils.translate("&7> &bRewards Settings")) && !this.chatConfig.containsKey(event.getPlayer())) {
//			Bukkit.getScheduler().runTaskLater(Hosts.getInstance(), () -> {
//				event.getPlayer().openInventory(this.configInventory);
//				this.editableRewards = false;
//			}, 1L);
//		}
//	}

	private boolean haveConfigLore(Rewards stack) {
		if (stack.getLore() != null && !stack.getLore().isEmpty())
			for (String lines : stack.getLore()) {
				if (lines.startsWith(Utils.translate("&eUse commands&7:")) || lines.startsWith(Utils.translate("&aLeft click to add commands on this item")) || lines
						.startsWith(Utils.translate("&cRight click to remove commands on this item")) || lines.startsWith(Utils.translate("&aShift left click to change enable or disable commands on this item")) || lines.startsWith(Utils.translate("&eMiddle click to view list of commands")))
					return true;
			}
		return false;
	}
}
