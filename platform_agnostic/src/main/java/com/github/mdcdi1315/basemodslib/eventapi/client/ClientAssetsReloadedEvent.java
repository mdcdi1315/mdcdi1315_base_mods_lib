package com.github.mdcdi1315.basemodslib.eventapi.client;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.annotations.APIStatus;
import com.github.mdcdi1315.basemodslib.eventapi.IDestroyableIfUnusedEvent;

import net.minecraft.client.Minecraft;

/**
 * Event that is fired when the Minecraft client's resources have been reloaded.
 * @param minecraft The Minecraft client object instance.
 * @since 1.0.40-beta7
 */
@APIStatus(Status = APIStatus.Status.STABLE, Since = "1.0.40-beta7")
public record ClientAssetsReloadedEvent(@NotNull Minecraft minecraft) implements IDestroyableIfUnusedEvent { }