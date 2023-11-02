package com.github.bfabri.loots;

import com.github.bfabri.loots.commands.utils.CommandsModule;
import com.github.bfabri.loots.commands.utils.framework.SimpleCommandManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Loots extends JavaPlugin {
    @Getter
    private static Loots instance;

    private LootUtils lootUtils;

    private HologramUtils hologramUtils;

    private SQLUtils sqlUtils;

    public LootUtils getLootUtils() {
        return this.lootUtils;
    }

    public HologramUtils getHologramUtils() {
        return this.hologramUtils;
    }

    public SQLUtils getSqlUtils() {
        return this.sqlUtils;
    }

    public void onEnable() {
        instance = this;
        new ConfigHandler(this);
        this.sqlUtils = new SQLUtils(this);
        this.hologramUtils = new HologramUtils();
        this.lootUtils = new LootUtils();
        this.lootUtils.loadAllLoots(this);
        this.lootUtils.loadAllPackages(this);
        this.lootUtils.loadMetaData();
        LootUtils.loadAllHolograms();
        switch (getHologramUtils().getHologramPlugin()) {
            default:
                Bukkit.getConsoleSender()
                        .sendMessage(ChatColor.RED + "Unable to find compatible Hologram plugin, holograms will not work!");
                break;
            case HOLOGRAPHIC_DISPLAYS:
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6&lLoots&8] &eHolographicDisplays was found, hooking in!"));
                break;
            case HOLOGRAMS:
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6&lLoots&8] &eHolograms was found, hooking in!"));
                break;
        }
        registerCommands();
        registerListeners();
    }

    public void registerListeners() {
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents((Listener)new LootListener(), (Plugin)this);
        manager.registerEvents((Listener)new KeyListener(), (Plugin)this);
        manager.registerEvents((Listener)new PackageListener(), (Plugin)this);
    }

    private void registerCommands() {
        new SimpleCommandManager(this).registerAll(new CommandsModule());
    }

    public void onDisable() {
        getHologramUtils().getHologramPlugin().getHologram().removeAll();
        instance = null;
    }
}
