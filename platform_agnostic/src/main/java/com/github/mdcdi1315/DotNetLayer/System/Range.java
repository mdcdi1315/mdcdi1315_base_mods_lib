package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.ClassIsDotNetStruct;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.IsReadOnly;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.MethodImpl;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNullWhen;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.MethodImplOptions;

/**
 * Represents a range that has start and end indexes.
 */
@IsReadOnly
@ClassIsDotNetStruct
public final class Range
    extends ValueType
{
    private final Index start, end;

    /**
     * Gets the inclusive start index of the {@link Range}.
     * @return The inclusive start index of the range.
     */
    public Index GetStart() { return start; }

    /**
     * Gets an {@link Index} that represents the exclusive end index of the range.
     * @return The end index of the range.
     */
    public Index GetEnd() { return end; }

    public Range() // Implicit constructor
    {
        end = new Index();
        start = new Index();
    }

    /**
     * Instantiates a new {@link Range} instance with the specified starting and ending indexes.
     * @param start The inclusive start index of the range.
     * @param end The exclusive end index of the range.
     */
    public Range(Index start, Index end)
    {
        ValueType.ValidateNonNullStructure(start);
        ValueType.ValidateNonNullStructure(end);
        this.end = end;
        this.start = start;
    }

    /**
     * Returns a value that indicates whether the current instance is equal to a specified object.
     * @param value An object to compare with this {@link Range} object.
     * @return {@code true} if {@code value} is of type {@link Range} and is equal to the current instance; otherwise, {@code false}.
     */
    public boolean Equals(@NotNullWhen(ReturnValue = true) @AllowNull Object value)
    {
        return value instanceof Range r &&
                r.start.Equals(start) &&
                r.end.Equals(end);
    }

    /**
     * Returns a value that indicates whether the current instance is equal to another {@link Range} object.
     * @param other A {@link Range} object to compare with this {@link Range} object.
     * @return {@code true} if the current instance is equal to {@code other}; otherwise, {@code false}.
     */
    public boolean Equals(Range other) { return other.start.Equals(start) && other.end.Equals(end); }

    /**
     * Returns the hash code for this instance.
     * @return The hash code.
     */
    public int GetHashCode()
    {
        int h1 = start.GetHashCode();
        return (((h1 << 5) | (h1 >> 27)) + h1) ^ end.GetHashCode();
    }

    /**
     * Returns the string representation of the current {@link Range} object.
     * @return The string representation of the range.
     */
    public String ToString()
    {
        return start.ToString() + ".." + end.ToString();
    }

    /**
     * Returns a new {@link Range} instance starting from a specified start index to the end of the collection.
     * @param start The position of the first element from which the {@link Range} will be created.
     * @return A range from {@code start} to the end of the collection.
     */
    public static Range StartAt(Index start) { return new Range(start, Index.GetEnd()); }

    /**
     * Creates a {@link Range} object starting from the first element in the collection to a specified end index.
     * @param end The position of the last element up to which the {@link Range} object will be created.
     * @return A range that starts from the first element to {@code end}.
     */
    public static Range EndAt(Index end) { return new Range(Index.GetStart(), end); }

    /**
     * Gets a {@link Range} object that starts from the first element to the end.
     * @return A range from the start to the end.
     */
    public static Range GetAll() { return new Range(Index.GetStart(), Index.GetEnd()); }

    /**
     * Calculates the start offset and length of the range object using a collection length.
     * @param length A positive integer that represents the length of the collection that the range will be used with.
     * @return The start offset and length of the range.
     */
    @MethodImpl(GetValue = MethodImplOptions.AggressiveInlining)
    public Tuple2<Integer, Integer> GetOffsetAndLength(int length)
    {
        int end = this.end.GetOffset(length);
        int start = this.start.GetOffset(length);

        if (start < 0 || end > length || start > end)
        {
            ThrowArgumentOutOfRangeException();
        }

        return new Tuple2<>(start, end - start);
    }

    private static void ThrowArgumentOutOfRangeException()
    {
        throw new ArgumentOutOfRangeException("length");
    }
}
