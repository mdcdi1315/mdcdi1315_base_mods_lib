package com.github.mdcdi1315.basemodslib.eventapi.gameplay;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.eventapi.IDestroyableIfUnusedEvent;

import net.minecraft.stats.Stat;
import net.minecraft.world.entity.player.Player;

/**
 * Provides an event fired when the player will be awarded by the requirements of the passed in stat. <br />
 * You can use this to determine additional actions to take, if needed so.
 * @param player The player that will be awarded.
 * @param stat The stat that will be updated.
 * @param incremented_by The value by the current stat will be updated by.
 * @since 1.0.11
 */
public record PlayerWillBeRewardedWithStatEvent(@NotNull Player player, @NotNull Stat<?> stat, int incremented_by)
    implements IDestroyableIfUnusedEvent
{ }
