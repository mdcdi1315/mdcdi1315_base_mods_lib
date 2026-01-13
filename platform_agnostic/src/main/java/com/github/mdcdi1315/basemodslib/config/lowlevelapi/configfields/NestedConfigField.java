package com.github.mdcdi1315.basemodslib.config.lowlevelapi.configfields;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

import com.github.mdcdi1315.basemodslib.config.IModConfig;
import com.github.mdcdi1315.basemodslib.config.lowlevelapi.IConfigFieldConstraint;

public final class NestedConfigField
    extends BaseConfigField<IModConfig>
{
    private final IModConfig value;

    /**
     * Constructs a new instance of the {@link NestedConfigField} class.
     *
     * @param name The name of the newly created field.
     * @param value The section value of the newly created field.
     * @param comment The comment, if any, of the newly created field.
     * @param constraints The constraints to apply on the newly created field.
     * @throws ArgumentNullException {@code name} and/or {@code constraints} and/or {@code value} are {@code null}.
     */
    public NestedConfigField(String name, @AllowNull String comment, IModConfig value, Iterable<IConfigFieldConstraint<IModConfig>> constraints)
            throws ArgumentNullException
    {
        super(name, comment, constraints);
        ArgumentNullException.ThrowIfNull(value, "value");
        this.value = value;
    }

    @Override
    public IModConfig GetValue() { return value; }
}
