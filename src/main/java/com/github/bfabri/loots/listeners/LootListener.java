package com.github.bfabri.loots.listeners;

import com.github.bfabri.loots.ConfigHandler;
import com.github.bfabri.loots.Loots;
import com.github.bfabri.loots.loot.Loot;
import com.github.bfabri.loots.loot.Rewards;
import com.github.bfabri.loots.loot.key.Key;
import com.github.bfabri.loots.utils.Utils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.Map;

public class LootListener implements Listener {

  @EventHandler
  public void onItemDrop(PlayerDropItemEvent e) {
    ItemStack item = e.getItemDrop().getItemStack();
    if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().contains("Loot")) {
      e.getPlayer().setItemInHand(new ItemStack(Material.AIR));
      e.getPlayer().playSound(e.getPlayer().getLocation(), Utils.isOldVersion() ? Sound.valueOf("ITEM_BREAK") : Sound.valueOf("ENTITY_ITEM_BREAK"), 1.0F, 1.0F);
      e.setCancelled(true);
    }
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
  public void onBlockPlace(BlockPlaceEvent event) {
    Player player = event.getPlayer();

    Loots.getInstance().getLootInterface().getLoots().values().forEach(loot -> {
      Key key = loot.getKey();
      String title = key.getDisplayName();

      if (player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().hasDisplayName() && Utils.translate(player
              .getItemInHand().getItemMeta().getDisplayName()).equalsIgnoreCase(Utils.translate(title.replace("{type}", loot.getDisplayName())))) {
        event.getPlayer().sendMessage(Utils.translate(ConfigHandler.Configs.LANG.getConfig().getString("CAN_NOT_PLACE_KEYS")));
        event.setCancelled(true);
      }
    });

    if (player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().hasDisplayName() && player.getItemInHand().getItemMeta().getDisplayName().contains("Loot")) {
      Loot loot = Loots.getInstance().getLootInterface().getLoot(ChatColor.stripColor(player.getItemInHand().getItemMeta().getDisplayName().trim().replace("Loot", "").replace(" ", "")));
      if (loot != null) {
        loot.getLocations().add(event.getBlock().getLocation().serialize());
        event.getBlock().setMetadata("loot", new FixedMetadataValue(Loots.getInstance(), loot.getName()));
        Loots.getInstance().getLootInterface().saveLoots();
        Loots.getInstance().getHologramInit().getHologramPlugin().getHologram().createHologram(event.getBlock().getLocation(), loot, (ArrayList<String>) ConfigHandler.Configs.LANG.getConfig().getStringList("HOLOGRAMS.lines"));
      } else {
        player.sendMessage(ChatColor.RED + "An error occured. Please contact an administrator.");
      }
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onBlockBreak(BlockBreakEvent event) {
    if (event.getPlayer().hasPermission("loots.editable")) {
      if (event.getBlock().hasMetadata("loot")) {
        String lootType = event.getBlock().getMetadata("loot").get(0).asString();
        Loot loot = Loots.getInstance().getLootInterface().getLoots().get(lootType);
        if (loot == null) {
          return;
        }
        if (!event.getPlayer().isSneaking()) {
          event.setCancelled(true);
          Loots.getInstance().getInventoryConfigListener().openConfigInventory(event.getPlayer(), loot);
        }
        if (event.getPlayer().isSneaking()) {
          event.getBlock().removeMetadata("loot", Loots.getInstance());
          loot.getLocations().remove(event.getBlock().getLocation().serialize());
          Loots.getInstance().getLootInterface().saveLoots();
          Loots.getInstance().getHologramInit().getHologramPlugin().getHologram().removeHologram(event.getBlock().getLocation(), loot);
        }
      }
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    if (event.getClickedBlock() == null || event.getClickedBlock().getType() == Material.AIR) {
      return;
    }
    if (!event.getClickedBlock().hasMetadata("loot") || event.getClickedBlock().getMetadata("loot").isEmpty()) {
      return;
    }
    Loot loot = Loots.getInstance().getLootInterface().getLoot(event.getClickedBlock().getMetadata("loot").get(0).asString());
    if (loot == null) {
      return;
    }

    String title = loot.getKey().getDisplayName();
    if (event.getAction().toString().contains("LEFT")) {
      if (player.isSneaking()) {
        return;
      }
      if (player.getGameMode() == GameMode.CREATIVE) {
        return;
      }
      event.setCancelled(true);
      Inventory preview = Bukkit.createInventory(null, loot.getSize(), Utils.translate(loot.getDisplayName()));
      preview.clear();
      loot.getRewards().forEach((integer, itemStack) -> preview.setItem(integer, Rewards.deserialize(itemStack)));
      player.openInventory(preview);
    } else if (event.getAction().toString().contains("RIGHT") && player.getGameMode() == GameMode.CREATIVE && player.hasPermission("loot.editable")) {
      event.setCancelled(true);
      Inventory edit = Bukkit.createInventory(null, loot.getSize(), Utils.translate(loot.getDisplayName()));
      edit.clear();
      loot.getRewards().forEach((integer, itemStack) -> edit.setItem(integer, Rewards.deserialize(itemStack)));
      player.openInventory(edit);
    } else if (player.getItemInHand() != null && player.getItemInHand().hasItemMeta() && !player.getItemInHand().getType().equals(Material.AIR) && player.getItemInHand().getItemMeta().getDisplayName() != null && Utils.translate(player.getItemInHand().getItemMeta().getDisplayName()).equals(Utils.translate(title.replace("{type}", loot.getDisplayName())))) {
      event.setCancelled(true);
      if (player.getInventory().firstEmpty() == -1) {
        player.sendMessage(ChatColor.RED + "You inventory is full");
        return;
      }
      if (loot.getKey().isEnchanted() && player.getItemInHand().hasItemMeta() && !player.getItemInHand().getItemMeta().hasEnchant(Enchantment.DURABILITY)) {
        return;
      }
      if (player.getItemInHand().getAmount() > 1) {
        player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
      } else {
        player.setItemInHand(null);
      }
      loot.handle(player);
    } else {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent e) {
    Player player = (Player)e.getWhoClicked();
    if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {
      return;
    }
    if (e.getInventory().getType() != InventoryType.CHEST) {
      return;
    }
    Loot loot = Loots.getInstance().getLootInterface().getLoot(ChatColor.stripColor(e.getView().getTitle().trim().replace("Loot", "").replace(" ", "")));
    if (loot != null && e.getView().getTitle().equalsIgnoreCase(Utils.translate(loot.getDisplayName()))) {
      if (player.getGameMode() == GameMode.CREATIVE && player.hasPermission("loot.edit"))
        return;

      Rewards rewards = new Rewards(loot.getRewards().get(e.getSlot()));

      if (rewards.isUsePackage() && player.getGameMode() == GameMode.SURVIVAL) {
        Inventory pack = Bukkit.createInventory(null, 27, Utils.translate("&ePreview"));
        player.openInventory(pack);
      }
      e.setCancelled(true);
    } else if (e.getView().getTitle().contains("Preview")) {
      e.setCancelled(true);
    }
  }
}
