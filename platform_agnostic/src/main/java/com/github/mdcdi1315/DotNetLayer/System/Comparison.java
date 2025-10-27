package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.ByRefParameterType;
import com.github.mdcdi1315.DotNetLayer.DotNetByRefParameter;

/**
 * Represents the method that compares two objects of the same type.
 * @param <T> The type of the objects to compare.
 */
public interface Comparison<@DotNetByRefParameter(ByRefParameterType.IN) T>
{
    /**
     * @param x The first object to compare.
     * @param y The second object to compare.
     * @return A signed integer that indicates the relative values of {@code x} and {@code y}.
     */
    int comparison(T x, T y);
}
