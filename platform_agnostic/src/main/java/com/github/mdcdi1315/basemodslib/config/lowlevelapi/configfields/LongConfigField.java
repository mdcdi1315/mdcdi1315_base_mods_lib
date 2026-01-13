package com.github.mdcdi1315.basemodslib.config.lowlevelapi.configfields;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

import com.github.mdcdi1315.basemodslib.config.lowlevelapi.IConfigFieldConstraint;

/**
 * Provides an implementation of the {@link com.github.mdcdi1315.basemodslib.config.lowlevelapi.IConfigField} interface for long fields.
 * @since 1.0.15
 */
public final class LongConfigField
    extends NumericConfigField<Long>
{
    private final long value;

    /**
     * Constructs a new instance of the {@link LongConfigField} class.
     *
     * @param name The name of the newly created field.
     * @param comment The comment, if any, of the newly created field.
     * @param constraints The constraints to apply on the newly created field.
     * @throws ArgumentNullException {@code name} and/or {@code constraints} are {@code null}.
     */
    public LongConfigField(String name, @AllowNull String comment, long value, Iterable<IConfigFieldConstraint<Long>> constraints)
            throws ArgumentNullException
    {
        super(name, comment, constraints);
        this.value = value;
    }

    @Override
    public Long GetValue() { return value; }
}
