package com.github.mdcdi1315.basemodslib.config.lowlevelapi.configfields;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

import com.github.mdcdi1315.basemodslib.config.lowlevelapi.IConfigFieldConstraint;

/**
 * Provides an implementation of the {@link com.github.mdcdi1315.basemodslib.config.lowlevelapi.IConfigField} interface for int fields.
 * @since 1.0.15
 */
public final class IntConfigField
    extends NumericConfigField<Integer>
{
    private final int value;

    /**
     * Constructs a new instance of the {@link IntConfigField} class.
     *
     * @param name The name of the newly created field.
     * @param value The integer value of the newly created field.
     * @param comment The comment, if any, of the newly created field.
     * @param constraints The constraints to apply on the newly created field.
     * @throws ArgumentNullException {@code name} and/or {@code constraints} are {@code null}.
     */
    public IntConfigField(String name, @AllowNull String comment, int value, Iterable<IConfigFieldConstraint<Integer>> constraints)
            throws ArgumentNullException
    {
        super(name, comment, constraints);
        this.value = value;
    }

    @Override
    public Integer GetValue() { return value; }
}
