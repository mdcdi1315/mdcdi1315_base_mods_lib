package com.github.mdcdi1315.basemodslib.config.lowlevelapi;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

/**
 * Provides the abstraction of mod configuration fields. <br />
 * The type parameter {@link T} defines the type of the configuration field to be defined.
 * @since 1.0.15
 * @param <T> The type of data defined in the configuration field.
 */
public interface IConfigField<T>
{
    /**
     * Gets the held value of this configuration field.
     * @return The value of the configuration field.
     */
    @MaybeNull
    T GetValue();

    /**
     * Gets the name of this configuration field.
     * @return The name of the configuration field.
     */
    @NotNull
    String GetName();

    /**
     * Gets the comment value of this configuration field.
     * @return The comment value of the configuration field.
     */
    @NotNull
    String GetComment();

    /**
     * Gets an iterable of configuration field constraints that are applied on the current configuration field.
     * @return The configuration constraints applied on the configuration field.
     */
    @NotNull
    Iterable<IConfigFieldConstraint<T>> GetConstraints();
}
