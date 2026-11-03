package com.github.mdcdi1315.basemodslib.client.gui.about_the_lib.licenses;

import net.minecraft.network.chat.Component;

public record LicenseEntry(
        String license_text,
        Component license_button_text,
        Component license_title_screen_text
) { }
