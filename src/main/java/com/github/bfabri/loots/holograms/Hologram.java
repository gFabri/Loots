package com.github.bfabri.loots.holograms;

import java.util.ArrayList;

import com.github.bfabri.loots.loot.Loot;
import org.bukkit.Location;

public interface Hologram {
  void createHologram(Location paramLocation, Loot paramLoot, ArrayList<String> paramArrayList);
  
  void removeHologram(Location paramLocation, Loot paramLoot);
  
  void removeAllHolograms();

  void loadAllHolograms();
}
