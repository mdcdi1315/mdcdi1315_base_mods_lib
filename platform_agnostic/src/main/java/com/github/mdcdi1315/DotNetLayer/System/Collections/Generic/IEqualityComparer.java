package com.github.mdcdi1315.DotNetLayer.System.Collections.Generic;

import com.github.mdcdi1315.DotNetLayer.ByRefParameterType;
import com.github.mdcdi1315.DotNetLayer.DotNetByRefParameter;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;

/**
 * Defines methods to support the comparison of objects for equality.
 * @param <T> The type of objects to compare.
 */
public interface IEqualityComparer<@DotNetByRefParameter(ByRefParameterType.IN) T>
{
    /**
     * Determines whether the specified objects are equal.
     * @param x The first object of type {@link T} to compare.
     * @param y The second object of type {@link T} to compare.
     * @return {@code true} if the specified objects are equal; otherwise, {@code false}.
     */
    boolean Equals(@AllowNull T x, @AllowNull T y);

    /**
     * Returns a hash code for the specified object.
     * @param obj The {@link Object} for which a hash code is to be returned.
     * @return A hash code for the specified object.
     * @exception com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException The type of {@code obj} is a reference type and obj is null.
     */
    int GetHashCode(@DisallowNull T obj);
}