/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.Particle
 *  org.bukkit.Sound
 *  org.bukkit.SoundCategory
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryCloseEvent
 *  org.bukkit.event.inventory.InventoryDragEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.EquipmentSlot
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.EnchantmentStorageMeta
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.Plugin
 *  org.jetbrains.annotations.NotNull
 */
package com.gmail.necnionch.myplugin.diamondcompressor.bukkit.gui;

import com.gmail.necnionch.myplugin.diamondcompressor.bukkit.CompressedDiamond;
import com.gmail.necnionch.myplugin.diamondcompressor.bukkit.DiamondCompressorPlugin;
import com.gmail.necnionch.myplugin.diamondcompressor.bukkit.events.CompressedDiamondEnhanceLevelEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class EnchantUpgradeGUI
implements Listener,
InventoryHolder {
    private static final String GUI_TITLE = String.valueOf(ChatColor.DARK_PURPLE) + "\u30a8\u30f3\u30c1\u30e3\u30f3\u30c8\u5f37\u5316";
    private static final int BOOK_SLOT = 10;
    private static final int DIAMOND_SLOT = 12;
    private static final int RESULT_SLOT = 16;
    private static final int INFO_SLOT = 22;
    private final DiamondCompressorPlugin plugin;
    private final Map<UUID, GUISession> sessions = new HashMap<UUID, GUISession>();

    public EnchantUpgradeGUI(DiamondCompressorPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAnvilClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if (event.getClickedBlock() == null) {
            return;
        }
        Material blockType = event.getClickedBlock().getType();
        if (blockType != Material.ANVIL && blockType != Material.CHIPPED_ANVIL && blockType != Material.DAMAGED_ANVIL) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack handItem = player.getInventory().getItemInMainHand();
        CompressedDiamond compressed = CompressedDiamond.from(handItem);
        if (compressed == null) {
            return;
        }
        event.setCancelled(true);
        this.openGUI(player);
    }

    public void openGUI(Player player) {
        Inventory gui = Bukkit.createInventory((InventoryHolder)this, (int)27, (String)GUI_TITLE);
        ItemStack glass = this.createGlassPane(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 27; ++i) {
            gui.setItem(i, glass);
        }
        gui.setItem(10, null);
        gui.setItem(12, null);
        gui.setItem(1, this.createInfoItem(Material.ENCHANTED_BOOK, String.valueOf(ChatColor.YELLOW) + "\u30a8\u30f3\u30c1\u30e3\u30f3\u30c8\u672c", String.valueOf(ChatColor.GRAY) + "\u6700\u5927\u30ec\u30d9\u30eb\u4ee5\u4e0a\u306e\u30a8\u30f3\u30c1\u30e3\u30f3\u30c8\u672c\u3092", String.valueOf(ChatColor.GRAY) + "\u3053\u3053\u306b\u7f6e\u3044\u3066\u304f\u3060\u3055\u3044"));
        gui.setItem(3, this.createInfoItem(Material.DIAMOND, String.valueOf(ChatColor.AQUA) + "\u5727\u7e2e\u30c0\u30a4\u30e4", String.valueOf(ChatColor.GRAY) + "\u5727\u7e2e\u30c0\u30a4\u30e4\u30e2\u30f3\u30c9\u3092", String.valueOf(ChatColor.GRAY) + "\u3053\u3053\u306b\u7f6e\u3044\u3066\u304f\u3060\u3055\u3044"));
        this.updateResultSlot(gui, null, null);
        gui.setItem(22, this.createInfoItem(Material.PAPER, String.valueOf(ChatColor.WHITE) + "\u4f7f\u3044\u65b9", String.valueOf(ChatColor.GRAY) + "1. \u6700\u5927\u30ec\u30d9\u30eb\u306e\u30a8\u30f3\u30c1\u30e3\u30f3\u30c8\u672c\u3092\u5de6\u306b\u7f6e\u304f", String.valueOf(ChatColor.GRAY) + "2. \u5727\u7e2e\u30c0\u30a4\u30e4\u3092\u53f3\u306b\u7f6e\u304f", String.valueOf(ChatColor.GRAY) + "3. \u5f37\u5316\u30dc\u30bf\u30f3\u3092\u30af\u30ea\u30c3\u30af", "", String.valueOf(ChatColor.GOLD) + "\u6210\u529f: \u30a8\u30f3\u30c1\u30e3\u30f3\u30c8\u30ec\u30d9\u30eb +1", String.valueOf(ChatColor.RED) + "\u5931\u6557: \u30ec\u30d9\u30eb\u304c1\u306b\u623b\u308b"));
        player.openInventory(gui);
        this.sessions.put(player.getUniqueId(), new GUISession());
    }

    private void updateResultSlot(Inventory gui, ItemStack book, CompressedDiamond diamond) {
        EnchantmentStorageMeta meta;
        if (book == null || diamond == null) {
            gui.setItem(16, this.createInfoItem(Material.BARRIER, String.valueOf(ChatColor.RED) + "\u6e96\u5099\u4e2d...", String.valueOf(ChatColor.GRAY) + "\u4e21\u65b9\u306e\u30a2\u30a4\u30c6\u30e0\u3092\u7f6e\u3044\u3066\u304f\u3060\u3055\u3044"));
            return;
        }
        ItemMeta itemMeta = book.getItemMeta();
        if (!(itemMeta instanceof EnchantmentStorageMeta) || !(meta = (EnchantmentStorageMeta)itemMeta).hasStoredEnchants()) {
            gui.setItem(16, this.createInfoItem(Material.BARRIER, String.valueOf(ChatColor.RED) + "\u7121\u52b9\u306a\u30a8\u30f3\u30c1\u30e3\u30f3\u30c8\u672c", String.valueOf(ChatColor.GRAY) + "\u30a8\u30f3\u30c1\u30e3\u30f3\u30c8\u304c\u4ed8\u4e0e\u3055\u308c\u305f\u672c\u304c\u5fc5\u8981\u3067\u3059"));
            return;
        }
        boolean hasUpgradable = meta.getStoredEnchants().entrySet().stream().filter(e -> ((Enchantment)e.getKey()).getMaxLevel() <= (Integer)e.getValue()).anyMatch(e -> this.plugin.getPluginConfig().isAllowEnchantLevelUp((Enchantment)e.getKey(), (Integer)e.getValue()));
        if (!hasUpgradable) {
            gui.setItem(16, this.createInfoItem(Material.BARRIER, String.valueOf(ChatColor.RED) + "\u5f37\u5316\u4e0d\u53ef", String.valueOf(ChatColor.GRAY) + "\u6700\u5927\u30ec\u30d9\u30eb\u4ee5\u4e0a\u306e\u30a8\u30f3\u30c1\u30e3\u30f3\u30c8\u304c\u306a\u3044\u304b", String.valueOf(ChatColor.GRAY) + "\u4e0a\u9650\u306b\u9054\u3057\u3066\u3044\u307e\u3059"));
            return;
        }
        float rate = diamond.getConfig().getOverEnchantRate();
        int ratePercent = (int)(rate * 100.0f);
        gui.setItem(16, this.createInfoItem(Material.LIME_CONCRETE, String.valueOf(ChatColor.GREEN) + String.valueOf(ChatColor.BOLD) + "\u5f37\u5316\u3059\u308b\uff01", String.valueOf(ChatColor.GRAY) + "\u30af\u30ea\u30c3\u30af\u3067\u30a8\u30f3\u30c1\u30e3\u30f3\u30c8\u3092\u5f37\u5316", "", String.valueOf(ChatColor.GOLD) + "\u6210\u529f\u78ba\u7387: " + ratePercent + "%", String.valueOf(ChatColor.GRAY) + "\u4f7f\u7528: " + diamond.getName()));
    }

    @NotNull
    public Inventory getInventory() {
        return Bukkit.createInventory((InventoryHolder)this, (int)27, (String)GUI_TITLE);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof EnchantUpgradeGUI)) {
            return;
        }
        HumanEntity humanEntity = event.getWhoClicked();
        if (!(humanEntity instanceof Player)) {
            return;
        }
        Player player = (Player)humanEntity;
        Inventory gui = event.getInventory();
        int slot = event.getRawSlot();
        if (slot >= 0 && slot < 27) {
            if (slot != 10 && slot != 12) {
                event.setCancelled(true);
                if (slot == 16) {
                    this.handleEnhance(player, gui);
                }
                return;
            }
            this.plugin.getServer().getScheduler().runTaskLater((Plugin)this.plugin, () -> this.updateGUI(gui), 1L);
        } else if (event.isShiftClick() || event.getHotbarButton() >= 0) {
            this.plugin.getServer().getScheduler().runTaskLater((Plugin)this.plugin, () -> this.updateGUI(gui), 1L);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getInventory().getHolder() instanceof EnchantUpgradeGUI)) {
            return;
        }
        Iterator iterator = event.getRawSlots().iterator();
        while (iterator.hasNext()) {
            int slot = (Integer)iterator.next();
            if (slot < 0 || slot >= 27 || slot == 10 || slot == 12) continue;
            event.setCancelled(true);
            return;
        }
        this.plugin.getServer().getScheduler().runTaskLater((Plugin)this.plugin, () -> this.updateGUI(event.getInventory()), 1L);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof EnchantUpgradeGUI)) {
            return;
        }
        HumanEntity humanEntity = event.getPlayer();
        if (!(humanEntity instanceof Player)) {
            return;
        }
        Player player = (Player)humanEntity;
        Inventory gui = event.getInventory();
        this.sessions.remove(player.getUniqueId());
        this.returnItem(player, gui.getItem(10));
        this.returnItem(player, gui.getItem(12));
    }

    private void returnItem(Player player, ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return;
        }
        HashMap overflow = player.getInventory().addItem(new ItemStack[]{item});
        for (ItemStack leftover : overflow.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), leftover);
        }
    }

    private void updateGUI(Inventory gui) {
        ItemStack book = gui.getItem(10);
        ItemStack diamondItem = gui.getItem(12);
        CompressedDiamond diamond = CompressedDiamond.from(diamondItem);
        this.updateResultSlot(gui, book, diamond);
    }

    private void handleEnhance(Player player, Inventory gui) {
        boolean success;
        int newLevel;
        EnchantmentStorageMeta meta;
        ItemStack book = gui.getItem(10);
        ItemStack diamondItem = gui.getItem(12);
        if (book == null || diamondItem == null) {
            return;
        }
        CompressedDiamond diamond = CompressedDiamond.from(diamondItem);
        if (diamond == null) {
            return;
        }
        ItemMeta itemMeta = book.getItemMeta();
        if (!(itemMeta instanceof EnchantmentStorageMeta) || !(meta = (EnchantmentStorageMeta)itemMeta).hasStoredEnchants()) {
            return;
        }
        List upgradableEnchants = meta.getStoredEnchants().entrySet().stream().filter(e -> ((Enchantment)e.getKey()).getMaxLevel() <= (Integer)e.getValue()).filter(e -> this.plugin.getPluginConfig().isAllowEnchantLevelUp((Enchantment)e.getKey(), (Integer)e.getValue())).map(Map.Entry::getKey).collect(Collectors.toList());
        if (upgradableEnchants.isEmpty()) {
            return;
        }
        Enchantment targetEnchant = (Enchantment)upgradableEnchants.get(new Random().nextInt(upgradableEnchants.size()));
        int currentLevel = meta.getStoredEnchantLevel(targetEnchant);
        float rate = diamond.getConfig().getOverEnchantRate();
        Random random = new Random();
        if (random.nextFloat() <= rate) {
            newLevel = currentLevel + 1;
            success = true;
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 1.0f, 1.0f);
            player.spawnParticle(Particle.NOTE, player.getLocation().add(0.0, 0.25, 0.0), 3, 0.5, 0.25, 0.5);
            player.sendMessage(String.valueOf(ChatColor.GREEN) + "\u6210\u529f\uff01 " + String.valueOf(ChatColor.WHITE) + this.getEnchantName(targetEnchant) + " \u304c Lv." + newLevel + " \u306b\u306a\u308a\u307e\u3057\u305f\uff01");
        } else {
            newLevel = 1;
            success = false;
            player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);
            player.spawnParticle(Particle.ANGRY_VILLAGER, player.getLocation().add(0.0, 0.25, 0.0), 3, 0.5, 0.25, 0.5);
            player.sendMessage(String.valueOf(ChatColor.RED) + "\u5931\u6557... " + String.valueOf(ChatColor.WHITE) + this.getEnchantName(targetEnchant) + " \u304c Lv.1 \u306b\u623b\u3063\u3066\u3057\u307e\u3044\u307e\u3057\u305f...");
        }
        meta.addStoredEnchant(targetEnchant, newLevel, true);
        book.setItemMeta((ItemMeta)meta);
        diamondItem.setAmount(diamondItem.getAmount() - 1);
        this.plugin.getServer().getPluginManager().callEvent((Event)new CompressedDiamondEnhanceLevelEvent(player, null, book, diamond, targetEnchant, currentLevel, newLevel, success));
        this.updateGUI(gui);
    }

    private String getEnchantName(Enchantment enchant) {
        return enchant.getKey().getKey().replace("_", " ");
    }

    private ItemStack createGlassPane(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createInfoItem(Material material, String name, String ... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore.length > 0) {
                meta.setLore(Arrays.asList(lore));
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private static class GUISession {
        private GUISession() {
        }
    }
}
