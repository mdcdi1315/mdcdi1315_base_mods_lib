package com.github.mdcdi1315.basemodslib.eventapi.gameplay;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.eventapi.IDestroyableIfUnusedEvent;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * Event that is fired when a player manages to kill any living entity.
 * @param player The player that killed the entity.
 * @param entity The entity killed by the player.
 * @since 1.0.11
 */
public record PlayerKilledEntityEvent(@NotNull Player player, @NotNull LivingEntity entity)
    implements IDestroyableIfUnusedEvent
{ }
