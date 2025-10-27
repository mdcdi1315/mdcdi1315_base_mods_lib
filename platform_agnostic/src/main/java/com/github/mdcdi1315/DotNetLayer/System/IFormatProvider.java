package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

/**
 * Provides a mechanism for retrieving an object to control formatting.
 */
public interface IFormatProvider
{
    /**
     * Returns an object that provides formatting services for the specified type.
     * @param formatType An object that specifies the type of format object to return.
     * @return An instance of the object specified by {@code formatType}, if the {@link IFormatProvider} implementation can supply that type of object; otherwise, {@code null}.
     */
    @MaybeNull
    Object GetFormat(@MaybeNull Class<?> formatType);
}
