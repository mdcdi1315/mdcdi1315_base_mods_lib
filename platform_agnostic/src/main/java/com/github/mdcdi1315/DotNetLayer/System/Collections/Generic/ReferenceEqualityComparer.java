package com.github.mdcdi1315.DotNetLayer.System.Collections.Generic;

/**
 * An {@link IEqualityComparer} that uses reference equality (ReferenceEquals(Object, Object)) instead of value equality ({@link Object#equals(Object)}) when comparing two object instances.
 */
public final class ReferenceEqualityComparer
    implements IEqualityComparer<Object>, com.github.mdcdi1315.DotNetLayer.System.Collections.IEqualityComparer
{
    private ReferenceEqualityComparer() { }

    /**
     * Gets the singleton {@link ReferenceEqualityComparer} instance.
     */
    public static final ReferenceEqualityComparer Instance = new ReferenceEqualityComparer();

    /**
     * Determines whether two object references refer to the same object instance.
     * @param x The first object to compare.
     * @param y The second object to compare.
     * @return {@code true} if both {@code x} and {@code y} refer to the same object instance or if both are {@code null}; otherwise, {@code false}.
     */
    @Override
    public boolean Equals(Object x, Object y) { return x == y; } // Same as invoking ReferenceEquals on .NET.

    /**
     * Returns a hash code for the specified object. The returned hash code is based on the object identity, not on the contents of the object.
     * @param obj The {@link Object} for which a hash code is to be returned.
     * @return A hash code for the identity of {@code obj}.
     */
    @Override
    public int GetHashCode(Object obj) { return System.identityHashCode(obj); } // Same as invoking RuntimeHelpers.GetHashCode on .NET.
}
