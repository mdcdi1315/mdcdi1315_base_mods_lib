package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.ByRefParameterType;
import com.github.mdcdi1315.DotNetLayer.DotNetByRefParameter;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

/**
 * Defines a generalized method that a value type or class implements to create a type-specific method for determining equality of instances.
 * @param <T> The type of objects to compare.
 */
public interface IEquatable<@DotNetByRefParameter(ByRefParameterType.IN) T>
{
    /**
     * Indicates whether the current object is equal to another object of the same type.
     * @param other An object to compare with this object.
     * @return {@code true} if the current object is equal to the {@code other} parameter; otherwise, {@code false}.
     */
    boolean Equals(@MaybeNull T other);
}
