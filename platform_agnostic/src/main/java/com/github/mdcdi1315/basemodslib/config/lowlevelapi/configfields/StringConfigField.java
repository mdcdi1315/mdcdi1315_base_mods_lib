package com.github.mdcdi1315.basemodslib.config.lowlevelapi.configfields;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

import com.github.mdcdi1315.DotNetLayer.System.StringUtils;
import com.github.mdcdi1315.basemodslib.config.lowlevelapi.IConfigFieldConstraint;

/**
 * Provides an implementation of the {@link com.github.mdcdi1315.basemodslib.config.lowlevelapi.IConfigField} interface for string fields.
 * @since 1.0.15
 */
public final class StringConfigField
    extends BaseConfigField<String>
{
    private final String value;

    /**
     * Initializes a new instance of the {@link StringConfigField} class.
     * @param name The name of the newly created field.
     * @param value The string value of the newly created field.
     * @param comment The comment, if any, of the newly created field.
     * @param constraints The constraints to apply on the newly created field.
     * @throws ArgumentNullException {@code name} and/or {@code constraints} are {@code null}.
     */
    public StringConfigField(String name, @AllowNull String comment, @AllowNull String value, Iterable<IConfigFieldConstraint<String>> constraints)
        throws ArgumentNullException
    {
        super(name, comment, constraints);
        this.value = value;
    }

    @Override
    public String GetValue() { return value; }
}
