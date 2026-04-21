package com.github.mdcdi1315.basemodslib.network;

import com.github.mdcdi1315.basemodslib.menu.MenuProviderEx;

import io.netty.buffer.Unpooled;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;

import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public record ExtendedScreenHandlerFactoryTranslation(MenuProviderEx mpx)
        implements ExtendedScreenHandlerFactory<FriendlyByteBuf>
{
    @Override
    public FriendlyByteBuf getScreenOpeningData(ServerPlayer player)
    {
        FriendlyByteBuf ffb = new FriendlyByteBuf(Unpooled.buffer());
        try {
            mpx.WriteScreenOpeningData(player , ffb);
        } catch (Exception e) {
            ffb.release();
            throw e;
        }
        return ffb;
    }

    @Override
    public Component getDisplayName() { return mpx.getDisplayName(); }

    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) { return mpx.createMenu(i , inventory, player); }
}
