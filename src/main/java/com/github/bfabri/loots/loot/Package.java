package com.github.bfabri.loots.loot;

import com.github.lfabril.loots.Loots;
import java.util.ArrayList;
import org.bukkit.Bukkit;

public class Package {
  protected String name;
  
  public String getName() {
    return this.name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  protected ArrayList<PackageRewards> rewards = new ArrayList<>();
  
  public ArrayList<PackageRewards> getRewards() {
    return this.rewards;
  }
  
  public Package(String name) {
    this.name = name;
    loadPackages();
  }
  
  public void loadPackages() {
    Loots loots = Loots.getInstance();
    if (!loots.getLootsConfig().isSet("Packages." + this.name + ".items"))
      return; 
    if (loots.getLootsConfig().getConfigurationSection("Packages." + this.name + ".items") != null)
      loots.getLootsConfig().getConfigurationSection("Packages." + this.name + ".items").getKeys(false).forEach(id -> {
            String path = "Packages." + this.name + ".items." + id;
            PackageRewards reward = new PackageRewards(path, loots);
            if (reward.isValid()) {
              this.rewards.add(reward);
            } else {
              Bukkit.getLogger().warning(path + " is an invalid rewards.");
            } 
          }); 
  }
  
  public ArrayList<PackageRewards> getAllRewards() {
    ArrayList<PackageRewards> lootRewardsList = new ArrayList<>();
    for (PackageRewards lootRewards : getRewards())
      lootRewardsList.add(lootRewards); 
    return lootRewardsList;
  }
}
