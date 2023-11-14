package com.github.bfabri.loots.holograms;

import java.util.ArrayList;

import com.github.bfabri.loots.Loots;
import com.github.bfabri.loots.loot.Loot;
import org.bukkit.Location;

public class FallbackHologram implements Hologram {

  @Override
  public void createHologram(Location location, Loot crate, ArrayList<String> lines) {
    Loots.getInstance().getLogger().warning("Hologram #create was called but no Hologram plugin is loaded!");
  }

  @Override
  public void removeHologram(Location location, Loot crate) {
    Loots.getInstance().getLogger().warning("Hologram #remove was called but no Hologram plugin is loaded!");
  }

  @Override
  public void removeAllHolograms() {
    Loots.getInstance().getLogger().warning("Hologram #removeAll was called but no Hologram plugin is loaded!");
  }

  @Override
  public void loadAllHolograms() {
    Loots.getInstance().getLogger().warning("Hologram #loadAll was called but no Hologram plugin is loaded!");
  }
}
