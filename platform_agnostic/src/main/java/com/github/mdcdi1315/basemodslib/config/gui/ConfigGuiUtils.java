package com.github.mdcdi1315.basemodslib.config.gui;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.config.reflect.ConfigurationClassesOperations;

import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

/**
 * Provides some GUI utilities for configuration files handling. <br />
 * It does mostly provide translation utilities for the field names and configuration values.
 */
public final class ConfigGuiUtils
{
    public static final String TRANSLATABLE_STRING_VAL = ConfigurationClassesOperations.TRANSLATABLE_STRING_VAL;

    private ConfigGuiUtils() {}

    public static Component ConstructConfigTranslatableString(@MaybeNull String str) {
        return ConfigurationClassesOperations.ConstructComponentFromConfigString(str);
    }

    public static Optional<Component[]> ConstructCommentComponentLines(@MaybeNull String s) {
        if (s == null) {
            return Optional.empty();
        } else {
            return ConstructCommentComponentLines(ConstructConfigTranslatableString(s));
        }
    }

    public static Optional<Component[]> ConstructCommentComponentLines(@MaybeNull Component cc)
    {
        if (cc == null) {
            return Optional.empty();
        } else {
            List<Component> ccg = new ArrayList<>();
            StringBuilder builder = new StringBuilder(500);
            for (Character c : cc.getString().toCharArray()) {
                if (c == '\n') {
                    ccg.add(Component.literal(builder.toString()));
                    builder.setLength(0);
                } else {
                    builder.append(c.charValue());
                }
            }
            if (!builder.isEmpty()) {
                ccg.add(Component.literal(builder.toString()));
            }
            Component[] components = new Component[ccg.size()];
            return Optional.of(ccg.toArray(components));
        }
    }

    public static Component ConstructRootConfigFieldTranslation(String cfg_name, String field_name)
            throws ArgumentException
    {
        ArgumentException.ThrowIfNullOrEmpty(cfg_name, "cfg_name");
        ArgumentException.ThrowIfNullOrEmpty(field_name, "field_name");
        return Component.translatable(String.format("configuration.%s.%s", cfg_name.replace('-', '_'), field_name));
    }
}
