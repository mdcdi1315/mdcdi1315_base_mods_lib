package com.github.mdcdi1315.basemodslib.registries;

import com.github.mdcdi1315.basemodslib.menu.MenuTypeCreaterEx;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

record MenuCreaterExToExtendedFactory<T extends AbstractContainerMenu>(MenuTypeCreaterEx<T> crt)
            implements ExtendedScreenHandlerType.ExtendedFactory<T, FriendlyByteBuf>
{
    @Override
    public T create(int syncId, Inventory inventory, FriendlyByteBuf buf)
    {
        T instance;
        try {
            instance = crt.Create(syncId, inventory, buf);
        } finally {
            buf.release();
        }
        return instance;
    }
}