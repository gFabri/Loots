package com.github.bfabri.loots.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.github.bfabri.loots.Loots;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class LootUtils {
  public static HashMap<String, Loots> getLoots() {
    return loots;
  }
  
  public static void setLoots(HashMap<String, Loots> loots) {
    LootUtils.loots = loots;
  }
  
  private static HashMap<String, Loots> loots = new HashMap<>();
  
  public static HashMap<String, Package> getPackages() {
    return packages;
  }
  
  public static void setPackages(HashMap<String, Package> packages) {
    LootUtils.packages = packages;
  }
  
  private static HashMap<String, Package> packages = new HashMap<>();
  
  private static final HashMap<String, List<String>> holograms = new HashMap<>();
  
  public static void addLoot(String name, Loots loot) {
    loots.put(name, loot);
  }
  
  public static void addPackage(String name, Package pack) {
    packages.put(name, pack);
  }
  
  public static Loots getLoot(String name) {
    if (loots.containsKey(name))
      return loots.get(name); 
    return null;
  }
  
  public static Package getPackage(String name) {
    if (packages.containsKey(name))
      return packages.get(name); 
    return null;
  }
  
  public static void openPercentageChange(Player sender, String name) {
    Loots loot = getLoots().get(name.toLowerCase());
    Inventory percentage = Bukkit.createInventory(null, Loots.getInstance().getLootsConfig().getInt("Loots." + loot.getName() + ".size"), Util.translate(loot.getDisplayName() + " &7Percentage, (&e" + loot.getTotalPercentage() + "&7/&e100)"));
    percentage.clear();
    loot.getRewardsANDslot().forEach((slot, reward) -> {
          ItemStack clone = reward.getItemStack().clone();
          ItemMeta meta = clone.getItemMeta();
          ArrayList<String> lore = new ArrayList<>();
          if (meta.getLore() != null)
            meta.getLore().forEach(()); 
          lore.add(Util.translate("&9Percentage&7: &f" + reward.getPercentage()));
          reward.setPercentage(reward.getPercentage());
          meta.setLore(lore);
          clone.setItemMeta(meta);
          percentage.setItem(slot.intValue(), clone);
        });
    sender.openInventory(percentage);
  }
  
  public static void openEditPackage(Player sender, String name) {
    Package pack = getPackages().get(name.toLowerCase());
    if (pack == null) {
      sender.sendMessage(ChatColor.RED + "Error with Package " + name);
      return;
    } 
    Inventory packageEdit = Bukkit.createInventory(null, 27, Util.translate("&6" + pack.getName().toUpperCase()));
    packageEdit.clear();
    pack.getAllRewards().forEach(reward -> packageEdit.addItem(new ItemStack[] { reward.getItemStack() }));
    sender.openInventory(packageEdit);
  }
  
  public static void setPackage(Player sender, String name, String pack, int slot) {
    String path = "Loots." + name + ".Rewards." + slot;
    if (Loots.getInstance().getLootsConfig().getString(path) == null) {
      sender.sendMessage(ChatColor.RED + "Slot is invalid or incorrect!");
      return;
    } 
    String itemType = Loots.getInstance().getLootsConfig().getString(path + ".type");
    Loots.getInstance().getLootsConfig().set(path + ".package", pack);
    ConfigHandler.Configs.LOOTS.saveConfig();
    ((Loot)Objects.<Loot>requireNonNull(getLoot(name.toLowerCase()))).loadLootBase();
    sender.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.package-set").replace("%package%", pack).replace("%loot%", name.toLowerCase()).replace("%item%", itemType)));
  }
  
  public static void addCommand(String loot, int slot, String command, CommandSender sender) {
    Loot loots = getLoot(loot.toLowerCase());
    if (loots.getRewardsANDslot().get(Integer.valueOf(slot)) == null) {
      sender.sendMessage(ChatColor.RED + "Slot incorrect!");
      return;
    } 
    List<String> commands = Loots.getInstance().getLootsConfig().getStringList("Loots." + loots.getName() + ".Rewards." + slot + ".commands");
    commands.add(command);
    Loots.getInstance().getLootsConfig().set("Loots." + loots.getName() + ".Rewards." + slot + ".commands", commands);
    ConfigHandler.Configs.LOOTS.saveConfig();
    sender.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.command-added").replace("%command%", command).replace("%item%", ((LootRewards)loots.getRewardsANDslot().get(Integer.valueOf(slot))).getItemStack().getType().name())));
    ((Loot)Objects.<Loot>requireNonNull(loots)).loadLootBase();
  }
  
  public static void removeCommand(String loot, int slot, int index, CommandSender sender) {
    Loot loots = getLoot(loot.toLowerCase());
    if (loots.getRewardsANDslot().get(Integer.valueOf(slot)) == null) {
      sender.sendMessage(ChatColor.RED + "Slot incorrect!");
      return;
    } 
    List<String> commands = Loots.getInstance().getLootsConfig().getStringList("Loots." + loots.getName() + ".Rewards." + slot + ".commands");
    try {
      sender.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.command-removed").replace("%command%", commands.get(index)).replace("%item%", ((LootRewards)loots.getRewardsANDslot().get(Integer.valueOf(slot))).getItemStack().getType().name())));
      commands.remove(index);
    } catch (IndexOutOfBoundsException ex) {
      sender.sendMessage(ChatColor.RED + "Index is incorrect!");
      return;
    } 
    Loots.getInstance().getLootsConfig().set("Loots." + loots.getName() + ".Rewards." + slot + ".commands", commands);
    ConfigHandler.Configs.LOOTS.saveConfig();
    ((Loot)Objects.<Loot>requireNonNull(loots)).loadLootBase();
  }
  
  public static void listCommands(String loot, int slot, CommandSender sender) {
    Loot loots = getLoot(loot.toLowerCase());
    if (loots.getRewardsANDslot().get(Integer.valueOf(slot)) == null) {
      sender.sendMessage(ChatColor.RED + "Slot incorrect!");
      return;
    } 
    List<String> commands = Loots.getInstance().getLootsConfig().getStringList("Loots." + loots.getName() + ".Rewards." + slot + ".commands");
    sender.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.command-list").replace("%commands%", commands.stream().map(String::toLowerCase).collect((Collector)Collectors.joining(",")))));
  }
  
  public Loot getLootFromDisplayname(String displayName) {
    for (Map.Entry<String, Loot> loots : getLoots().entrySet()) {
      if (Util.translate(((Loot)loots.getValue()).getDisplayName()).equalsIgnoreCase(Util.translate(displayName)))
        return loots.getValue(); 
    } 
    return null;
  }
  
  public void loadAllLoots(Loots loots) {
    if (loots.getLootsConfig().isSet("Loots"))
      for (String loot : loots.getLootsConfig().getConfigurationSection("Loots").getKeys(false))
        registerLoot(loots.getLootsConfig(), loot);  
  }
  
  public void loadAllPackages(Loots loots) {
    if (loots.getLootsConfig().isSet("Packages"))
      for (String pack : loots.getLootsConfig().getConfigurationSection("Packages").getKeys(false))
        registerPackage(loots.getLootsConfig(), pack);  
  }
  
  public static void loadAllHolograms() {
    Loots lootsInstance = Loots.getInstance();
    if (lootsInstance.getLootsConfig().isSet("Loots"))
      for (String loots : lootsInstance.getLootsConfig().getConfigurationSection("Loots").getKeys(false)) {
        Loot loot = getLoot(loots.toLowerCase());
        LootKey keyloot = (LootKey)loot;
        if (loot != null && lootsInstance.getSqlUtils().existLoot(loot.getName())) {
          List<String> locations = lootsInstance.getSqlUtils().getLocations(loot.getName());
          for (String location : locations)
            keyloot.loadHolograms(Util.getLocationByString(location)); 
        } 
      }  
  }
  
  public static void removeAllHolograms() {
    Loots.getInstance().getHologramUtils().getHologramPlugin().getHologram().removeAll();
  }
  
  public static List<String> getHolograms(String lootType) {
    return holograms.get(lootType.toLowerCase());
  }
  
  public static void giveLoot(Player player, Loot lootType, boolean all) {
    if (!all) {
      if (player == null || !player.isOnline() || lootType == null)
        return; 
      ItemStack lootItem = new ItemStack(lootType.getBlock(), 1, (short)0);
      ItemMeta lootMeta = lootItem.getItemMeta();
      lootMeta.setDisplayName(ChatColor.GOLD + lootType.getName() + " Loot");
      lootItem.setItemMeta(lootMeta);
      player.getInventory().addItem(new ItemStack[] { lootItem });
    } else {
      for (String loot : Loots.getInstance().getLootsConfig().getConfigurationSection("Loots").getKeys(false)) {
        ItemStack lootItem = new ItemStack(Material.valueOf(Loots.getInstance().getLootsConfig().getString("Loots." + loot + ".type")), 1, (short)0);
        ItemMeta lootMeta = lootItem.getItemMeta();
        lootMeta.setDisplayName(ChatColor.GOLD + loot + " Loot");
        lootItem.setItemMeta(lootMeta);
        player.getInventory().addItem(new ItemStack[] { lootItem });
      } 
    } 
  }
  
  public static void giveKey(Player player, String lootType, Integer amount, CommandSender sender) {
    LootKey loot = (LootKey)getLoots().get(lootType.toLowerCase());
    if (loot == null) {
      sender.sendMessage(ChatColor.RED + "Loot type: '" + lootType + "' does not exist");
      return;
    } 
    Key key = loot.getKey();
    if (key == null) {
      sender.sendMessage(ChatColor.RED + "Could not get key for Loot: '" + lootType + "'");
      return;
    } 
    if (player.getInventory().firstEmpty() == -1) {
      ItemStack itemStack = key.getKey(amount, sender);
      player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
      return;
    } 
    player.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.key-get").replace("%loot%", lootType).replace("%amount%", String.valueOf(amount))));
    sender.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.key-gived").replace("%loot%", lootType).replace("%amount%", String.valueOf(amount)).replace("%player%", player.getName())));
    ItemStack keyItem = key.getKey(amount, sender);
    HashMap<Integer, ItemStack> remaining = player.getInventory().addItem(new ItemStack[] { keyItem });
    int amountLeft = 0;
    for (Map.Entry<Integer, ItemStack> item : remaining.entrySet())
      amountLeft += ((ItemStack)item.getValue()).getAmount(); 
    if (amountLeft > 0)
      giveKey(player, lootType, Integer.valueOf(amountLeft), sender); 
  }
  
  public static void giveKeyAll(String lootType, Integer amount, CommandSender sender) {
    LootKey loot = (LootKey)getLoots().get(lootType.toLowerCase());
    if (loot == null) {
      sender.sendMessage(ChatColor.RED + "Loot type: '" + lootType + "' does not exist");
      return;
    } 
    Key key = loot.getKey();
    if (key == null) {
      sender.sendMessage(ChatColor.RED + "Could not get key for Loot: '" + lootType + "'");
      return;
    } 
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (player.getInventory().firstEmpty() == -1) {
        ItemStack itemStack = key.getKey(amount, sender);
        player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
        return;
      } 
      player.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.key-get").replace("%loot%", lootType).replace("%amount%", String.valueOf(amount))));
      ItemStack keyItem = key.getKey(amount, sender);
      HashMap<Integer, ItemStack> remaining = player.getInventory().addItem(new ItemStack[] { keyItem });
      int amountLeft = 0;
      for (Map.Entry<Integer, ItemStack> item : remaining.entrySet())
        amountLeft += ((ItemStack)item.getValue()).getAmount(); 
      if (amountLeft > 0)
        giveKey(player, lootType, Integer.valueOf(amountLeft), sender); 
    } 
    sender.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.key-gived").replace("%loot%", lootType).replace("%amount%", String.valueOf(amount)).replace("%player%", "All online players")));
  }
  
  public static void displaynameLoot(FileConfiguration config, String name, String displayname) {
    Loot loot = getLoots().get(name.toLowerCase());
    config.set("Loots." + loot.getName() + ".displayName", displayname.replace("_", " "));
    loot.setDisplayName(displayname.replace("_", " "));
    ConfigHandler.Configs.LOOTS.saveConfig();
  }
  
  public static void resize(FileConfiguration config, String name, int size) {
    if (!isValidSize(size)) {
      Loots.getInstance().getLogger().warning("Loot " + name + " have an invalid size!");
      return;
    } 
    Loot loot = getLoots().get(name.toLowerCase());
    config.set("Loots." + loot.getName() + ".size", Integer.valueOf(size));
    loot.setSize(size);
    ConfigHandler.Configs.LOOTS.saveConfig();
  }
  
  public static void unregisterLoot(FileConfiguration config, String name) {
    config.set("Loots." + name, null);
    ConfigHandler.Configs.LOOTS.saveConfig();
    getLoots().remove(name.toLowerCase());
  }
  
  public static void unregisterPackage(FileConfiguration config, String name) {
    config.set("Packages." + name, null);
    ConfigHandler.Configs.LOOTS.saveConfig();
    getPackages().remove(name.toLowerCase());
  }
  
  public static void registerLoot(FileConfiguration config, String name) {
    if (!config.isSet("Hologram Text")) {
      List<String> toAdd = new ArrayList<>();
      toAdd.add("%loot%");
      toAdd.add("Right-Click to Open!");
      toAdd.add("Left-Click to Preview!");
      config.set("Hologram Text", toAdd);
    } 
    List<String> defaultHologramText = Loots.getInstance().getLootsConfig().getStringList("Hologram Text");
    holograms.put(name.toLowerCase(), defaultHologramText);
    if (!config.isSet("Loots." + name)) {
      config.set("Loots." + name + ".itemInHologram", Boolean.valueOf(false));
      config.set("Loots." + name + ".itemType", "DIAMOND_SWORD");
      config.set("Loots." + name + ".Key.type", "TRIPWIRE_HOOK");
      config.set("Loots." + name + ".Key.displayName", "%type% Key");
      config.set("Loots." + name + ".Key.enchanted", Boolean.valueOf(true));
      config.set("Loots." + name + ".displayName", name);
      config.set("Loots." + name + ".type", "CHEST");
      config.set("Loots." + name + ".size", Integer.valueOf(27));
      config.set("Loots." + name + ".Rewards.0.type", "DIAMOND_SWORD");
      config.set("Loots." + name + ".Rewards.0.short", Integer.valueOf(0));
      config.set("Loots." + name + ".Rewards.0.percentage", Integer.valueOf(100));
      config.set("Loots." + name + ".Rewards.0.amount", Integer.valueOf(1));
      config.set("Loots." + name + ".Rewards.0.displayName", "&9Example Item");
      ConfigHandler.Configs.LOOTS.saveConfig();
    } 
    if (config.isSet("Loots." + name))
      addLoot(name.toLowerCase(), (Loot)new LootKey(name)); 
  }
  
  public static void registerPackage(FileConfiguration config, String name) {
    if (!config.isSet("Packages." + name)) {
      config.set("Packages." + name + ".items.0.type", "DIAMOND_SWORD");
      config.set("Packages." + name + ".items.0.short", Integer.valueOf(0));
      config.set("Packages." + name + ".items.0.amount", Integer.valueOf(1));
      config.set("Packages." + name + ".items.0.displayName", "&9Example Item");
      ConfigHandler.Configs.LOOTS.saveConfig();
    } 
    if (config.isSet("Packages." + name))
      addPackage(name.toLowerCase(), new Package(name)); 
  }
  
  public void loadMetaData() {
    Loots.getInstance().getSqlUtils().getLoots().forEach(name -> {
          Loot loot = getLoot(name.toLowerCase());
          if (loot != null) {
            LootKey keyloot = (LootKey)loot;
            List<String> locations = Loots.getInstance().getSqlUtils().getLocations(name);
            for (String s : locations) {
              Location location = Util.getLocationByString(s);
              try {
                Block block = location.getBlock();
                if (block == null || block.getType().equals(Material.AIR)) {
                  Loots.getInstance().getLogger().warning("No block found at " + Util.getStringByLocation(location) + " removing from storage");
                  keyloot.removeFromConfig(location);
                  if (Loots.getInstance().getLootsConfig().getBoolean("Loots." + name + ".itemInHologram")) {
                    keyloot.removeHolograms(location.add(0.5D, 2.5D, 0.5D));
                    continue;
                  } 
                  keyloot.removeHolograms(location.add(0.5D, 1.9D, 0.5D));
                  continue;
                } 
                block.setMetadata("loot", (MetadataValue)new FixedMetadataValue((Plugin)Loots.getInstance(), loot.getName()));
              } catch (Exception exception) {}
            } 
          } 
        });
  }
  
  public static boolean isValidSize(int size) {
    switch (size) {
      case 9:
      case 18:
      case 27:
      case 36:
      case 45:
      case 54:
        return true;
    } 
    return false;
  }
}
