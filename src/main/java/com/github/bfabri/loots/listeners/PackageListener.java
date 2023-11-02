package com.github.bfabri.loots.listeners;

import com.github.lfabril.loots.ConfigHandler;
import com.github.lfabril.loots.Loots;
import com.github.lfabril.loots.loot.Package;
import com.github.lfabril.loots.utils.LootUtils;
import com.github.lfabril.loots.utils.Util;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class PackageListener implements Listener {
  @EventHandler
  public void onClosePackage(InventoryCloseEvent event) {
    Player player = (Player)event.getPlayer();
    Package pack = LootUtils.getPackage(ChatColor.stripColor(event.getView().getTitle().toLowerCase()));
    if (pack == null || !event.getView().getTitle().equalsIgnoreCase(Util.translate("&6" + pack.getName().toUpperCase())))
      return; 
    if (!player.hasPermission("loot.edit"))
      return; 
    for (int i = 0; i < event.getInventory().getSize(); i++) {
      String path = "Packages." + pack.getName() + ".items." + i;
      if (event.getInventory().getItem(i) != null) {
        ItemStack itemStack = event.getInventory().getItem(i);
        Loots.getInstance().getLootsConfig().set(path + ".type", itemStack.getType().name());
        Loots.getInstance().getLootsConfig().set(path + ".short", Short.valueOf(itemStack.getDurability()));
        Loots.getInstance().getLootsConfig().set(path + ".amount", Integer.valueOf(itemStack.getAmount()));
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore()) {
          Loots.getInstance().getLootsConfig().set(path + ".lore", itemStack.getItemMeta().getLore());
        } else {
          Loots.getInstance().getLootsConfig().set(path + ".lore", null);
        } 
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
          Loots.getInstance().getLootsConfig().set(path + ".displayName", itemStack.getItemMeta().getDisplayName());
        } else {
          Loots.getInstance().getLootsConfig().set(path + ".displayName", "NONE");
        } 
        List<String> enchants = new ArrayList<>();
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasEnchants()) {
          itemStack.getEnchantments().forEach((enchant, level) -> enchants.add(enchant.getName().toUpperCase() + ":" + level));
          Loots.getInstance().getLootsConfig()
            .set(path + ".enchantments", enchants);
        } else {
          Loots.getInstance().getLootsConfig()
            .set(path + ".enchantments", null);
        } 
      } else {
        Loots.getInstance().getLootsConfig().set(path, null);
      } 
      ConfigHandler.Configs.LOOTS.saveConfig();
    } 
    pack.getRewards().clear();
    pack.loadPackages();
    player.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.package-update")));
  }
}
