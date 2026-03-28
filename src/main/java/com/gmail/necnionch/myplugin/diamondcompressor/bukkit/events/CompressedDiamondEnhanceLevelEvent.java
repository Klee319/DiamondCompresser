/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 *  org.bukkit.inventory.AnvilInventory
 *  org.bukkit.inventory.ItemStack
 *  org.jetbrains.annotations.NotNull
 */
package com.gmail.necnionch.myplugin.diamondcompressor.bukkit.events;

import com.gmail.necnionch.myplugin.diamondcompressor.bukkit.CompressedDiamond;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CompressedDiamondEnhanceLevelEvent
extends Event {
    public static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final AnvilInventory inventory;
    private final ItemStack source;
    private final CompressedDiamond compressedDiamond;
    private final Enchantment enchantment;
    private final int oldLevel;
    private final int newLevel;
    private final boolean success;

    public CompressedDiamondEnhanceLevelEvent(Player player, AnvilInventory inventory, ItemStack source, CompressedDiamond compressed, Enchantment enchantment, int oldLevel, int newLevel, boolean success) {
        this.player = player;
        this.inventory = inventory;
        this.source = source;
        this.compressedDiamond = compressed;
        this.enchantment = enchantment;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
        this.success = success;
    }

    public Player getPlayer() {
        return this.player;
    }

    public AnvilInventory getInventory() {
        return this.inventory;
    }

    public ItemStack getSourceItemStack() {
        return this.source;
    }

    public CompressedDiamond getCompressedDiamond() {
        return this.compressedDiamond;
    }

    public Enchantment getEnchantment() {
        return this.enchantment;
    }

    public int getOldLevel() {
        return this.oldLevel;
    }

    public int getNewLevel() {
        return this.newLevel;
    }

    public boolean isSuccess() {
        return this.success;
    }

    @NotNull
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
