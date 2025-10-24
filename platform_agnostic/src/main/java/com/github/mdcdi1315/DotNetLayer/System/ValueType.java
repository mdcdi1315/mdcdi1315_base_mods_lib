package com.github.mdcdi1315.DotNetLayer.System;

/**
 * The base class for all .NET - translated structures.
 * <p>
 *     You can derive from this class in order to do primarily two things, one is
 *     to mimic the behavior of the .NET structure and the other is to indicate
 *     that the class you are porting is a .NET structure.
 * </p>
 */
public class ValueType
{
    public ValueType()
    {

    }

    @Override
    public final boolean equals(Object obj) {
        return super.equals(obj);
    }

    public boolean Equals(Object any) {
        return equals(any);
    }

    public final int hashCode() {
        return GetHashCode();
    }

    public int GetHashCode() {
        return super.hashCode();
    }

    public final String toString() {
        return ToString();
    }

    public String ToString() {
        return getClass().getName();
    }
}
