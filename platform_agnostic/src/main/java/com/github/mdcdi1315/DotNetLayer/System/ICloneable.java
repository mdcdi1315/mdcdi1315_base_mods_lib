package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

/**
 * Supports cloning, which creates a new instance of a class with the same value as an existing instance.
 */
public interface ICloneable
{
    /**
     * Creates a new object that is a copy of the current instance.
     * @return A new object that is a copy of this instance.
     */
    @NotNull // Must be not-null by definition.
    Object Clone();
}
