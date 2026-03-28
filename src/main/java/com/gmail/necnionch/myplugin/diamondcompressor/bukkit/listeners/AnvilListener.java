/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryOpenEvent
 *  org.bukkit.event.inventory.PrepareAnvilEvent
 *  org.bukkit.inventory.AnvilInventory
 *  org.bukkit.inventory.InventoryView
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.EnchantmentStorageMeta
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.inventory.view.AnvilView
 */
package com.gmail.necnionch.myplugin.diamondcompressor.bukkit.listeners;

import com.gmail.necnionch.myplugin.diamondcompressor.bukkit.DiamondCompressorPlugin;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.view.AnvilView;

public class AnvilListener
implements Listener {
    private final DiamondCompressorPlugin plugin;

    public AnvilListener(DiamondCompressorPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAnvilOpen(InventoryOpenEvent event) {
        InventoryView inventoryView = event.getView();
        if (inventoryView instanceof AnvilView) {
            AnvilView anvilView = (AnvilView)inventoryView;
            anvilView.setMaximumRepairCost(Integer.MAX_VALUE);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        EnchantmentStorageMeta bookMeta;
        ItemMeta itemMeta;
        AnvilInventory inventory = event.getInventory();
        ItemStack firstItem = inventory.getItem(0);
        ItemStack secondItem = inventory.getItem(1);
        if (firstItem == null || secondItem == null) {
            return;
        }
        if (Material.ENCHANTED_BOOK.equals((Object)secondItem.getType()) && (itemMeta = secondItem.getItemMeta()) instanceof EnchantmentStorageMeta && this.hasOverEnchantment(bookMeta = (EnchantmentStorageMeta)itemMeta)) {
            this.handleOverEnchantApply(event, firstItem, secondItem, bookMeta);
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPrepareAnvilRemoveCostLimit(PrepareAnvilEvent event) {
        AnvilView anvilView = event.getView();
        if (!(anvilView instanceof AnvilView)) {
            return;
        }
        AnvilView anvilView2 = anvilView;
        anvilView2.setMaximumRepairCost(Integer.MAX_VALUE);
        if (event.getResult() == null) {
            int currentCost;
            AnvilInventory inventory = event.getInventory();
            ItemStack firstItem = inventory.getItem(0);
            ItemStack secondItem = inventory.getItem(1);
            if (firstItem != null && secondItem != null && (currentCost = anvilView2.getRepairCost()) >= 40) {
                anvilView2.setRepairCost(39);
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPrepareAnvilClampCost(PrepareAnvilEvent event) {
        int cost;
        AnvilView anvilView = event.getView();
        if (!(anvilView instanceof AnvilView)) {
            return;
        }
        AnvilView anvilView2 = anvilView;
        if (event.getResult() != null && (cost = anvilView2.getRepairCost()) > 39) {
            anvilView2.setRepairCost(39);
        }
    }

    private boolean hasOverEnchantment(EnchantmentStorageMeta meta) {
        if (!meta.hasStoredEnchants()) {
            return false;
        }
        return meta.getStoredEnchants().entrySet().stream().anyMatch(e -> (Integer)e.getValue() > ((Enchantment)e.getKey()).getMaxLevel());
    }

    private void handleOverEnchantApply(PrepareAnvilEvent event, ItemStack targetItem, ItemStack enchantBook, EnchantmentStorageMeta bookMeta) {
        boolean bl;
        ItemMeta targetMeta = targetItem.getItemMeta();
        if (targetMeta == null) {
            return;
        }
        ItemStack result = event.getResult() != null ? event.getResult().clone() : targetItem.clone();
        ItemMeta resultMeta = result.getItemMeta();
        if (resultMeta == null) {
            return;
        }
        for (Map.Entry entry : targetMeta.getEnchants().entrySet()) {
            int currentResultLevel;
            Enchantment enchantment = (Enchantment)entry.getKey();
            int targetLevel = (Integer)entry.getValue();
            if (targetLevel <= (currentResultLevel = resultMeta.getEnchantLevel(enchantment))) continue;
            resultMeta.addEnchant(enchantment, targetLevel, true);
        }
        boolean applied = false;
        for (Map.Entry entry : bookMeta.getStoredEnchants().entrySet()) {
            int newLevel;
            int currentLevel;
            Enchantment enchant = (Enchantment)entry.getKey();
            int bookLevel = (Integer)entry.getValue();
            if (!enchant.canEnchantItem(targetItem) && !targetMeta.hasEnchant(enchant) || (currentLevel = resultMeta.getEnchantLevel(enchant)) == bookLevel || (newLevel = Math.max(currentLevel, bookLevel)) <= currentLevel) continue;
            resultMeta.addEnchant(enchant, newLevel, true);
            applied = true;
        }
        if (!applied && !(bl = targetMeta.getEnchants().entrySet().stream().anyMatch(e -> (Integer)e.getValue() > ((Enchantment)e.getKey()).getMaxLevel()))) {
            return;
        }
        result.setItemMeta(resultMeta);
        event.setResult(result);
        AnvilView anvilView = event.getView();
        if (anvilView instanceof AnvilView) {
            AnvilView anvilView2 = anvilView;
            int n = bookMeta.getStoredEnchants().entrySet().stream().mapToInt(e -> Math.max(0, (Integer)e.getValue() - ((Enchantment)e.getKey()).getMaxLevel())).max().orElse(0);
            anvilView2.setRepairCost(Math.min(1 + n * 5, 39));
        }
    }
}
