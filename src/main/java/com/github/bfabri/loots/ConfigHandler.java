package com.github.bfabri.loots;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigHandler {
  private static ConfigHandler instance;
  
  private final JavaPlugin plugin;
  
  public ConfigHandler(JavaPlugin plugin) {
    this.plugin = plugin;
    instance = this;
    createConfigs();
  }
  
  private void createConfigs() {
    for (Configs config : Configs.values())
      config.init(this); 
  }
  
  public FileConfiguration createConfig(String name) {
    File conf = new File(this.plugin.getDataFolder(), name);
    if (!conf.exists()) {
      conf.getParentFile().mkdirs();
      this.plugin.saveResource(name, false);
    } 
    YamlConfiguration yamlConfiguration = new YamlConfiguration();
    try {
      yamlConfiguration.load(conf);
      return (FileConfiguration)yamlConfiguration;
    } catch (IOException|org.bukkit.configuration.InvalidConfigurationException e) {
      e.printStackTrace();
      return null;
    } 
  }
  
  public void saveConfig(FileConfiguration config, String name) {
    try {
      config.save(new File(this.plugin.getDataFolder(), name));
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  public void reloadConfig(FileConfiguration config, String name) {
    try {
      config.load(new File(this.plugin.getDataFolder(), name));
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  public enum Configs {
    CONFIG("config.yml"),
    LANG("lang.yml"),
    LOOTS("loots.json");
    
    private final String name;
    
    private FileConfiguration config;
    
    Configs(String name) {
      this.name = name;
    }
    
    public void init(ConfigHandler handler) {
      this.config = handler.createConfig(this.name);
    }
    
    public FileConfiguration getConfig() {
      return this.config;
    }
    
    public void saveConfig() {
      ConfigHandler.instance.saveConfig(this.config, this.name);
    }
    
    public void reloadConfig() {
      ConfigHandler.instance.reloadConfig(this.config, this.name);
    }
  }
}
