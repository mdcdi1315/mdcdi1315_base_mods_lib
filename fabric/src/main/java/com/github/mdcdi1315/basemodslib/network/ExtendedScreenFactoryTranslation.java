package com.github.mdcdi1315.basemodslib.network;


import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;

import com.github.mdcdi1315.basemodslib.menu.MenuProviderEx;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;

import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public final class ExtendedScreenFactoryTranslation
    implements ExtendedScreenHandlerFactory
{
    private final MenuProviderEx wrapping;

    public ExtendedScreenFactoryTranslation(MenuProviderEx wrap) { wrapping = wrap; }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) { wrapping.WriteScreenOpeningData(player, buf); }

    @Override
    public Component getDisplayName() { return wrapping.getDisplayName(); }

    @Override
    public @MaybeNull AbstractContainerMenu createMenu(int id, @DisallowNull Inventory inventory, @DisallowNull Player player) { return wrapping.createMenu(id, inventory , player); }
}
