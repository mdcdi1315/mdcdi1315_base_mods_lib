package com.github.mdcdi1315.DotNetLayer.System.Collections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

/**
 * Defines methods to support the comparison of objects for equality.
 */
public interface IEqualityComparer
{
    /**
     * Determines whether the specified objects are equal.
     * @param x The first object to compare.
     * @param y The second object to compare.
     * @return {@code true} if the specified objects are equal; otherwise, {@code false}.
     * @throws ArgumentException {@code x} and {@code y} are of different types and neither one can handle comparisons with the other.
     */
    boolean Equals(@MaybeNull Object x, @MaybeNull Object y) throws ArgumentException;

    /**
     * Returns a hash code for the specified object.
     * @param obj The {@link Object} for which a hash code is to be returned.
     * @return A hash code for the specified object.
     * @throws ArgumentNullException The type of {@code obj} is a reference type and {@code obj} is {@code null}.
     */
    int GetHashCode(Object obj) throws ArgumentNullException;
}
