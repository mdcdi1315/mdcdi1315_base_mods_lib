package com.github.mdcdi1315.basemodslib.client.gui.about_the_lib.licenses;

import com.github.mdcdi1315.basemodslib.client.gui.about_the_lib.BaseAboutScreen;

import net.minecraft.network.chat.Component;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.MultiLineTextWidget;

public final class LicenseViewerScreen
    extends BaseAboutScreen
{
    public LicenseViewerScreen(Screen parent, LicenseEntry entry)
    {
        super(ConstructTitle(entry), parent);
        AddWorkAreaWidget(new MultiLineTextWidget(Component.literal(entry.license_text()), minecraft.font));
    }

    private static Component ConstructTitle(LicenseEntry entry)
    {
        Component c = entry.license_title_screen_text();
        if (c == null) {
            c = Component.literal("License file");
        }
        return c;
    }
}
