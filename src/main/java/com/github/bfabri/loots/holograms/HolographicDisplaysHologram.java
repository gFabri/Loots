package com.github.bfabri.loots.holograms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.github.bfabri.loots.Loots;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class HolographicDisplaysHologram implements Hologram {
  public static HashMap<String, Hologram> holograms = new HashMap<>();
  
  public void create(Location location, Loots loot, ArrayList<String> lines) {
    Hologram hologram;
    if (Loots.getInstance().getLootsConfig().getBoolean("Loots." + loot.getName() + ".itemInHologram")) {
      ItemStack item = new ItemStack(Material.matchMaterial(Loots.getInstance().getLootsConfig()
            .getString("Loots." + loot.getName() + ".itemType")));
      hologram = HologramsAPI.createHologram((Plugin)Loots.getInstance(), location.add(0.5D, 2.5D, 0.5D));
      if (item != null) {
        hologram.appendItemLine(item);
      } else {
        Loots.getInstance().getLogger().warning("Attention! " + Loots.getInstance().getLootsConfig()
            .getString("Loots." + loot.getName() + ".itemType") + " no is item!");
      } 
    } else {
      hologram = HologramsAPI.createHologram((Plugin)Loots.getInstance(), location.add(0.5D, 1.9D, 0.5D));
    } 
    for (String line : lines)
      hologram.appendTextLine(line); 
    holograms.put(Util.getStringByLocation(location), hologram);
  }
  
  public void remove(Location location, Loot loot) {
    if (holograms.containsKey(Util.getStringByLocation(location))) {
      ((Hologram)holograms.get(Util.getStringByLocation(location))).delete();
      holograms.remove(Util.getStringByLocation(location));
    } 
  }
  
  public void removeAll() {
    for (Map.Entry<String, Hologram> entry : holograms.entrySet())
      ((Hologram)entry.getValue()).delete(); 
  }
}
