package com.github.bfabri.loots.holograms;

import java.util.ArrayList;

import com.github.bfabri.loots.Loots;
import org.bukkit.Location;

public interface Hologram {
  void create(Location paramLocation, Loots paramLoot, ArrayList<String> paramArrayList);
  
  void remove(Location paramLocation, Loots paramLoot);
  
  void removeAll();
}
