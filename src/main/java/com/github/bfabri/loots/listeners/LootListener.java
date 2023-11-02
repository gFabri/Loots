package com.github.bfabri.loots.listeners;

import com.github.lfabril.loots.ConfigHandler;
import com.github.lfabril.loots.Loots;
import com.github.lfabril.loots.loot.Loot;
import com.github.lfabril.loots.loot.LootRewards;
import com.github.lfabril.loots.loot.Package;
import com.github.lfabril.loots.loot.PackageRewards;
import com.github.lfabril.loots.loot.keys.LootKey;
import com.github.lfabril.loots.utils.CreateItem;
import com.github.lfabril.loots.utils.LootUtils;
import com.github.lfabril.loots.utils.Util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class LootListener implements Listener {
  private boolean waitForPercentage;
  
  private Player playerWaiting;
  
  private Loot loot;
  
  private int slot;
  
  @EventHandler
  public void onItemDrop(PlayerDropItemEvent e) {
    ItemStack item = e.getItemDrop().getItemStack();
    if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item
      .getItemMeta().getDisplayName().contains("Loot")) {
      e.getPlayer().setItemInHand(new ItemStack(Material.AIR));
      e.getPlayer().playSound(e.getPlayer().getLocation(), isOldVersion() ? Sound.valueOf("ITEM_BREAK") : Sound.valueOf("ENTITY_ITEM_BREAK"), 1.0F, 1.0F);
      e.setCancelled(true);
    } 
  }
  
  @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
  public void onBlockPlace(BlockPlaceEvent event) {
    Player player = event.getPlayer();
    if (player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().hasDisplayName() && player
      .getItemInHand().getItemMeta().getDisplayName().contains("Loot")) {
      String lootType = player.getItemInHand().getItemMeta().getDisplayName().replaceAll(" Loot", "");
      Loot loot = LootUtils.getLoot(ChatColor.stripColor(lootType).toLowerCase());
      if (loot instanceof LootKey) {
        LootKey LootKey = (LootKey)loot;
        Location location = event.getBlock().getLocation();
        LootKey.addLocation(location.getBlockX() + "-" + location.getBlockY() + "-" + location.getBlockZ(), location);
        LootKey.addToConfig(location);
        event.getBlock().setMetadata("loot", (MetadataValue)new FixedMetadataValue(
              (Plugin)Loots.getInstance(), loot.getName()));
        player.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.loot-placed").replace("%loot%", loot.getName())));
        LootKey.loadHolograms(event.getBlock().getLocation());
      } 
    } 
  }
  
  @EventHandler(ignoreCancelled = true)
  public void onBlockBreak(BlockBreakEvent event) {
    if (event.getPlayer().hasPermission("loot.edit")) {
      if (event.getBlock().getMetadata("loot") == null || event.getBlock().getMetadata("loot").isEmpty())
        return; 
      String lootType = ((MetadataValue)event.getBlock().getMetadata("loot").get(0)).asString();
      Loot loot = (Loot)LootUtils.getLoots().get(lootType.toLowerCase());
      if (loot == null)
        return; 
      if (!(loot instanceof LootKey))
        return; 
      if (!event.getPlayer().isSneaking()) {
        event.getPlayer().sendMessage(ChatColor.RED + "Sneak to Break!");
        event.setCancelled(true);
      } 
      if (event.getPlayer().isSneaking()) {
        LootKey LootKey = (LootKey)loot;
        event.getBlock().removeMetadata("loot", (Plugin)Loots.getInstance());
        LootKey.removeFromConfig(event.getBlock().getLocation());
        if (Loots.getInstance().getLootsConfig().getBoolean("Loots." + loot.getName() + ".itemInHologram")) {
          LootKey.removeHolograms(event.getBlock().getLocation().add(0.5D, 2.5D, 0.5D));
        } else {
          LootKey.removeHolograms(event.getBlock().getLocation().add(0.5D, 1.9D, 0.5D));
        } 
      } 
    } 
  }
  
  @EventHandler(ignoreCancelled = true)
  public void onPlayerInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    if (event.getClickedBlock() == null || event.getClickedBlock().getType() == Material.AIR)
      return; 
    if (event.getClickedBlock().getMetadata("loot") == null || event
      .getClickedBlock().getMetadata("loot").isEmpty())
      return; 
    String lootType = ((MetadataValue)event.getClickedBlock().getMetadata("loot").get(0)).asString();
    Loot loot = (Loot)LootUtils.getLoots().get(lootType.toLowerCase());
    if (loot == null)
      return; 
    if (!(loot instanceof LootKey))
      return; 
    LootKey LootKey = (LootKey)loot;
    String title = LootKey.getKey().getName();
    if (event.getAction().toString().contains("LEFT")) {
      if (player.isSneaking())
        return; 
      event.setCancelled(true);
      Inventory preview = Bukkit.createInventory(null, Loots.getInstance().getLootsConfig().getInt("Loots." + loot.getName() + ".size"), Util.translate(loot.getDisplayName()));
      preview.clear();
      for (String i : Loots.getInstance().getLootsConfig().getConfigurationSection("Loots." + loot.getName() + ".Rewards").getKeys(false)) {
        String path = "Loots." + loot.getName() + ".Rewards." + Integer.parseInt(i);
        List<String> lore = new ArrayList<>();
        if (Loots.getInstance().getLootsConfig().isSet(path + ".lore")) {
          List<String> lines = Loots.getInstance().getLootsConfig().getStringList(path + ".lore");
          for (String line : lines)
            lore.add(Util.translate(line)); 
        } 
        HashMap<Enchantment, Integer> enchantments = new HashMap<>();
        if (Loots.getInstance().getLootsConfig().isSet(path + ".enchantments")) {
          List<?> enchtantments = Loots.getInstance().getLootsConfig().getList(path + ".enchantments");
          for (Object object : enchtantments) {
            String enchantment = (String)object;
            String[] args = enchantment.split(":");
            int level = 1;
            if (args.length > 1)
              level = Integer.parseInt(args[1]); 
            Enchantment enchant = Util.getEnchantmentFromNiceName(args[0].toUpperCase());
            if (enchant == null) {
              Bukkit.getLogger().warning("Invalid enchantment " + args[0].toUpperCase());
              continue;
            } 
            enchantments.put(enchant, Integer.valueOf(level));
          } 
        } 
        ItemStack stack = (new CreateItem(Material.valueOf(Loots.getInstance().getLootsConfig().getString(path + ".type")), Loots.getInstance().getLootsConfig().getInt(path + ".amount"), Loots.getInstance().getLootsConfig().getInt(path + ".short"))).addLore(lore).addEnchantments(enchantments).setName(Loots.getInstance().getLootsConfig().getString(path + ".displayName")).create();
        if (stack == null)
          continue; 
        preview.setItem(Integer.parseInt(i), stack);
      } 
      player.openInventory(preview);
    } else if (event.getAction().toString().contains("RIGHT") && player.getGameMode() == GameMode.CREATIVE && player.hasPermission("loot.edit")) {
      event.setCancelled(true);
      Inventory edit = Bukkit.createInventory(null, Loots.getInstance().getLootsConfig().getInt("Loots." + loot.getName() + ".size"), Util.translate(loot.getDisplayName()));
      edit.clear();
      for (String i : Loots.getInstance().getLootsConfig().getConfigurationSection("Loots." + loot.getName() + ".Rewards").getKeys(false)) {
        String path = "Loots." + loot.getName() + ".Rewards." + Integer.parseInt(i);
        List<String> lore = new ArrayList<>();
        if (Loots.getInstance().getLootsConfig().isSet(path + ".lore")) {
          List<String> lines = Loots.getInstance().getLootsConfig().getStringList(path + ".lore");
          for (String line : lines)
            lore.add(Util.translate(line)); 
        } 
        HashMap<Enchantment, Integer> enchantments = new HashMap<>();
        if (Loots.getInstance().getLootsConfig().isSet(path + ".enchantments")) {
          List<?> enchtantments = Loots.getInstance().getLootsConfig().getList(path + ".enchantments");
          for (Object object : enchtantments) {
            String enchantment = (String)object;
            String[] args = enchantment.split(":");
            int level = 1;
            if (args.length > 1)
              level = Integer.parseInt(args[1]); 
            Enchantment enchant = Util.getEnchantmentFromNiceName(args[0].toUpperCase());
            if (enchant == null) {
              Bukkit.getLogger().warning("Invalid enchantment " + args[0].toUpperCase());
              continue;
            } 
            enchantments.put(enchant, Integer.valueOf(level));
          } 
        } 
        ItemStack stack = (new CreateItem(Material.valueOf(Loots.getInstance().getLootsConfig().getString(path + ".type")), Loots.getInstance().getLootsConfig().getInt(path + ".amount"), Loots.getInstance().getLootsConfig().getInt(path + ".short"))).addLore(lore).addEnchantments(enchantments).setName(Loots.getInstance().getLootsConfig().getString(path + ".displayName")).create();
        if (stack == null)
          continue; 
        edit.setItem(Integer.parseInt(i), stack);
      } 
      player.openInventory(edit);
    } else if (player.getItemInHand() != null && player.getItemInHand().hasItemMeta() && !player.getItemInHand().getType().equals(Material.AIR) && player
      .getItemInHand().getItemMeta().getDisplayName() != null && player
      .getItemInHand().getItemMeta().getDisplayName().equals(title)) {
      event.setCancelled(true);
      if (player.getInventory().firstEmpty() == -1) {
        player.sendMessage(ChatColor.RED + "You inventory is full");
        return;
      } 
      if (LootKey.getKey().isEnchanted() && !player.getItemInHand().getItemMeta().hasEnchants())
        return; 
      if (player.getItemInHand().getAmount() > 1) {
        player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
      } else {
        player.setItemInHand(null);
      } 
      loot.handle(player);
    } else {
      event.setCancelled(true);
    } 
  }
  
  @EventHandler
  public void onPlayerCloseLoot(InventoryCloseEvent event) {
    Player player = (Player)event.getPlayer();
    if (event.getInventory().getType() != InventoryType.CHEST)
      return; 
    Loot loot = Loots.getInstance().getLootUtils().getLootFromDisplayname(event.getView().getTitle());
    if (loot == null || !event.getView().getTitle().equalsIgnoreCase(Util.translate(loot.getDisplayName())))
      return; 
    if (player.getGameMode() != GameMode.CREATIVE || !player.hasPermission("loot.edit"))
      return; 
    for (int i = 0; i < event.getInventory().getSize(); i++) {
      String path = "Loots." + loot.getName() + ".Rewards." + i;
      if (event.getInventory().getItem(i) != null) {
        ItemStack itemStack = event.getInventory().getItem(i);
        Loots.getInstance().getLootsConfig().set(path + ".type", itemStack.getType().name());
        Loots.getInstance().getLootsConfig().set(path + ".short", Short.valueOf(itemStack.getDurability()));
        Loots.getInstance().getLootsConfig().set(path + ".amount", Integer.valueOf(itemStack.getAmount()));
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore()) {
          Loots.getInstance().getLootsConfig().set(path + ".lore", itemStack.getItemMeta().getLore());
        } else {
          Loots.getInstance().getLootsConfig().set(path + ".lore", null);
        } 
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
          Loots.getInstance().getLootsConfig().set(path + ".displayName", itemStack.getItemMeta().getDisplayName());
        } else {
          Loots.getInstance().getLootsConfig().set(path + ".displayName", "NONE");
        } 
        List<String> enchants = new ArrayList<>();
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasEnchants()) {
          itemStack.getEnchantments().forEach((enchant, level) -> enchants.add(enchant.getName().toUpperCase() + ":" + level));
          Loots.getInstance().getLootsConfig()
            .set(path + ".enchantments", enchants);
        } else {
          Loots.getInstance().getLootsConfig()
            .set(path + ".enchantments", null);
        } 
      } else {
        Loots.getInstance().getLootsConfig().set(path, null);
      } 
      ConfigHandler.Configs.LOOTS.saveConfig();
    } 
    LootKey keyloot = (LootKey)loot;
    keyloot.loadLootBase();
    player.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.loot-update")));
  }
  
  @EventHandler
  public void onInventoryClick(InventoryClickEvent e) {
    Player player = (Player)e.getWhoClicked();
    if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)
      return; 
    if (e.getInventory().getType() != InventoryType.CHEST)
      return; 
    Loot loot = Loots.getInstance().getLootUtils().getLootFromDisplayname(e.getView().getTitle());
    if (loot != null && e.getView().getTitle().equalsIgnoreCase(Util.translate(loot.getDisplayName()))) {
      if (player.getGameMode() == GameMode.CREATIVE && player.hasPermission("loot.edit"))
        return; 
      if (loot.getRewardsANDslot().get(Integer.valueOf(e.getSlot())) != null && ((LootRewards)loot.getRewardsANDslot().get(Integer.valueOf(e.getSlot()))).isPackages() && player.getGameMode() == GameMode.SURVIVAL) {
        Inventory pack = Bukkit.createInventory(null, 27, Util.translate("&ePreview"));
        ((Package)Objects.<Package>requireNonNull(LootUtils.getPackage(((LootRewards)loot.getRewardsANDslot().get(Integer.valueOf(e.getSlot()))).getPack().toLowerCase()))).getAllRewards().forEach(rewards -> {
              if (!rewards.isCommand())
                pack.addItem(new ItemStack[] { rewards.getItemStack() }); 
            });
        player.openInventory(pack);
      } 
      e.setCancelled(true);
    } else if (e.getView().getTitle().contains("Preview")) {
      e.setCancelled(true);
    } 
  }
  
  @EventHandler
  public void onPercentageClick(InventoryClickEvent e) {
    Player player = (Player)e.getWhoClicked();
    if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)
      return; 
    if (e.getView().getTitle().contains(Util.translate("Percentage"))) {
      Loot loot = Loots.getInstance().getLootUtils().getLootFromDisplayname(e.getView().getTitle().split(" ")[0]);
      if (loot != null) {
        this.loot = loot;
        this.slot = e.getSlot();
        this.waitForPercentage = true;
        this.playerWaiting = player;
        player.sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.insert-percentage")));
        e.setCancelled(true);
        player.closeInventory();
      } 
    } 
  }
  
  @EventHandler
  public void onPlayerChat(AsyncPlayerChatEvent event) {
    if (this.waitForPercentage && event.getPlayer() == this.playerWaiting)
      try {
        event.setCancelled(true);
        double percentage = Double.parseDouble(event.getMessage());
        Loots.getInstance().getLootsConfig().set("Loots." + this.loot.getName() + ".Rewards." + this.slot + ".percentage", Double.valueOf(percentage));
        ConfigHandler.Configs.LOOTS.saveConfig();
        this.loot.loadLootBase();
        event.getPlayer().sendMessage(Util.translate(Loots.getInstance().getMessages().getString("Messages.inserted").replace("%percentage%", String.valueOf(percentage)).replace("%item%", Loots.getInstance().getLootsConfig().getString("Loots." + this.loot.getName() + ".Rewards." + this.slot + ".type"))));
        this.waitForPercentage = false;
        this.playerWaiting = null;
      } catch (NumberFormatException e) {
        event.getPlayer().sendMessage(Util.translate("&cInvalid percentage!"));
      }  
  }
  
  public boolean isOldVersion() {
    String[] split = Bukkit.getBukkitVersion().split("\\.");
    String serverVersion = split[0] + "_" + split[1] + "_R" + split[3].split("\\-")[0];
    return (!serverVersion.startsWith("1_7") || !serverVersion.startsWith("1_8"));
  }
}
