package com.github.bfabri.loots.holograms;

import com.github.lfabril.loots.Loots;
import com.github.lfabril.loots.loot.Loot;
import com.github.lfabril.loots.utils.Util;
import com.sainttx.holograms.HologramPlugin;
import com.sainttx.holograms.api.Hologram;
import com.sainttx.holograms.api.HologramManager;
import com.sainttx.holograms.api.line.HologramLine;
import com.sainttx.holograms.api.line.ItemLine;
import com.sainttx.holograms.api.line.TextLine;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class HolographicHologramHologram implements Hologram {
  public static HashMap<String, Hologram> holograms = new HashMap<>();
  
  private final HologramManager hologramManager = ((HologramPlugin)JavaPlugin.getPlugin(HologramPlugin.class)).getHologramManager();
  
  public void create(Location location, Loot loot, ArrayList<String> lines) {
    Hologram hologram = new Hologram(loot.getName(), location.add(0.5D, 1.9D, 0.5D), true);
    this.hologramManager.addActiveHologram(hologram);
    if (Loots.getInstance().getLootsConfig().getBoolean("Loots." + loot.getName() + ".itemInHologram")) {
      ItemStack item = new ItemStack(Material.matchMaterial(Loots.getInstance().getLootsConfig()
            .getString("Loots." + loot.getName() + ".itemType")));
      if (item != null)
        lines.forEach(line -> {
              ItemLine itemLine = new ItemLine(hologram, item);
              hologram.addLine((HologramLine)itemLine);
            }); 
    } 
    lines.forEach(to -> {
          TextLine textLine = new TextLine(hologram, to);
          hologram.addLine((HologramLine)textLine);
        });
    this.hologramManager.reload();
    holograms.put(Util.getStringByLocation(location), hologram);
  }
  
  public void remove(Location location, Loot loot) {
    if (holograms.containsKey(Util.getStringByLocation(location))) {
      this.hologramManager.removeActiveHologram(holograms.get(Util.getStringByLocation(location)));
      ((Hologram)holograms.get(Util.getStringByLocation(location))).despawn();
      holograms.remove(Util.getStringByLocation(location));
    } 
  }
  
  public void removeAll() {
    holograms.forEach((key, value) -> {
          value.despawn();
          this.hologramManager.removeActiveHologram(value);
          this.hologramManager.deleteHologram(value);
        });
  }
}
