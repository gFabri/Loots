package com.github.bfabri.loots.holograms;

import java.util.ArrayList;
import org.bukkit.Location;

public class FallbackHologram implements Hologram {
  public void create(Location location, Loot crate, ArrayList<String> lines) {
    Loots.getInstance().getLogger().warning("Hologram #create was called but no Hologram plugin is loaded!");
  }
  
  public void remove(Location location, Loot crate) {
    Loots.getInstance().getLogger().warning("Hologram #remove was called but no Hologram plugin is loaded!");
  }
  
  public void removeAll() {
    Loots.getInstance().getLogger().warning("Hologram #removeAll was called but no Hologram plugin is loaded!");
  }
}
