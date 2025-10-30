package com.github.mdcdi1315.basemodslib.config.gui;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;


import net.minecraft.network.chat.Component;

/**
 * Provides some GUI utilities for configuration files handling. <br />
 * It does mostly provide translation utilities for the field names and configuration values.
 */
public final class ConfigGuiUtils
{
    public static final String TRANSLATABLE_STRING_VAL = "[Translate]";

    private ConfigGuiUtils() {}

    public static Component ConstructConfigTranslatableString(@MaybeNull String str)
    {
        Component component;
        if (str == null) {
            component = Component.empty();
        } else if (str.startsWith(TRANSLATABLE_STRING_VAL)) {
            component = Component.translatable(str.substring(TRANSLATABLE_STRING_VAL.length()));
        } else {
            component = Component.literal(str);
        }
        return component;
    }

    public static Component ConstructRootConfigFieldTranslation(String mod_id, String field_name)
            throws ArgumentException
    {
        ArgumentException.ThrowIfNullOrEmpty(mod_id, "mod_id");
        ArgumentException.ThrowIfNullOrEmpty(field_name, "field_name");
        return Component.translatable(String.format("%s.configuration.%s", mod_id, field_name));
    }
}
