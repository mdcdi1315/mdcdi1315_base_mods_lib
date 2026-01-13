package com.github.mdcdi1315.basemodslib.config.lowlevelapi;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;

/**
 * Provides a way to define additional constraints on the values that a configuration field can have.
 * @param <T> The type of data defined in the configuration field.
 */
public interface IConfigFieldConstraint<T>
{
    /**
     * Gets a value whether the current constraint instance is satisfied on the specified configuration field.
     * @param field The configuration field to be tested.
     * @return A value whether the configuration field passes the current constraint described by this instance.
     */
    boolean IsSatisfied(@DisallowNull IConfigField<T> field);
}
