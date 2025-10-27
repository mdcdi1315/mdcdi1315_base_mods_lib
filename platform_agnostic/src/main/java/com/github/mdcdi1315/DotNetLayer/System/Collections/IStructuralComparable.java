package com.github.mdcdi1315.DotNetLayer.System.Collections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

/**
 * Supports the structural comparison of collection objects.
 */
public interface IStructuralComparable
{
    /**
     * Determines whether the current collection object precedes, occurs in the same position as, or follows another object in the sort order.
     * @param other The object to compare with the current instance.
     * @param comparer An object that compares members of the current collection object with the corresponding members of {@code other}.
     * @return A signed integer that indicates the relationship of the current collection object to other in the sort order:
     * - If less than 0, the current instance precedes {@code other}.
     * - If 0, the current instance and {@code other} are equal.
     * - If greater than 0, the current instance follows {@code other}.
     * @throws ArgumentException This instance and {@code other} are not the same type.
     */
    int CompareTo(@MaybeNull Object other, IComparer comparer) throws ArgumentException;
}
