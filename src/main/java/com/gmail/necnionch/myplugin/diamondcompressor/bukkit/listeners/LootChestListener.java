/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.NamespacedKey
 *  org.bukkit.Registry
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.world.LootGenerateEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.EnchantmentStorageMeta
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.loot.LootTable
 */
package com.gmail.necnionch.myplugin.diamondcompressor.bukkit.listeners;

import com.gmail.necnionch.myplugin.diamondcompressor.bukkit.DiamondCompressorPlugin;
import com.gmail.necnionch.myplugin.diamondcompressor.bukkit.config.PluginConfig;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.loot.LootTable;

public class LootChestListener
implements Listener {
    private static final String LOOT_TABLE_PREFIX = "minecraft:chests/";
    private final DiamondCompressorPlugin plugin;
    private final Random random = new Random();

    public LootChestListener(DiamondCompressorPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLootGenerate(LootGenerateEvent event) {
        PluginConfig config = this.plugin.getPluginConfig();
        if (!config.isLootOverenchantEnabled()) {
            return;
        }
        LootTable lootTable = event.getLootTable();
        String tableKey = lootTable.getKey().toString();
        if (!tableKey.startsWith(LOOT_TABLE_PREFIX)) {
            return;
        }
        String structureKey = tableKey.substring(LOOT_TABLE_PREFIX.length());
        Map<String, Double> structures = config.getLootOverenchantStructures();
        Double chance = structures.get(structureKey);
        if (chance == null) {
            return;
        }
        if (this.random.nextDouble() > chance) {
            return;
        }
        ItemStack overEnchantBook = this.createOverEnchantBook(config);
        if (overEnchantBook != null) {
            event.getLoot().add(overEnchantBook);
        }
    }

    private ItemStack createOverEnchantBook(PluginConfig config) {
        Map<NamespacedKey, Integer> limits = config.getEnchantLevelLimits();
        int maxBonus = config.getLootOverenchantMaxBonusLevel();
        ArrayList<EnchantCandidate> candidates = new ArrayList<EnchantCandidate>();
        for (Map.Entry<NamespacedKey, Integer> entry : limits.entrySet()) {
            Enchantment enchantment = (Enchantment)Registry.ENCHANTMENT.get(entry.getKey());
            if (enchantment == null) continue;
            int defaultMax = enchantment.getMaxLevel();
            int configMax = entry.getValue();
            if (configMax <= defaultMax) continue;
            candidates.add(new EnchantCandidate(enchantment, defaultMax, configMax));
        }
        if (candidates.isEmpty()) {
            return null;
        }
        EnchantCandidate chosen = (EnchantCandidate)candidates.get(this.random.nextInt(candidates.size()));
        int minLevel = chosen.defaultMax + 1;
        int maxLevel = Math.min(chosen.defaultMax + maxBonus, chosen.configMax);
        int level = minLevel + this.random.nextInt(maxLevel - minLevel + 1);
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta)book.getItemMeta();
        if (meta == null) {
            return null;
        }
        meta.addStoredEnchant(chosen.enchantment, level, true);
        book.setItemMeta((ItemMeta)meta);
        return book;
    }

    private record EnchantCandidate(Enchantment enchantment, int defaultMax, int configMax) {
    }
}
