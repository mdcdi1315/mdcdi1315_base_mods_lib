package com.github.mdcdi1315.basemodslib.config.reflect.constraints;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;

/**
 * Provides the basis for declaring configuration field constraint implementations.
 */
public interface IConfigFieldConstraint
{
    boolean IsSatisfied(@DisallowNull Object raw_value);

    @NotNull
    Class<?> AppliesTo();
}
