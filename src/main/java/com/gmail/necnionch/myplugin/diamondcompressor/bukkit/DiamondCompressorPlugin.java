/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.NamespacedKey
 *  org.bukkit.event.Listener
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.Recipe
 *  org.bukkit.inventory.RecipeChoice
 *  org.bukkit.inventory.RecipeChoice$ExactChoice
 *  org.bukkit.inventory.ShapedRecipe
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.gmail.necnionch.myplugin.diamondcompressor.bukkit;

import com.gmail.necnionch.myplugin.diamondcompressor.bukkit.CompressedDiamond;
import com.gmail.necnionch.myplugin.diamondcompressor.bukkit.config.PluginConfig;
import com.gmail.necnionch.myplugin.diamondcompressor.bukkit.gui.EnchantUpgradeGUI;
import com.gmail.necnionch.myplugin.diamondcompressor.bukkit.listeners.AnvilListener;
import com.gmail.necnionch.myplugin.diamondcompressor.bukkit.listeners.CraftListener;
import com.gmail.necnionch.myplugin.diamondcompressor.bukkit.listeners.LootChestListener;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class DiamondCompressorPlugin
extends JavaPlugin {
    private final PluginConfig pluginConfig = new PluginConfig(this);

    public void onEnable() {
        CompressedDiamond.init(this);
        this.pluginConfig.load();
        this.getServer().getPluginManager().registerEvents((Listener)new EnchantUpgradeGUI(this), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new AnvilListener(this), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new CraftListener(this), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new LootChestListener(this), (Plugin)this);
        this.registerRecipes();
        this.getLogger().info("DiamondCompressor v" + this.getDescription().getVersion() + " enabled!");
    }

    public void onDisable() {
        this.unregisterRecipes();
        this.getLogger().info("DiamondCompressor disabled.");
    }

    public PluginConfig getPluginConfig() {
        return this.pluginConfig;
    }

    private void registerRecipes() {
        this.registerCompressRecipe("d4_to_d_comped4", Material.DIAMOND, CompressedDiamond.X4);
        this.registerDecompressRecipe("d_comped4_to_d4", CompressedDiamond.X4, Material.DIAMOND, 4);
        this.registerCompressRecipe("d_comped4_to_d_comped16", CompressedDiamond.X4, CompressedDiamond.X16);
        this.registerDecompressRecipe("d_comped16_to_d_comped4", CompressedDiamond.X16, CompressedDiamond.X4, 4);
        this.registerCompressRecipe("d_comped16_to_d_comped64", CompressedDiamond.X16, CompressedDiamond.X64);
        this.registerDecompressRecipe("d_comped64_to_d_comped16", CompressedDiamond.X64, CompressedDiamond.X16, 4);
        this.registerCompressRecipe("d_comped64_to_d_comped256", CompressedDiamond.X64, CompressedDiamond.X256);
        this.registerDecompressRecipe("d_comped256_to_d_comped64", CompressedDiamond.X256, CompressedDiamond.X64, 4);
        this.registerCompressRecipe("d_comped256_to_d_comped1024", CompressedDiamond.X256, CompressedDiamond.X1024);
        this.registerDecompressRecipe("d_comped1024_to_d_comped256", CompressedDiamond.X1024, CompressedDiamond.X256, 4);
        this.getLogger().info("Registered " + CompressedDiamond.ITEMS.length * 2 + " recipes.");
    }

    private void registerCompressRecipe(String key, Material ingredient, CompressedDiamond result) {
        NamespacedKey recipeKey = new NamespacedKey((Plugin)this, key);
        ShapedRecipe recipe = new ShapedRecipe(recipeKey, result.create());
        recipe.shape(new String[]{"dd", "dd"});
        recipe.setIngredient('d', ingredient);
        Bukkit.addRecipe((Recipe)recipe);
    }

    private void registerCompressRecipe(String key, CompressedDiamond ingredient, CompressedDiamond result) {
        NamespacedKey recipeKey = new NamespacedKey((Plugin)this, key);
        ShapedRecipe recipe = new ShapedRecipe(recipeKey, result.create());
        recipe.shape(new String[]{"dd", "dd"});
        recipe.setIngredient('d', (RecipeChoice)new RecipeChoice.ExactChoice(ingredient.create()));
        Bukkit.addRecipe((Recipe)recipe);
    }

    private void registerDecompressRecipe(String key, CompressedDiamond ingredient, Material result, int amount) {
        NamespacedKey recipeKey = new NamespacedKey((Plugin)this, key);
        ShapedRecipe recipe = new ShapedRecipe(recipeKey, new ItemStack(result, amount));
        recipe.shape(new String[]{"c"});
        recipe.setIngredient('c', (RecipeChoice)new RecipeChoice.ExactChoice(ingredient.create()));
        Bukkit.addRecipe((Recipe)recipe);
    }

    private void registerDecompressRecipe(String key, CompressedDiamond ingredient, CompressedDiamond result, int amount) {
        NamespacedKey recipeKey = new NamespacedKey((Plugin)this, key);
        ShapedRecipe recipe = new ShapedRecipe(recipeKey, result.create(amount));
        recipe.shape(new String[]{"c"});
        recipe.setIngredient('c', (RecipeChoice)new RecipeChoice.ExactChoice(ingredient.create()));
        Bukkit.addRecipe((Recipe)recipe);
    }

    private void unregisterRecipes() {
        Iterator it = Bukkit.recipeIterator();
        while (it.hasNext()) {
            ShapedRecipe shaped;
            Recipe recipe = (Recipe)it.next();
            if (!(recipe instanceof ShapedRecipe) || !(shaped = (ShapedRecipe)recipe).getKey().getNamespace().equals(this.getName().toLowerCase())) continue;
            it.remove();
        }
    }
}
