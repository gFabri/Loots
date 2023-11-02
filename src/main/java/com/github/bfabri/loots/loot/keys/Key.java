package com.github.bfabri.loots.loot.keys;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Key {
  private final LootKey lootKey;
  
  private final Material material;
  
  private final short data;
  
  private final String name;
  
  private List<String> lore;
  
  private final boolean enchanted;
  
  private Player giver;
  
  public Player getGiver() {
    return this.giver;
  }
  
  public void setGiver(Player giver) {
    this.giver = giver;
  }
  
  public Key(LootKey loot, Material material, short data, String name, boolean enchanted, List<String> lore) {
    this.lootKey = loot;
    if (material == null)
      material = Material.TRIPWIRE_HOOK; 
    this.material = material;
    this.data = data;
    this.name = name;
    this.enchanted = enchanted;
    this.lore = lore;
  }
  
  public Material getMaterial() {
    return this.material;
  }
  
  public short getData() {
    return this.data;
  }
  
  public String getName() {
    return ChatColor.translateAlternateColorCodes('&', this.name);
  }
  
  public List<String> getLore() {
    ArrayList<String> newLore = new ArrayList<>();
    for (String line : this.lore)
      newLore.add(ChatColor.translateAlternateColorCodes('&', line.replace("%player%", (getGiver() != null) ? getGiver().getDisplayName() : "Console"))); 
    return newLore;
  }
  
  public boolean isEnchanted() {
    return this.enchanted;
  }
  
  public ItemStack getKey(Integer amount, CommandSender sender) {
    if (sender instanceof Player) {
      setGiver((Player)sender);
    } else {
      setGiver(null);
    } 
    ItemStack keyItem = new ItemStack(getMaterial(), amount.intValue(), getData());
    if (isEnchanted())
      keyItem.addUnsafeEnchantment(Enchantment.DURABILITY, 10); 
    ItemMeta keyItemMeta = keyItem.getItemMeta();
    String title = getName().replaceAll("%type%", getLootKey().getName());
    keyItemMeta.setDisplayName(title);
    keyItemMeta.setLore(getLore());
    keyItem.setItemMeta(keyItemMeta);
    return keyItem;
  }
  
  public LootKey getLootKey() {
    return this.lootKey;
  }
}
