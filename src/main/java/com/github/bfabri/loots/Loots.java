package com.github.bfabri.loots;

import com.github.bfabri.loots.commands.utils.CommandsModule;
import com.github.bfabri.loots.commands.utils.framework.SimpleCommandManager;
import com.github.bfabri.loots.holograms.HologramInit;
import com.github.bfabri.loots.listeners.InventoryConfigListener;
import com.github.bfabri.loots.listeners.LootListener;
import com.github.bfabri.loots.listeners.RewardsListener;
import com.github.bfabri.loots.loot.Loot;
import com.github.bfabri.loots.loot.LootInterface;
import com.github.bfabri.loots.loot.LootJSON;
import com.github.bfabri.loots.loot.Rewards;
import com.github.bfabri.loots.loot.key.Key;
import com.github.bfabri.loots.utils.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;

public class Loots extends JavaPlugin {

    @Getter
    private static Loots instance;

    @Getter
    private LootInterface lootInterface;

    @Getter
    private HologramInit hologramInit;

    @Getter
    private InventoryConfigListener inventoryConfigListener;

    @Getter
    private RewardsListener rewardsListener;

    public void onEnable() {
        instance = this;
        new ConfigHandler(this);

        ConfigurationSerialization.registerClass(Loot.class);
        ConfigurationSerialization.registerClass(Rewards.class);
        ConfigurationSerialization.registerClass(Key.class);

        if (ConfigHandler.Configs.CONFIG.getConfig().getString("LOOTS.STORAGE.type").equalsIgnoreCase("JSON")) {
            lootInterface = new LootJSON();
        } else {
//            lootInterface = new StatsMySQL();
//            try {
//                if (!((StatsMySQL) lootInterface).getConnection().isClosed()) {
//                    lootInterface.loadStats();
//                } else {
//                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error with MySQL: Incorrect data in config.yml");
//                    Bukkit.getPluginManager().disablePlugin(this);
//                    return;
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
        }

        Bukkit.getConsoleSender().sendMessage(Utils.translate("&bDetected Server Version&7: &f" + Bukkit.getServer().getBukkitVersion().split("-")[0]));

        this.hologramInit = new HologramInit();
//        this.lootUtils.loadAllPackages(this);
        switch (getHologramInit().getHologramPlugin()) {
            default:
                Bukkit.getConsoleSender()
                        .sendMessage(ChatColor.RED + "Unable to find compatible Hologram plugin, holograms will not work!");
                break;
            case HOLOGRAMS:
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6&lLoots&8] &eHolograms was found, hooking in!"));
                break;
            case DECENT_HOLOGRAMS:
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6&lLoots&8] &eDecent Holograms was found, hooking in!"));
                break;
        }
        hologramInit.getHologramPlugin().getHologram().loadAllHolograms();
        registerCommands();
        registerListeners();

        (new BukkitRunnable() {
            public void run() {
                Bukkit.getConsoleSender().sendMessage(Utils.translate(Utils.PREFIX + "&aSaving Loots..."));
                if (ConfigHandler.Configs.CONFIG.getConfig().getString("LOOTS.STORAGE.type").equalsIgnoreCase("JSON")) {
                    lootInterface.saveLoots();
                }
                Bukkit.getConsoleSender().sendMessage(Utils.translate(Utils.PREFIX + "&aSaved loots"));
            }
        }).runTaskTimerAsynchronously(instance, 3000L, 6000L);
    }

    public void registerListeners() {
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new LootListener(), this);
        manager.registerEvents(inventoryConfigListener = new InventoryConfigListener(), this);
        manager.registerEvents(rewardsListener = new RewardsListener(), this);
//        manager.registerEvents((Listener)new KeyListener(), (Plugin)this);
//        manager.registerEvents((Listener)new PackageListener(), (Plugin)this);
    }

    private void registerCommands() {
        new SimpleCommandManager(this).registerAll(new CommandsModule());
    }

    public void onDisable() {
        getHologramInit().getHologramPlugin().getHologram().removeAllHolograms();
        if (ConfigHandler.Configs.CONFIG.getConfig().getString("LOOTS.STORAGE.type").equalsIgnoreCase("JSON")) {
            lootInterface.saveLoots();
        }
        instance = null;
    }
}
