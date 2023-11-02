package com.github.bfabri.loots.loot;

import com.github.lfabril.loots.Loots;
import com.github.lfabril.loots.utils.Util;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class PackageRewards {
  private boolean valid = false;
  
  private final Loots loots;
  
  private boolean command = false;
  
  public boolean isCommand() {
    return this.command;
  }
  
  private List<String> commands = new ArrayList<>();
  
  public List<String> getCommands() {
    return this.commands;
  }
  
  private List<String> packs = new ArrayList<>();
  
  public List<String> getPacks() {
    return this.packs;
  }
  
  private final List<String> lore = new ArrayList<>();
  
  private ItemStack itemStack;
  
  public PackageRewards(String path, Loots loots) {
    this.loots = loots;
    FileConfiguration config = loots.getLootsConfig();
    if (!config.isSet(path))
      return; 
    if (!config.isSet(path + ".type"))
      return; 
    Material itemType = null;
    if (config.isSet(path + ".type"))
      itemType = Material.getMaterial(config.getString(path + ".type").toUpperCase()); 
    if (itemType == null)
      return; 
    int itemData = 0;
    if (config.isSet(path + ".short"))
      itemData = config.getInt(path + ".short"); 
    int amount = 1;
    if (config.isSet(path + ".amount"))
      amount = config.getInt(path + ".amount"); 
    ItemStack itemStack = new ItemStack(itemType, amount, (short)itemData);
    if (config.isSet(path + ".commands") && config.getStringList(path + ".commands").size() != 0) {
      this.command = true;
      this.commands = config.getStringList(path + ".commands");
    } 
    ItemMeta itemStackMeta = itemStack.getItemMeta();
    if (config.isSet(path + ".lore")) {
      List<String> lines = config.getStringList(path + ".lore");
      for (String line : lines)
        this.lore.add(Util.translate(line)); 
      itemStackMeta.setLore(this.lore);
    } 
    if (config.isSet(path + ".displayName") && !config.getString(path + ".displayName").equals("NONE"))
      itemStackMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString(path + ".displayName"))); 
    if (config.isSet(path + ".enchantments")) {
      List<?> enchtantments = config.getList(path + ".enchantments");
      for (Object object : enchtantments) {
        String enchantment = (String)object;
        String[] args = enchantment.split(":");
        int level = 1;
        if (args.length > 1)
          level = Integer.parseInt(args[1]); 
        Enchantment enchant = Util.getEnchantmentFromNiceName(args[0].toUpperCase());
        if (enchant == null) {
          Bukkit.getLogger().warning("Invalid enchantment " + args[0].toUpperCase());
          continue;
        } 
        itemStackMeta.addEnchant(enchant, level, true);
      } 
    } 
    itemStack.setItemMeta(itemStackMeta);
    this.itemStack = itemStack.clone();
    this.valid = true;
  }
  
  public boolean isValid() {
    return this.valid;
  }
  
  public ItemStack getItemStack() {
    return this.itemStack;
  }
  
  public void runWin(Player player) {
    if (isCommand() && getCommands().size() > 0) {
      Bukkit.getScheduler().runTask((Plugin)this.loots, () -> runCommands(player));
    } else {
      player.getInventory().addItem(new ItemStack[] { getItemStack() });
    } 
  }
  
  private void runCommands(Player player) {
    getCommands().forEach(command -> Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), command.replaceAll("%player%", player.getName())));
  }
}
