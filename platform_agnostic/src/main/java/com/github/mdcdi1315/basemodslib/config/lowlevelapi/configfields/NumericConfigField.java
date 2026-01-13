package com.github.mdcdi1315.basemodslib.config.lowlevelapi.configfields;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.basemodslib.config.lowlevelapi.IConfigFieldConstraint;

import org.jetbrains.annotations.ApiStatus;

/**
 * Provides a base class for numeric configuration fields. <br />
 * Users of the library should not derive from this class; it is meant to be used only by the library infrastructure itself.
 * @param <T>
 */
@ApiStatus.Internal
public abstract class NumericConfigField<T extends Number>
    extends BaseConfigField<T>
{
    /**
     * Constructs a new instance of the {@link NumericConfigField} class.
     *
     * @param name        The name of the newly created field.
     * @param constraints The constraints to apply on the newly created field.
     * @throws ArgumentNullException {@code name} and/or {@code constraints} are {@code null}.
     */
    protected NumericConfigField(String name, Iterable<IConfigFieldConstraint<T>> constraints)
            throws ArgumentNullException
    {
        super(name, constraints);
    }

    /**
     * Constructs a new instance of the {@link NumericConfigField} class.
     *
     * @param name The name of the newly created field.
     * @param comment The comment, if any, of the newly created field.
     * @param constraints The constraints to apply on the newly created field.
     * @throws ArgumentNullException {@code name} and/or {@code constraints} are {@code null}.
     */
    protected NumericConfigField(String name, @AllowNull String comment, Iterable<IConfigFieldConstraint<T>> constraints)
            throws ArgumentNullException
    {
        super(name, comment, constraints);
    }
}
