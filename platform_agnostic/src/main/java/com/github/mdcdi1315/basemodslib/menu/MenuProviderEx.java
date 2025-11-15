package com.github.mdcdi1315.basemodslib.menu;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;

import net.minecraft.world.MenuProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

/**
 * An extended variant of the {@link MenuProvider}, providing support for sending additional data when dispatching a 'menu screen open' packet.
 * @since 1.0.6
 */
public interface MenuProviderEx
    extends MenuProvider
{
    /**
     * Provides the method implementation that will write the additional data to the specified friendly byte buffer.
     * @param player The server player that dispatches the 'screen open' packet.
     * @param buf The network buffer to write additional data to.
     */
    void WriteScreenOpeningData(@DisallowNull ServerPlayer player, @DisallowNull FriendlyByteBuf buf);
}
