package com.github.mdcdi1315.DotNetLayer.System.Collections;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

/**
 * Exposes a method that compares two objects.
 */
public interface IComparer
{
    /**
     * Compares two objects and returns a value indicating whether one is less than, equal to, or greater than the other.
     * @param x The first object to compare.
     * @param y The second object to compare.
     * @return A signed integer that indicates the relative values of x and y:
     * - If less than 0, {@code x} is less than {@code y}.
     * - If 0, {@code x} equals {@code y}.
     * - If greater than 0, {@code x} is greater than {@code y}.
     */
    int Compare(@MaybeNull Object x, @MaybeNull Object y);
}
