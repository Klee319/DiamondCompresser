/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.NamespacedKey
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.CraftItemEvent
 *  org.bukkit.event.inventory.PrepareItemCraftEvent
 *  org.bukkit.inventory.CraftingRecipe
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.Recipe
 */
package com.gmail.necnionch.myplugin.diamondcompressor.bukkit.listeners;

import com.gmail.necnionch.myplugin.diamondcompressor.bukkit.CompressedDiamond;
import com.gmail.necnionch.myplugin.diamondcompressor.bukkit.DiamondCompressorPlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class CraftListener
implements Listener {
    private final DiamondCompressorPlugin plugin;

    public CraftListener(DiamondCompressorPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        ItemStack result = event.getInventory().getResult();
        if (result == null) {
            return;
        }
        ItemStack[] matrix = event.getInventory().getMatrix();
        if (this.isDecompressRecipe(matrix)) {
            return;
        }
        CompressedDiamond resultCompressed = CompressedDiamond.from(result);
        if (resultCompressed != null) {
            if (!resultCompressed.getConfig().isCraftable()) {
                event.getInventory().setResult(null);
                return;
            }
            if (resultCompressed == CompressedDiamond.X4) {
                for (ItemStack item : matrix) {
                    if (item == null || CompressedDiamond.from(item) == null) continue;
                    event.getInventory().setResult(null);
                    return;
                }
            }
        }
        if (result.getType() == Material.DIAMOND_BLOCK) {
            for (ItemStack item : matrix) {
                if (item == null || CompressedDiamond.from(item) == null) continue;
                event.getInventory().setResult(null);
                return;
            }
        }
    }

    private boolean isDecompressRecipe(ItemStack[] matrix) {
        int compressedCount = 0;
        int totalCount = 0;
        for (ItemStack item : matrix) {
            if (item == null || item.getType() == Material.AIR) continue;
            ++totalCount;
            if (CompressedDiamond.from(item) == null) continue;
            ++compressedCount;
        }
        return totalCount == 1 && compressedCount == 1;
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onCraftItem(CraftItemEvent event) {
        Recipe recipe = event.getRecipe();
        if (!(recipe instanceof CraftingRecipe)) {
            return;
        }
        CraftingRecipe craftingRecipe = (CraftingRecipe)recipe;
        NamespacedKey key = craftingRecipe.getKey();
        if (!key.getNamespace().equalsIgnoreCase(this.plugin.getName())) {
            return;
        }
        ItemStack result = event.getInventory().getResult();
        if (result == null || result.getType() != Material.DIAMOND) {
            return;
        }
        if (CompressedDiamond.from(result) != null) {
            return;
        }
        String keyName = key.getKey();
        CompressedDiamond correctItem = this.getCompressedDiamondFromRecipeKey(keyName);
        if (correctItem != null) {
            event.getInventory().setResult(correctItem.create(result.getAmount()));
        }
    }

    private CompressedDiamond getCompressedDiamondFromRecipeKey(String keyName) {
        if (keyName.contains("to_d_comped4") && !keyName.contains("comped4_to")) {
            return CompressedDiamond.X4;
        }
        if (keyName.contains("to_d_comped16") && !keyName.contains("comped16_to")) {
            return CompressedDiamond.X16;
        }
        if (keyName.contains("to_d_comped64") && !keyName.contains("comped64_to")) {
            return CompressedDiamond.X64;
        }
        if (keyName.contains("to_d_comped256") && !keyName.contains("comped256_to")) {
            return CompressedDiamond.X256;
        }
        if (keyName.contains("to_d_comped1024")) {
            return CompressedDiamond.X1024;
        }
        if (keyName.equals("d_comped16_to_d_comped4")) {
            return CompressedDiamond.X4;
        }
        if (keyName.equals("d_comped64_to_d_comped16")) {
            return CompressedDiamond.X16;
        }
        if (keyName.equals("d_comped256_to_d_comped64")) {
            return CompressedDiamond.X64;
        }
        if (keyName.equals("d_comped1024_to_d_comped256")) {
            return CompressedDiamond.X256;
        }
        return null;
    }
}
