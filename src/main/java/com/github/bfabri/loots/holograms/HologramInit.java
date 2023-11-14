package com.github.bfabri.loots.holograms;

import org.bukkit.Bukkit;

public class HologramInit {
  private HologramPlugin hologramPlugin = HologramPlugin.NONE;
  
  public HologramInit() {
    if (Bukkit.getPluginManager().isPluginEnabled("Holograms"))
      this.hologramPlugin = HologramPlugin.HOLOGRAMS;
    if (Bukkit.getPluginManager().isPluginEnabled("DecentHolograms"))
      this.hologramPlugin = HologramPlugin.DECENT_HOLOGRAMS;
    getHologramPlugin().init();
  }
  
  public HologramPlugin getHologramPlugin() {
    return this.hologramPlugin;
  }
  
  public enum HologramPlugin {
    NONE, HOLOGRAMS, DECENT_HOLOGRAMS;
    
    private Hologram hologram;
    
    public void init() {
      switch (this) {
        default:
          this.hologram = new FallbackHologram();
          return;
        case HOLOGRAMS:
          this.hologram = new HolographicHologramHologram();
          break;
        case DECENT_HOLOGRAMS:
          this.hologram = new HolographiDecentHologram();
          break;
      }
    }
    
    public Hologram getHologram() {
      return this.hologram;
    }
  }
}