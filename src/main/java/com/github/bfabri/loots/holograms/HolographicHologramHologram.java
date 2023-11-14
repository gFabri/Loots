package com.github.bfabri.loots.holograms;

import com.github.bfabri.loots.ConfigHandler;
import com.github.bfabri.loots.Loots;
import com.github.bfabri.loots.loot.Loot;
import com.github.bfabri.loots.utils.Utils;
import com.sainttx.holograms.api.HologramManager;
import com.sainttx.holograms.api.HologramPlugin;
import com.sainttx.holograms.api.line.ItemLine;
import com.sainttx.holograms.api.line.TextLine;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;

public class HolographicHologramHologram implements Hologram {
  public static HashMap<String, com.sainttx.holograms.api.Hologram> holograms = new HashMap<>();
  
  private final HologramManager hologramManager = JavaPlugin.getPlugin(HologramPlugin.class).getHologramManager();
  
  public void createHologram(Location location, Loot loot, ArrayList<String> lines) {
    com.sainttx.holograms.api.Hologram hologram;
    lines.remove(0);
    if (loot.isItemInHologram()) {
        ItemStack item = new ItemStack(Material.valueOf(loot.getItemType()));
        hologram = new com.sainttx.holograms.api.Hologram(loot.getName(), location.add(0.5D, 2.5D, 0.5D), true);

        ItemLine itemLine = new ItemLine(hologram, item);
        hologram.addLine(itemLine);

        lines.forEach(to -> {
            TextLine textLine = new TextLine(hologram, to.replace("{loot}", Utils.translate(loot.getDisplayName())));
            hologram.addLine(textLine);
        });
    } else {
        hologram = new com.sainttx.holograms.api.Hologram(loot.getName(), location.add(0.5D, 1.9D, 0.5D), true);
        lines.forEach(to -> {
            TextLine textLine = new TextLine(hologram, to.replace("{loot}", Utils.translate(loot.getDisplayName())));
            hologram.addLine(textLine);
        });
    }
    this.hologramManager.addActiveHologram(hologram);
    this.hologramManager.reload();
    holograms.put(Utils.serializeLocation(location), hologram);
  }
  
  public void removeHologram(Location location, Loot loot) {
      if (loot.isItemInHologram()) {
          location.add(0.5D, 2.5D, 0.5D);
      } else {
          location.add(0.5D, 1.9D, 0.5D);
      }
    if (holograms.containsKey(Utils.serializeLocation(location))) {
      com.sainttx.holograms.api.Hologram hologram = holograms.get(Utils.serializeLocation(location));
      hologram.despawn();
      this.hologramManager.removeActiveHologram(hologram);
      this.hologramManager.deleteHologram(hologram);
      holograms.remove(Utils.serializeLocation(location));
    } 
  }
  
  public void removeAllHolograms() {
    holograms.forEach((key, value) -> {
          value.despawn();
          this.hologramManager.removeActiveHologram(value);
          this.hologramManager.deleteHologram(value);
        });
  }

    @Override
    public void loadAllHolograms() {
        Loots.getInstance().getLootInterface().getLoots().forEach((s, loot) -> {
            if (loot.getLocations() != null) {
                loot.getLocations().forEach(location -> {
                    ArrayList<String> lines = (ArrayList<String>) ConfigHandler.Configs.LANG.getConfig().getStringList("HOLOGRAMS.lines");
                    createHologram(Location.deserialize(location), loot, lines);
                });
            }
        });
    }
}
