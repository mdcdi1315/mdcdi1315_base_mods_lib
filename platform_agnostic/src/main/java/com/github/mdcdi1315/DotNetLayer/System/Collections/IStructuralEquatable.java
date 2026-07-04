package com.github.mdcdi1315.DotNetLayer.System.Collections;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

/**
 * Defines methods to support the comparison of objects for structural equality.
 */
public interface IStructuralEquatable
{
    /**
     * Determines whether an object is structurally equal to the current instance.
     * @param other The object to compare with the current instance.
     * @param comparer An object that determines whether the current instance and {@code other} are equal.
     * @return {@code true} if the two objects are equal; otherwise, {@code false}.
     */
    boolean Equals(@AllowNull Object other, IEqualityComparer comparer);

    /**
     * Returns a hash code for the current instance.
     * @param comparer An object that computes the hash code of the current object.
     * @return The hash code for the current instance.
     */
    int GetHashCode(IEqualityComparer comparer);
}
