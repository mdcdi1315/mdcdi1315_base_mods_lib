package com.github.mdcdi1315.basemodslib.client.gui.about_the_lib;

import com.github.mdcdi1315.basemodslib.BaseModsLibClient;

import com.github.mdcdi1315.basemodslib.config.gui.ConfigurationScreenFactory;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.components.StringWidget;

import net.minecraft.network.chat.Component;

public final class OpenConfigScreenScreen
    extends BaseAboutScreen
{
    private final LinearLayout layout;

    public OpenConfigScreenScreen(Screen parent)
    {
        super(Component.translatable("mdcdi1315_base_mods_lib.about_bml_screen_cfg_screen.title"), parent);
        layout = LinearLayout.vertical();
        boolean is_empty = true;
        try (var en = BaseModsLibClient.GetConfigurationScreens().GetEnumerator())
        {
            while (en.MoveNext())
            {
                var p = en.getCurrent();
                layout.addChild(Button.builder(
                        Component.translatable("mdcdi1315_base_mods_lib.about_bml_screen_cfg_screen.button_open_screen", p.first()),
                        new ConstructScreenData(this, p.second())
                ).build());
                is_empty = false;
            }
        }
        if (is_empty)
        {
            layout.addChild(
                    new StringWidget(Component.translatable("mdcdi1315_base_mods_lib.about_bml_screen_cfg_screen.no_mods_detected"), minecraft.font)
            );
        }
    }

    @Override
    protected void init()
    {
        super.init();

        CreateScrollableAreaInWorkArea(layout);
    }

    private record ConstructScreenData(OpenConfigScreenScreen parent, ConfigurationScreenFactory<?> factory)
        implements Button.OnPress
    {
        @Override
        public void onPress(Button button)
        {
            parent.minecraft.setScreen(factory.Create(parent));
        }
    }
}
