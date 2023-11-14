package com.github.bfabri.loots.listeners;

import com.github.bfabri.loots.ConfigHandler;
import com.github.bfabri.loots.Loots;
import com.github.bfabri.loots.loot.Loot;
import com.github.bfabri.loots.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;

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
}
