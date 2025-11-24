package com.github.mdcdi1315.basemodslib.eventapi.gameplay;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.eventapi.IDestroyableIfUnusedEvent;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;

/**
 * Event dispatched when a player is killed. <br />
 * The event does also provide which is the direct source of killing this player.
 * @param player The player object that was killed.
 * @param source The damage source object providing which is the cause of killing the player.
 * @since 1.0.11
 */
public record PlayerWasKilledEvent(@NotNull Player player, @NotNull DamageSource source)
    implements IDestroyableIfUnusedEvent
{ }
