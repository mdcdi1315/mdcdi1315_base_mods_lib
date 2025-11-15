package com.github.mdcdi1315.basemodslib.menu;

import com.github.mdcdi1315.DotNetLayer.System.Func1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

public final class ForgeMenuTypeRegistrar
    implements IMenuTypeRegistrar
{
    private DeferredRegister<MenuType<?>> MENU_TYPE_REGISTER;

    public ForgeMenuTypeRegistrar(String mod_id) {
        MENU_TYPE_REGISTER = DeferredRegister.create(ForgeRegistries.MENU_TYPES , mod_id);
    }

    private record MenuCreaterToMenuSupplier<T extends AbstractContainerMenu>(MenuTypeCreater<T> crt)
        implements MenuType.MenuSupplier<T>
    {
        @Override
        public T create(int i, Inventory inventory) {
            return crt.Create(i , inventory);
        }
    }

    private record MenuTypeSupplier<T extends AbstractContainerMenu>(MenuTypeRegistrationInfo<T> info)
        implements Func1<MenuType<T>>
    {
        @Override
        public MenuType<T> function() {
            return new MenuType<>(new MenuCreaterToMenuSupplier<>(info.creater()) , info.required_features());
        }
    }

    @Override
    public <T extends AbstractContainerMenu> void Register(String name, MenuTypeRegistrationInfo<T> info)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(name, "name");
        ArgumentNullException.ThrowIfNull(info, "info");

        MENU_TYPE_REGISTER.register(name , new MenuTypeSupplier<>(info));
    }

    public void RegisterToEventBus(IEventBus evb) {
        MENU_TYPE_REGISTER.register(evb);
        MENU_TYPE_REGISTER = null;
    }
}
