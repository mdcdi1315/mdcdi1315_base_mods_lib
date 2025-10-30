package com.github.mdcdi1315.basemodslib.forge;

import com.github.mdcdi1315.basemodslib.config.gui.ConfigurationScreenFactory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import net.minecraftforge.client.ConfigScreenHandler;

import java.util.function.Supplier;
import java.util.function.BiFunction;

public record ConfigScreenFactorySupplierImplementation<T extends Screen>(ConfigurationScreenFactory<T> factory)
    implements Supplier<ConfigScreenHandler.ConfigScreenFactory>
{
    private record FunctionImplementation<T extends Screen>(ConfigurationScreenFactory<T> factory)
        implements BiFunction<Minecraft, Screen, Screen>
    {
        @Override
        public T apply(Minecraft minecraft, Screen screen) {
            return factory.Create(screen);
        }
    }

    @Override
    public ConfigScreenHandler.ConfigScreenFactory get() {
        return new ConfigScreenHandler.ConfigScreenFactory(new FunctionImplementation<>(factory));
    }
}
