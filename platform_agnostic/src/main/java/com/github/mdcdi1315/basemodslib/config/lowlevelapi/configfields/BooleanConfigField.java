package com.github.mdcdi1315.basemodslib.config.lowlevelapi.configfields;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

import com.github.mdcdi1315.basemodslib.config.lowlevelapi.IConfigFieldConstraint;

/**
 * Provides an implementation of the {@link com.github.mdcdi1315.basemodslib.config.lowlevelapi.IConfigField} interface for boolean fields.
 * @since 1.0.15
 */
public final class BooleanConfigField
    extends BaseConfigField<Boolean>
{
    private final boolean value;

    /**
     * Constructs a new instance of the {@link BooleanConfigField} class.
     *
     * @param name The name of the newly created field.
     * @param value The boolean value of the newly created field.
     * @param comment The comment, if any, of the newly created field.
     * @param constraints The constraints to apply on the newly created field.
     * @throws ArgumentNullException {@code name} and/or {@code constraints} are {@code null}.
     */
    public BooleanConfigField(String name, @AllowNull String comment, boolean value, Iterable<IConfigFieldConstraint<Boolean>> constraints)
            throws ArgumentNullException
    {
        super(name, comment, constraints);
        this.value = value;
    }

    @Override
    public Boolean GetValue() { return value; }
}
