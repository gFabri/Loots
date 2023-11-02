package com.github.bfabri.loots.loot.keys;

import com.github.lfabril.loots.Loots;
import com.github.lfabril.loots.loot.Loot;
import com.github.lfabril.loots.utils.LootUtils;
import com.github.lfabril.loots.utils.Util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;

public class LootKey extends Loot {
  protected Key key;
  
  protected Loots loots;
  
  public Key getKey() {
    return this.key;
  }
  
  public void setKey(Key key) {
    this.key = key;
  }
  
  protected HashMap<String, Location> locations = new HashMap<>();
  
  public HashMap<String, Location> getLocations() {
    return this.locations;
  }
  
  public LootKey(String name) {
    super(name);
    this.loots = Loots.getInstance();
    loadLoot();
  }
  
  protected void loadLoot() {
    if (!this.loots.getLootsConfig().isSet("Loots." + this.name + ".Key") || 
      !this.loots.getLootsConfig().isSet("Loots." + this.name + ".Key.type") || 
      !this.loots.getLootsConfig().isSet("Loots." + this.name + ".Key.displayName") || 
      !this.loots.getLootsConfig().isSet("Loots." + this.name + ".Key.enchanted"))
      return; 
    this
      
      .key = new Key(this, Material.valueOf(this.loots.getLootsConfig().getString("Loots." + this.name + ".Key.type")), (short)this.loots.getLootsConfig().getInt("Loots." + this.name + ".Key.short", 0), this.loots.getLootsConfig().getString("Loots." + this.name + ".Key.displayName").replaceAll("%type%", getName()), this.loots.getLootsConfig().getBoolean("Loots." + this.name + ".Key.enchanted"), this.loots.getLootsConfig().getStringList("Loots." + this.name + ".Key.lore"));
  }
  
  public void addLocation(String string, Location location) {
    this.locations.put(string, location);
  }
  
  public Location getLocation(String key) {
    return this.locations.get(key);
  }
  
  public Location removeLocation(String key) {
    return this.locations.remove(key);
  }
  
  public void loadHolograms(Location location) {
    if (LootUtils.getHolograms(this.name) == null || LootUtils.getHolograms(this.name).isEmpty())
      return; 
    ArrayList<String> list = new ArrayList<>();
    for (String line : LootUtils.getHolograms(this.name))
      list.add(Util.translate(line.replace("%loot%", getDisplayName().replace("_", " ")))); 
    this.loots.getHologramUtils().getHologramPlugin().getHologram().create(location, this, list);
  }
  
  public void removeHolograms(Location location) {
    this.loots.getHologramUtils().getHologramPlugin().getHologram().remove(location, this);
  }
  
  public void removeFromConfig(Location location) {
    if (this.loots.getSqlUtils().existLoot(getName())) {
      List<String> locations = this.loots.getSqlUtils().getLocations(getName());
      if (locations.contains(Util.getStringByLocation(location))) {
        locations.remove(Util.getStringByLocation(location));
        this.loots.getSqlUtils().removeLocation(Util.getStringByLocation(location));
      } 
    } 
  }
  
  public void addToConfig(Location location) {
    List<String> locations = new ArrayList<>();
    if (this.loots.getSqlUtils().existLoot(getName()))
      locations = this.loots.getSqlUtils().getLocations(getName()); 
    locations.add(Util.getStringByLocation(location));
    this.loots.getSqlUtils().addLocation(getName(), Util.getStringByLocation(location));
  }
}
