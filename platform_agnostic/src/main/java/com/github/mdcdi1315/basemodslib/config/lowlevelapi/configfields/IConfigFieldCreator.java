package com.github.mdcdi1315.basemodslib.config.lowlevelapi.configfields;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.config.lowlevelapi.IConfigField;
import com.github.mdcdi1315.basemodslib.config.lowlevelapi.IConfigFieldConstraint;

/**
 * Provides a functional interface for creating configuration fields.
 * @param <TV> The type of the value held by backing field.
 * @since 1.0.15
 */
@FunctionalInterface
public interface IConfigFieldCreator<TV>
{
    /**
     * Creates a new {@link IConfigField} instance from the specified name, value and constraints for the given field.
     * @param name The name of the configuration field.
     * @param comment The comment, if any, of the configuration field.
     * @param value The value of the configuration field.
     * @param constraints The constraints of the configuration field.
     * @return An object implementing the {@link IConfigField} interface.
     */
    @NotNull
    IConfigField<TV> CreateConfigField(String name, String comment, TV value, Iterable<IConfigFieldConstraint<TV>> constraints);
}
