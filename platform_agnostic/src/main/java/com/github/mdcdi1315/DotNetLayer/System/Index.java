package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.ClassIsDotNetStruct;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.IsReadOnly;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.MethodImpl;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNullWhen;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DoesNotReturn;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.MethodImplOptions;

/**
 * Represents a type that can be used to index a collection either from the beginning or the end.
 */
@IsReadOnly
@ClassIsDotNetStruct
public final class Index
    extends ValueType
{
    private final int _value;

    /**
     * Initializes a new Index with a specified index position and a value that indicates if the index is from the beginning or the end of a collection.
     * @param value The index value. It has to be greater then or equal to zero.
     * @param fromEnd {@code true} to index from the end of the collection, or {@code false} to index from the beginning of the collection.
     */
    @MethodImpl(GetValue = MethodImplOptions.AggressiveInlining)
    public Index(int value, boolean fromEnd)
    {
        if (value < 0)
        {
            ThrowValueArgumentOutOfRange_NeedNonNegNumException();
        }

        if (fromEnd)
            _value = ~value;
        else
            _value = value;
    }

    public Index(int value) { _value = value; }

    public Index() { _value = 0; } // Implicit empty constructor definition

    /**
     * Gets an {@link Index} that points to the first element of a collection.
     * @return An instance that points to the first element of a collection.
     */
    public static Index GetStart() { return new Index(0); }

    /**
     * Gets an {@link Index} that points beyond the last element.
     * @return An index that points beyond the last element.
     */
    public static Index GetEnd() { return new Index(~0); }

    /**
     * Creates an {@link Index} from the specified index at the start of a collection.
     * @param value The index position from the start of a collection.
     * @return The index value.
     */
    @MethodImpl(GetValue = MethodImplOptions.AggressiveInlining)
    public static Index FromStart(int value)
    {
        if (value < 0)
        {
            ThrowValueArgumentOutOfRange_NeedNonNegNumException();
        }

        return new Index(value);
    }

    /**
     * Creates an {@link Index} from the end of a collection at a specified index position.
     * @param value The index value from the end of a collection.
     * @return The index value.
     */
    @MethodImpl(GetValue = MethodImplOptions.AggressiveInlining)
    public static Index FromEnd(int value)
    {
        if (value < 0)
        {
            ThrowValueArgumentOutOfRange_NeedNonNegNumException();
        }

        return new Index(~value);
    }

    /**
     * Gets the index value.
     * @return The index value.
     */
    public int GetValue()
    {
        if (_value < 0)
            return ~_value;
        else
            return _value;
    }

    /**
     * Gets a value that indicates whether the index is from the start or the end.
     * @return {@code true} if the Index is from the end; otherwise, {@code false}.
     */
    public boolean IsFromEnd() { return _value < 0; }

    /**
     * Calculates the offset from the start of the collection using the specified collection length.
     * @param length The length of the collection that the Index will be used with. Must be a positive value.
     * @return The offset.
     */
    @MethodImpl(GetValue = MethodImplOptions.AggressiveInlining)
    public int GetOffset(int length)
    {
        int offset = _value;
        if (IsFromEnd())
        {
            // offset = length - (~value)
            // offset = length + (~(~value) + 1)
            // offset = length + value + 1

            offset += length + 1;
        }
        return offset;
    }

    /**
     * Indicates whether the current {@link Index} object is equal to a specified object.
     * @param value An object to compare with this instance.
     * @return {@code true} if {@code value} is of type {@link Index} and is equal to the current instance; otherwise, {@code false}.
     */
    public boolean Equals(@NotNullWhen(ReturnValue = true) @AllowNull Object value) { return value instanceof Index i && _value == i._value; }

    /**
     * Returns a value that indicates whether the current object is equal to another {@link Index} object.
     * @param other The object to compare with this instance.
     * @return {@code true} if the current {@link Index} object is equal to {@code other}; otherwise, {@code false}.
     */
    public boolean Equals(Index other) { ValueType.ValidateNonNullStructure(other); return _value == other._value; }

    /**
     * Returns the hash code for this instance.
     * @return The hash code.
     */
    public int GetHashCode() { return _value; }

    /**
     * Returns the string representation of the current {@link Index} instance.
     * @return The string representation of the {@link Index}.
     */
    public String ToString()
    {
        if (IsFromEnd())
            return ToStringFromEnd();
        else
            return Integer.toString(_value);
    }

    @DoesNotReturn
    private static void ThrowValueArgumentOutOfRange_NeedNonNegNumException()
    {
        throw new ArgumentOutOfRangeException("value", "value must be non-negative");
    }

    private String ToStringFromEnd() { return '^' + Integer.toString(_value); }
}
