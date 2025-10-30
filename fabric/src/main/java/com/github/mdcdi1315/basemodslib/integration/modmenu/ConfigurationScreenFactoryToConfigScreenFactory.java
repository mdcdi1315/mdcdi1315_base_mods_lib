package com.github.mdcdi1315.basemodslib.integration.modmenu;

import com.github.mdcdi1315.basemodslib.config.gui.ConfigurationScreenFactory;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;

import net.minecraft.client.gui.screens.Screen;

public record ConfigurationScreenFactoryToConfigScreenFactory<T extends Screen>(ConfigurationScreenFactory<T> factory)
    implements ConfigScreenFactory<T>
{
    @Override
    public T create(Screen screen) {
        return factory.Create(screen);
    }
}