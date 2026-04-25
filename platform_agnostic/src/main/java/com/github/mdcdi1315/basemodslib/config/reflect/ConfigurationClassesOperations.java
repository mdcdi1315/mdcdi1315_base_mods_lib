package com.github.mdcdi1315.basemodslib.config.reflect;

import com.github.mdcdi1315.DotNetLayer.System.Exception;
import com.github.mdcdi1315.DotNetLayer.System.StringUtils;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.config.IModConfig;
import com.github.mdcdi1315.basemodslib.utils.collections.SingleLinkedList;
import com.github.mdcdi1315.basemodslib.utils.collections.ITraversableCollection;

import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Provides common operations for configuration classes.
 */
public final class ConfigurationClassesOperations
{
    private ConfigurationClassesOperations() {}

    public static final String TRANSLATABLE_STRING_VAL = "[Translate]";

    /**
     * Gets a traversable collection of reflected configuration fields, over a specified configuration class.
     * @param config_class The configuration class to lookup it's fields.
     * @return An {@link ITraversableCollection} of {@link ReflectedConfigFieldData}.
     */
    @NotNull
    public static ITraversableCollection<ReflectedConfigFieldData> GetConfigFields(Class<? extends IModConfig> config_class)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(config_class, "config_class");
        SingleLinkedList<ReflectedConfigFieldData> d = new SingleLinkedList<>();

        for (Field f : config_class.getFields())
        {
            try {
                if (Modifier.isStatic(f.getModifiers())) { continue; } // Avoid static fields to be looked up to reflected config field data.
                d.Add(new ReflectedConfigFieldData(f));
            } catch (Exception ex) {
                BaseModsLib.LOGGER.warn(
                        StringUtils.Format(
                                "Could not register config field of config class \"{0}\" named \"{1}\" due to an exception.",
                                config_class.getName(),
                                f.getName()
                        ),
                        ex
                );
            }
        }

        return d;
    }

    /**
     * Constructs a {@link Component} from the specified configuration string.
     * @param str The string to construct the component from.
     * @return The constructed {@link Component}.
     */
    @NotNull
    public static Component ConstructComponentFromConfigString(@AllowNull String str)
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
}
