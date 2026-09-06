package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.StackTraceHidden;

/**
 * The base class for all .NET - translated structures. <br />
 * You can derive from this class in order to do primarily two things, one is
 * to mimic the behavior of the .NET structure and the other is to indicate
 * that the class you are porting is a .NET structure. <br /> <br />
 *
 * Although that it can be initialized on its own fashion, it's not very practical to do it. <br />
 * Note also that the Java methods cannot be overridden when you create a new .NET - translated structure; you must align with the .NET conventions.
 */
public class ValueType
{
    /**
     * Initializes a new instance of the {@link ValueType} class.
     */
    public ValueType() { }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() { return GetHashCode(); }

    /**
     * Returns the hash code for this instance.
     * @return A 32-bit signed integer that is the hash code for this instance.
     */
    public int GetHashCode() { return super.hashCode(); }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() { return ToString(); }

    /**
     * Returns the fully qualified type name of this instance.
     * @return The fully qualified type name.
     */
    @MaybeNull
    public String ToString() { return getClass().getName(); }

    /**
     * Indicates whether this instance and a specified object are equal.
     * @param any The object to compare with the current instance.
     * @return {@code true} if {@code obj} and this instance are the same type and represent the same value; otherwise, {@code false}.
     */
    public boolean Equals(Object any) { return super.equals(any); }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("EqualsDoesntCheckParameterClass")
    public final boolean equals(Object obj) { return Equals(obj); }

    /**
     * Validates that the given {@link ValueType} is not {@code null}. <br />
     * Use this method on every possible call site to validate this fact. <br />
     * Note - .NET structures cannot be {@code null} at any cost. <br />
     * If {@code null}, a special runtime corruption exception is thrown.
     * @param vt The {@link ValueType} instance to validate.
     * @apiNote This API does not exist in .NET; it is .NET Layer-specific.
     */
    @StackTraceHidden
    public static void ValidateNonNullStructure(ValueType vt)
    {
        if (vt == null)
        {
            throw new ExecutionEngineException("Structure parameter/field cannot be null.");
        }
    }

    /**
     * Validates that the all given {@link ValueType} instances in the array are not {@code null}. <br />
     * Use this method on every possible call site to validate this fact. <br />
     * Note - .NET structures cannot be {@code null} at any cost. <br />
     * If {@code null}, a special runtime corruption exception is thrown.
     * @param array The array {@link ValueType} instances to validate.
     * @apiNote This API does not exist in .NET; it is .NET Layer-specific.
     */
    @StackTraceHidden
    public static void ValidateElementsOfArray(ValueType[] array)
    {
        for (ValueType vt : array) { ValidateNonNullStructure(vt); }
    }
}
