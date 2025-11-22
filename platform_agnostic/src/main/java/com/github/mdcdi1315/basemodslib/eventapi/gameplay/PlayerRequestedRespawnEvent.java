package com.github.mdcdi1315.basemodslib.eventapi.gameplay;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.eventapi.IDestroyableIfUnusedEvent;

import net.minecraft.world.entity.player.Player;

/**
 * An event dispatched once the player has been requested to respawn. <br />
 * Although this typically is dispatched in client-only environments, it is left as both-side if in the future respawning has actions done in both sides.
 * @param player The player object requested to be respawned. Do not assume that this object is either a server or a client player.
 * @since 1.0.11
 */
public record PlayerRequestedRespawnEvent(@NotNull Player player)
    implements IDestroyableIfUnusedEvent
{ }
