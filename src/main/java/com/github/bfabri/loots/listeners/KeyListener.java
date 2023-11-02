package com.github.bfabri.loots.listeners;

import com.github.lfabril.loots.Loots;
import com.github.lfabril.loots.loot.Loot;
import com.github.lfabril.loots.loot.keys.Key;
import com.github.lfabril.loots.loot.keys.LootKey;
import com.github.lfabril.loots.utils.LootUtils;
import com.github.lfabril.loots.utils.Util;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class KeyListener implements Listener {
  @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
  public void onBlockPlace(BlockPlaceEvent event) {
    Player player = event.getPlayer();
    for (Map.Entry<String, Loot> loot : (Iterable<Map.Entry<String, Loot>>)LootUtils.getLoots().entrySet()) {
      if (!(loot.getValue() instanceof LootKey))
        continue; 
      LootKey keyloot = (LootKey)loot.getValue();
      Key key = keyloot.getKey();
      if (key == null)
        continue; 
      String title = key.getName();
      if (player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().hasDisplayName() && player
        .getItemInHand().getItemMeta().getDisplayName().contains(title)) {
        event.getPlayer().sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.cant-place-keys")));
        event.setCancelled(true);
        return;
      } 
    } 
  }
}
