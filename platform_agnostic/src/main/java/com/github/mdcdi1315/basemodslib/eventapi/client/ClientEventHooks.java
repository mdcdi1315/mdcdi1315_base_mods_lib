package com.github.mdcdi1315.basemodslib.eventapi.client;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.BaseModsLibClient;

import com.github.mdcdi1315.basemodslib.utils.annotations.MixinUnsafe;
import net.minecraft.client.Minecraft;

import org.jetbrains.annotations.ApiStatus;

/**
 * Internal event hooks for the Minecraft client.
 * Do not use by your code as this is subject to change without notice.
 */
@ApiStatus.Internal
public final class ClientEventHooks
{
    // Do not let anyone be able to instantiate this class.
    private ClientEventHooks() {}

    @MixinUnsafe
    public static void ClientStopping(Minecraft mc)
    {
        BaseModsLib.GetEventsManager().FireEvent(new ClientStoppingEvent(mc));
        BaseModsLib.DestroySelf();
        BaseModsLibClient.DestroySelf();
    }

    @MixinUnsafe
    public static void ClientStarted(Minecraft mc)
    {
        BaseModsLib.GetEventsManager().FireEvent(new ClientStartedEvent(mc));
    }
}
