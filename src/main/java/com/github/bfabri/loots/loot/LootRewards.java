package com.github.bfabri.loots.loot;

import com.github.lfabril.loots.Loots;
import com.github.lfabril.loots.utils.LootUtils;
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

public class LootRewards {
  private boolean valid = false;
  
  private final Loots loots;
  
  private boolean command = false;
  
  public boolean isCommand() {
    return this.command;
  }
  
  private boolean packages = false;
  
  public boolean isPackages() {
    return this.packages;
  }
  
  private double percentage = 0.0D;
  
  public double getPercentage() {
    return this.percentage;
  }
  
  public void setPercentage(double percentage) {
    this.percentage = percentage;
  }
  
  private List<String> commands = new ArrayList<>();
  
  private String pack;
  
  private ItemStack itemStack;
  
  public List<String> getCommands() {
    return this.commands;
  }
  
  public String getPack() {
    return this.pack;
  }
  
  public LootRewards(String path, Loots loots) {
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
    if (config.isSet(path + ".percentage"))
      this.percentage = config.getInt(path + ".percentage"); 
    int amount = 1;
    if (config.isSet(path + ".amount"))
      amount = config.getInt(path + ".amount"); 
    ItemStack itemStack = new ItemStack(itemType, amount, (short)itemData);
    if (config.isSet(path + ".commands") && config.getStringList(path + ".commands").size() != 0) {
      this.command = true;
      this.commands = config.getStringList(path + ".commands");
    } 
    if (config.isSet(path + ".package")) {
      this.packages = true;
      this.pack = config.getString(path + ".package");
    } 
    ItemMeta itemStackMeta = itemStack.getItemMeta();
    if (config.isSet(path + ".lore")) {
      List<String> lines = config.getStringList(path + ".lore");
      List<String> lore = new ArrayList<>();
      for (String line : lines)
        lore.add(Util.translate(line)); 
      itemStackMeta.setLore(lore);
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
    this.itemStack = itemStack;
    this.valid = true;
  }
  
  public boolean isValid() {
    return this.valid;
  }
  
  public ItemStack getItemStack() {
    return this.itemStack;
  }
  
  public ItemStack runWin(Player player) {
    LootRewards lootRewards = this;
    if (isCommand() && getCommands().size() > 0) {
      Bukkit.getScheduler().runTask((Plugin)this.loots, () -> runCommands(player));
    } else if (isPackages()) {
      if (LootUtils.getPackage(this.pack.toLowerCase()) == null) {
        Loots.getInstance().getLogger().warning("Invalid Package " + this.pack.toLowerCase());
        return lootRewards.getItemStack();
      } 
      dispatchPackage(player, LootUtils.getPackage(this.pack.toLowerCase()));
    } else if (!isCommand()) {
      return lootRewards.getItemStack();
    } 
    return null;
  }
  
  private void runCommands(Player player) {
    getCommands().forEach(command -> Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), command.replaceAll("%player%", player.getName())));
  }
  
  private void dispatchPackage(Player player, Package pack) {
    pack.getAllRewards().forEach(items -> items.runWin(player));
  }
}
