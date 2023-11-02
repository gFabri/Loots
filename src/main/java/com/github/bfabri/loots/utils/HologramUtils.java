package com.github.bfabri.loots.utils;

import org.bukkit.Bukkit;

public class HologramUtils {
  private HologramPlugin hologramPlugin = HologramPlugin.NONE;
  
  public HologramUtils() {
    if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays"))
      this.hologramPlugin = HologramPlugin.HOLOGRAPHIC_DISPLAYS; 
    if (Bukkit.getPluginManager().isPluginEnabled("Holograms"))
      this.hologramPlugin = HologramPlugin.HOLOGRAMS; 
    getHologramPlugin().init();
  }
  
  public HologramPlugin getHologramPlugin() {
    return this.hologramPlugin;
  }
  
  public enum HologramPlugin {
    NONE, HOLOGRAMS, HOLOGRAPHIC_DISPLAYS;
    
    private Hologram hologram;
    
    public void init() {
      switch (this) {
        default:
          this.hologram = (Hologram)new FallbackHologram();
          return;
        case HOLOGRAPHIC_DISPLAYS:
          this.hologram = (Hologram)new HolographicDisplaysHologram();
          return;
        case HOLOGRAMS:
          break;
      } 
      this.hologram = (Hologram)new HolographicHologramHologram();
    }
    
    public Hologram getHologram() {
      return this.hologram;
    }
  }
}
