/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  org.bukkit.ChatColor
 *  org.bukkit.NamespacedKey
 *  org.bukkit.Registry
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.gmail.necnionch.myplugin.diamondcompressor.bukkit.config;

import com.gmail.necnionch.myplugin.diamondcompressor.bukkit.CompressedDiamond;
import com.gmail.necnionch.myplugin.diamondcompressor.common.BukkitConfigDriver;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginConfig
extends BukkitConfigDriver {
    private final Map<NamespacedKey, Integer> enchantLevelLimits = Maps.newHashMap();
    private boolean allowUndefinedOverenchant;
    private boolean lootOverenchantEnabled;
    private int lootOverenchantMaxBonusLevel;
    private final Map<String, Double> lootOverenchantStructures = Maps.newHashMap();

    public PluginConfig(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean onLoaded(FileConfiguration config) {
        if (super.onLoaded(config)) {
            this.loadCompressedItems();
            this.loadEnchantLevelLimits();
            this.loadLootOverenchant();
            return true;
        }
        return false;
    }

    private void loadCompressedItems() {
        for (CompressedDiamond compressed : CompressedDiamond.ITEMS) {
            ConfigurationSection section = this.config.getConfigurationSection(String.format("compressed.x%ddiamond", compressed.getDiamondTotalAmount()));
            if (section != null) {
                compressed.getConfig().craftable = section.getBoolean("craftable", false);
                compressed.getConfig().overEnchantRate = (float)section.getDouble("overenchant-rate", 0.0);
                String text = section.getString("overenchant-lore");
                compressed.getConfig().enhanceLevelLore = text == null || text.isEmpty() ? null : Lists.newArrayList((Object[])ChatColor.translateAlternateColorCodes((char)'&', (String)text).split("\n"));
                continue;
            }
            compressed.getConfig().craftable = false;
            compressed.getConfig().overEnchantRate = 0.0f;
        }
    }

    private void loadEnchantLevelLimits() {
        this.enchantLevelLimits.clear();
        if (!this.config.contains("allow-undefined-overenchant")) {
            this.getLogger().info("[DiamondCompressor] 'allow-undefined-overenchant' が設定にありません。デフォルト値 false を使用します。未定義エンチャントのオーバーエンチャントは無効です。");
        }
        this.allowUndefinedOverenchant = this.config.getBoolean("allow-undefined-overenchant", false);
        ConfigurationSection section = this.config.getConfigurationSection("enchant-level-limits");
        if (section == null) {
            return;
        }
        for (String enchName : section.getKeys(false)) {
            NamespacedKey key = NamespacedKey.minecraft((String)enchName);
            int level = section.getInt(enchName);
            Enchantment enchantment = (Enchantment)Registry.ENCHANTMENT.get(key);
            if (level > 0 && enchantment != null) {
                this.enchantLevelLimits.put(key, level);
                continue;
            }
            this.getLogger().warning("Unknown enchantment: " + enchName);
        }
    }

    public int getMaxEnchantLevel(NamespacedKey enchantKey) {
        return this.enchantLevelLimits.getOrDefault(enchantKey, -1);
    }

    public boolean isAllowEnchantLevelUp(Enchantment enchantment, int level) {
        if (enchantment == null) {
            return false;
        }
        int maxLimit = this.getMaxEnchantLevel(enchantment.getKey());
        if (maxLimit == -1) {
            return this.allowUndefinedOverenchant;
        }
        return level < maxLimit;
    }

    private void loadLootOverenchant() {
        this.lootOverenchantStructures.clear();
        this.lootOverenchantEnabled = this.config.getBoolean("loot-overenchant.enabled", false);
        this.lootOverenchantMaxBonusLevel = this.config.getInt("loot-overenchant.max-bonus-level", 2);
        ConfigurationSection section = this.config.getConfigurationSection("loot-overenchant.structures");
        if (section == null) {
            return;
        }
        for (String key : section.getKeys(true)) {
            double chance = section.getDouble(key);
            if (!(chance > 0.0)) continue;
            this.lootOverenchantStructures.put(key, chance);
        }
    }

    public boolean isLootOverenchantEnabled() {
        return this.lootOverenchantEnabled;
    }

    public int getLootOverenchantMaxBonusLevel() {
        return this.lootOverenchantMaxBonusLevel;
    }

    public Map<String, Double> getLootOverenchantStructures() {
        return this.lootOverenchantStructures;
    }

    public Map<NamespacedKey, Integer> getEnchantLevelLimits() {
        return this.enchantLevelLimits;
    }

    public String[] getOverEnchantLore() {
        String text = this.config.getString("overenchant-lore");
        if (text == null || text.isEmpty()) {
            return null;
        }
        return ChatColor.translateAlternateColorCodes((char)'&', (String)text).split("\n");
    }

    public static class Compressed {
        private boolean craftable;
        private float overEnchantRate;
        private List<String> enhanceLevelLore;

        public Compressed() {
        }

        public Compressed(boolean craftable, float overEnchantRate, List<String> enhanceLevelLore) {
            this.craftable = craftable;
            this.overEnchantRate = overEnchantRate;
            this.enhanceLevelLore = enhanceLevelLore;
        }

        public boolean isCraftable() {
            return this.craftable;
        }

        public float getOverEnchantRate() {
            return this.overEnchantRate;
        }

        public List<String> getEnhanceLevelLore() {
            return this.enhanceLevelLore;
        }
    }
}
