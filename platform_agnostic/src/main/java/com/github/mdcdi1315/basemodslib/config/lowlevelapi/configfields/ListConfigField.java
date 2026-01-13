package com.github.mdcdi1315.basemodslib.config.lowlevelapi.configfields;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.config.ConfigList;
import com.github.mdcdi1315.basemodslib.config.lowlevelapi.IConfigFieldConstraint;

/**
 * Provides an implementation of the {@link com.github.mdcdi1315.basemodslib.config.lowlevelapi.IConfigField} interface that retains a list of possible values.
 * @since 1.0.15
 */
public final class ListConfigField
    extends BaseConfigField<ConfigList>
{
    private final ConfigList list;
    private final Class<?> element_class;

    /**
     * Constructs a new instance of the {@link ListConfigField} class.
     *
     * @param name The name of the newly created field.
     * @param value The list value of the newly created field.
     * @param comment The comment, if any, of the newly created field.
     * @param constraints The constraints to apply on the newly created field.
     * @throws ArgumentNullException {@code name} and/or {@code constraints} are {@code null}.
     */
    public ListConfigField(String name, @AllowNull String comment, @NotNull ConfigList value, @NotNull Class<?> element_class, Iterable<IConfigFieldConstraint<ConfigList>> constraints)
            throws ArgumentNullException
    {
        super(name, comment, constraints);
        ArgumentNullException.ThrowIfNull(value, "value");
        ArgumentNullException.ThrowIfNull(element_class, "element_class");
        this.list = value;
        this.element_class = element_class;
    }

    @NotNull
    @Override
    public ConfigList GetValue() { return list; }

    @NotNull
    public Class<?> GetElementClass() { return element_class; }
}
