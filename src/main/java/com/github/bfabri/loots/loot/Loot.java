package com.github.bfabri.loots.loot;

import com.github.lfabril.loots.Loots;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class Loot {
  protected String name;
  
  protected String displayName;
  
  public String getName() {
    return this.name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getDisplayName() {
    return this.displayName;
  }
  
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }
  
  protected Material block = Material.CHEST;
  
  public Material getBlock() {
    return this.block;
  }
  
  public void setBlock(Material block) {
    this.block = block;
  }
  
  protected int size = 27;
  
  public int getSize() {
    return this.size;
  }
  
  public void setSize(int size) {
    this.size = size;
  }
  
  protected ArrayList<LootRewards> rewards = new ArrayList<>();
  
  public ArrayList<LootRewards> getRewards() {
    return this.rewards;
  }
  
  protected HashMap<Integer, LootRewards> rewardsANDslot = new HashMap<>();
  
  public HashMap<Integer, LootRewards> getRewardsANDslot() {
    return this.rewardsANDslot;
  }
  
  protected double totalPercentage = 0.0D;
  
  public double getTotalPercentage() {
    return this.totalPercentage;
  }
  
  public Loot(String name) {
    this.name = name;
    loadLootBase();
  }
  
  public void loadLootBase() {
    Loots loots = Loots.getInstance();
    if (loots.getLootsConfig().isSet("Loots." + this.name + ".displayName"))
      this.displayName = loots.getLootsConfig().getString("Loots." + this.name + ".displayName"); 
    if (loots.getLootsConfig().isSet("Loots." + this.name + ".type"))
      this.block = Material.valueOf(((String)Objects.<String>requireNonNull(loots.getLootsConfig().getString("Loots." + this.name + ".type"))).toUpperCase()); 
    if (loots.getLootsConfig().isSet("Loots." + this.name + ".type"))
      this.block = Material.valueOf(((String)Objects.<String>requireNonNull(loots.getLootsConfig().getString("Loots." + this.name + ".type"))).toUpperCase()); 
    if (loots.getLootsConfig().isSet("Loots." + this.name + ".size")) {
      int size = loots.getLootsConfig().getInt("Loots." + this.name + ".size");
      if (size > 54 || size < 9) {
        this.size = 9;
      } else {
        this.size = size;
      } 
    } 
    if (!loots.getLootsConfig().isSet("Loots." + this.name + ".Rewards"))
      return; 
    if (loots.getLootsConfig().getConfigurationSection("Loots." + this.name + ".Rewards") != null) {
      this.totalPercentage = 0.0D;
      ((ConfigurationSection)Objects.<ConfigurationSection>requireNonNull(loots.getLootsConfig().getConfigurationSection("Loots." + this.name + ".Rewards"))).getKeys(false).forEach(id -> {
            String path = "Loots." + this.name + ".Rewards." + id;
            LootRewards reward = new LootRewards(path, loots);
            if (this.totalPercentage + reward.getPercentage() < 100.0D) {
              if (reward.isValid()) {
                this.totalPercentage += reward.getPercentage();
                this.rewards.add(reward);
                this.rewardsANDslot.put(Integer.valueOf(Integer.parseInt(id)), reward);
              } else {
                Bukkit.getLogger().warning(path + " is an invalid rewards.");
              } 
            } else {
              Bukkit.getLogger().warning("Disabled Rewards from Loot " + this.name + ", Your percentages must NOT add up to more than 100%");
            } 
          });
    } 
  }
  
  public LootRewards handle(Player player) {
    return handle(player, null);
  }
  
  public LootRewards handle(Player player, LootRewards reward) {
    if (reward == null)
      reward = getRandomReward(); 
    ItemStack itemStack = reward.runWin(player);
    if (itemStack != null && itemStack.getType() == (isOldVersion() ? Material.valueOf("STAINED_GLASS_PANE") : Material.valueOf("LEGACY_STAINED_GLASS_PANE"))) {
      handle(player);
    } else if (itemStack != null) {
      player.getInventory().addItem(new ItemStack[] { itemStack });
    } 
    return reward;
  }
  
  public ArrayList<LootRewards> getAllRewards() {
    ArrayList<LootRewards> lootRewardsList = new ArrayList<>(getRewards());
    return lootRewardsList;
  }
  
  public LootRewards getRandomReward() {
    LootRewards reward;
    if (getTotalPercentage() > 0.0D) {
      List<LootRewards> lootRewardsList = getAllRewards();
      double totalWeight = 0.0D;
      for (LootRewards lootRewards : lootRewardsList)
        totalWeight += lootRewards.getPercentage(); 
      int randomIndex = -1;
      double random = Math.random() * totalWeight;
      for (int i = 0; i < this.rewards.size(); i++) {
        random -= ((LootRewards)this.rewards.get(i)).getPercentage();
        if (random <= 0.0D) {
          randomIndex = i;
          break;
        } 
      } 
      reward = this.rewards.get(randomIndex);
    } else {
      reward = getAllRewards().get(randInt(0, getRewards().size() - 1));
    } 
    return reward;
  }
  
  public boolean isOldVersion() {
    String[] split = Bukkit.getBukkitVersion().split("\\.");
    String serverVersion = split[0] + "_" + split[1] + "_R" + split[3].split("\\-")[0];
    return (!serverVersion.startsWith("1_7") || !serverVersion.startsWith("1_8"));
  }
  
  public int randInt(int min, int max) {
    return (new Random()).nextInt(max - min + 1) + min;
  }
}
