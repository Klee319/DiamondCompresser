/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.NamespacedKey
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.inventory.meta.components.CustomModelDataComponent
 *  org.bukkit.persistence.PersistentDataContainer
 *  org.bukkit.persistence.PersistentDataType
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.gmail.necnionch.myplugin.diamondcompressor.bukkit;

import com.gmail.necnionch.myplugin.diamondcompressor.bukkit.config.PluginConfig;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CompressedDiamond {
    private static NamespacedKey ITEM_ID_KEY;
    private final String itemId;
    private final int diamondTotalAmount;
    private final String name;
    private final int customModelData;
    private final PluginConfig.Compressed config = new PluginConfig.Compressed();
    public static final CompressedDiamond X4;
    public static final CompressedDiamond X16;
    public static final CompressedDiamond X64;
    public static final CompressedDiamond X256;
    public static final CompressedDiamond X1024;
    public static final CompressedDiamond[] ITEMS;

    public static void init(JavaPlugin plugin) {
        ITEM_ID_KEY = new NamespacedKey((Plugin)plugin, "compressed_diamond_id");
    }

    @Nullable
    public static CompressedDiamond from(@Nullable ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() != Material.DIAMOND) {
            return null;
        }
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return null;
        }
        PersistentDataContainer data = meta.getPersistentDataContainer();
        String itemId = (String)data.get(ITEM_ID_KEY, PersistentDataType.STRING);
        return CompressedDiamond.from(itemId);
    }

    @Nullable
    public static CompressedDiamond from(@Nullable String itemId) {
        if (itemId == null) {
            return null;
        }
        if (CompressedDiamond.X4.itemId.equalsIgnoreCase(itemId)) {
            return X4;
        }
        if (CompressedDiamond.X16.itemId.equalsIgnoreCase(itemId)) {
            return X16;
        }
        if (CompressedDiamond.X64.itemId.equalsIgnoreCase(itemId)) {
            return X64;
        }
        if (CompressedDiamond.X256.itemId.equalsIgnoreCase(itemId)) {
            return X256;
        }
        if (CompressedDiamond.X1024.itemId.equalsIgnoreCase(itemId)) {
            return X1024;
        }
        return null;
    }

    @Nullable
    public static CompressedDiamond fromAmount(int amount) {
        for (CompressedDiamond item : ITEMS) {
            if (item.diamondTotalAmount != amount) continue;
            return item;
        }
        return null;
    }

    @Nullable
    public CompressedDiamond getNextLevel() {
        for (int i = 0; i < ITEMS.length - 1; ++i) {
            if (ITEMS[i] != this) continue;
            return ITEMS[i + 1];
        }
        return null;
    }

    @Nullable
    public CompressedDiamond getPreviousLevel() {
        for (int i = 1; i < ITEMS.length; ++i) {
            if (ITEMS[i] != this) continue;
            return ITEMS[i - 1];
        }
        return null;
    }

    public CompressedDiamond(String itemId, int diamondTotalAmount, String name, int customModelData) {
        this.itemId = itemId;
        this.diamondTotalAmount = diamondTotalAmount;
        this.name = name;
        this.customModelData = customModelData;
    }

    @NotNull
    public String getItemId() {
        return this.itemId;
    }

    @NotNull
    public Material getMaterial() {
        return Material.DIAMOND;
    }

    public int getDiamondTotalAmount() {
        return this.diamondTotalAmount;
    }

    public String getName() {
        return this.name;
    }

    public int getCustomModelData() {
        return this.customModelData;
    }

    @NotNull
    public ItemStack create() {
        return this.create(1);
    }

    @NotNull
    public ItemStack create(int amount) {
        ItemStack itemStack = new ItemStack(this.getMaterial(), amount);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, (Object)this.itemId);
            meta.setDisplayName(String.valueOf(ChatColor.AQUA) + this.name);
            meta.setLore(List.of(String.valueOf(ChatColor.GRAY) + "\u30c0\u30a4\u30e4\u30e2\u30f3\u30c9 " + this.diamondTotalAmount + "\u500b\u5206", String.valueOf(ChatColor.DARK_GRAY) + "\u30af\u30e9\u30d5\u30c8\u3067\u5c55\u958b\u53ef\u80fd"));
            CustomModelDataComponent cmdComponent = meta.getCustomModelDataComponent();
            cmdComponent.setFloats(List.of(Float.valueOf(this.customModelData)));
            meta.setCustomModelDataComponent(cmdComponent);
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }

    public void applyTo(@NotNull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, (Object)this.itemId);
            meta.setDisplayName(String.valueOf(ChatColor.AQUA) + this.name);
            meta.setLore(List.of(String.valueOf(ChatColor.GRAY) + "\u30c0\u30a4\u30e4\u30e2\u30f3\u30c9 " + this.diamondTotalAmount + "\u500b\u5206", String.valueOf(ChatColor.DARK_GRAY) + "\u30af\u30e9\u30d5\u30c8\u3067\u5c55\u958b\u53ef\u80fd"));
            CustomModelDataComponent cmdComponent = meta.getCustomModelDataComponent();
            cmdComponent.setFloats(List.of(Float.valueOf(this.customModelData)));
            meta.setCustomModelDataComponent(cmdComponent);
            itemStack.setItemMeta(meta);
        }
    }

    public boolean matches(@Nullable ItemStack itemStack) {
        CompressedDiamond found = CompressedDiamond.from(itemStack);
        return found == this;
    }

    public PluginConfig.Compressed getConfig() {
        return this.config;
    }

    static {
        X4 = new CompressedDiamond("compressed_diamond_x4", 4, "4\u500d\u5727\u7e2e\u30c0\u30a4\u30e4", 1001);
        X16 = new CompressedDiamond("compressed_diamond_x16", 16, "16\u500d\u5727\u7e2e\u30c0\u30a4\u30e4", 1002);
        X64 = new CompressedDiamond("compressed_diamond_x64", 64, "64\u500d\u5727\u7e2e\u30c0\u30a4\u30e4", 1003);
        X256 = new CompressedDiamond("compressed_diamond_x256", 256, "256\u500d\u5727\u7e2e\u30c0\u30a4\u30e4", 1004);
        X1024 = new CompressedDiamond("compressed_diamond_x1024", 1024, "1024\u500d\u5727\u7e2e\u30c0\u30a4\u30e4", 1005);
        ITEMS = new CompressedDiamond[]{X4, X16, X64, X256, X1024};
    }
}
