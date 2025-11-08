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

    // Do not let anyone instantiate this class.
    private ConfigGuiUtils() {}

    /**
     * Constructs a chat component from the given config translatable string. <br />
     * See {@link com.github.mdcdi1315.basemodslib.config.ConfigField#comment()} method for more information on how this is used.
     * @param str The string to create a translation for, if needed.
     * @return The created chat component.
     */
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

    /**
     * Constructs a chat component for a config field. The parameters denote from which mod and field to construct the chat component from.
     * @param mod_id The ID of the mod managing the configuration file.
     * @param field_name The name of the configuration file field.
     * @return The constructed chat component.
     * @throws ArgumentException {@code mod_id} and/or {@code field_name} are {@code null}.
     */
    public static Component ConstructRootConfigFieldTranslation(String mod_id, String field_name)
            throws ArgumentException
    {
        ArgumentException.ThrowIfNullOrEmpty(mod_id, "mod_id");
        ArgumentException.ThrowIfNullOrEmpty(field_name, "field_name");
        return Component.translatable(String.format("%s.configuration.%s", mod_id, field_name));
    }
}
