package com.github.mdcdi1315.basemodslib.eventapi.server;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.basemodslib.eventapi.IDestroyableIfUnusedEvent;
import com.github.mdcdi1315.basemodslib.eventapi.IEvent;
import net.minecraft.server.ReloadableServerResources;

/**
 * Fired when the server's resources have been reloaded. <br />
 * From this event you can get the actual reloaded resources.
 * @param resources The object containing the reloaded data.
 */
public record ServerResourcesReloadedEvent(@NotNull ReloadableServerResources resources) implements IDestroyableIfUnusedEvent { }
