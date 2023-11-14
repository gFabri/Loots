package com.github.bfabri.loots.holograms;

import com.github.bfabri.loots.ConfigHandler;
import com.github.bfabri.loots.Loots;
import com.github.bfabri.loots.loot.Loot;
import com.github.bfabri.loots.utils.Utils;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.utils.items.HologramItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HolographiDecentHologram implements Hologram {
  public static HashMap<String, eu.decentsoftware.holograms.api.holograms.Hologram> holograms = new HashMap<>();

  public void createHologram(Location location, Loot loot, ArrayList<String> lines) {
    if (!loot.isHologram()) {
      return;
    }
    eu.decentsoftware.holograms.api.holograms.Hologram hologram;
    ArrayList<String> newLines = new ArrayList<>();
    if (loot.isItemInHologram()) {
        ItemStack item = new ItemStack(Material.valueOf(loot.getItemType()));
      for (String line: lines) {
        if (line.contains("{icon}")) {
          newLines.add(line.replace("{icon}", "#ICON:" + HologramItem.fromItemStack(item).getContent()).replace("{loot}", Utils.translate(loot.getDisplayName())));
        } else {
          newLines.add(line.replace("{loot}", Utils.translate(loot.getDisplayName())));
        }
      }
      hologram = DHAPI.createHologram(loot.getName() + location.getBlockX() + "-" + location.getBlockZ(), location.add(0.5D, 2.5D, 0.5D), false, newLines);
    } else {
      lines.remove(0);
      for (String line: lines) {
        newLines.add(line.replace("{loot}", Utils.translate(loot.getDisplayName())));
      }
      hologram = DHAPI.createHologram(loot.getName() + location.getBlockX() + "-" + location.getBlockZ(), location.add(0.5D, 1.9D, 0.5D), false, newLines);
    }
    holograms.put(Utils.serializeLocation(location), hologram);
  }
  
  public void removeHologram(Location location, Loot loot) {
    if (loot.isItemInHologram()) {
      location.add(0.5D, 2.5D, 0.5D);
    } else {
      location.add(0.5D, 1.9D, 0.5D);
    }
    if (holograms.containsKey(Utils.serializeLocation(location))) {
      holograms.get(Utils.serializeLocation(location)).destroy();
      holograms.remove(Utils.serializeLocation(location));
    } 
  }
  
  public void removeAllHolograms() {
    holograms.forEach((key, value) -> value.destroy());
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
