package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.ClassIsDotNetStruct;
import com.github.mdcdi1315.DotNetLayer.ExplicitInterfaceDeclaration;

import com.github.mdcdi1315.DotNetLayer.System.Collections.IComparer;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.Comparer;
import com.github.mdcdi1315.DotNetLayer.System.Collections.IEqualityComparer;
import com.github.mdcdi1315.DotNetLayer.System.Collections.IStructuralEquatable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.IStructuralComparable;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.ITuple;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.TypeForwardedFrom;

/**
 * Represents a value tuple with a single component.
 * @param <T1> The type of the value tuple's only element.
 */
@ClassIsDotNetStruct
@TypeForwardedFrom(AssemblyFullName = "mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089")
public final class ValueTuple1<T1>
    extends ValueType
    implements
        IValueTupleInternal,
        ITuple,
        IStructuralEquatable,
        IStructuralComparable,
        IComparable<ValueTuple1<T1>>
{
    public T1 Item1;

    /**
     * Initializes a new {@link ValueTuple1} instance.
     * @param item The value tuple's first element.
     */
    public ValueTuple1(T1 item) { Item1 = item; }

    public ValueTuple1() { super(); Item1 = null; }

    @Override
    @SuppressWarnings("rawtypes")
    @ExplicitInterfaceDeclaration(value = IStructuralComparable.class, ShouldBePrivate = true)
    public int CompareTo(@AllowNull Object other, IComparer comparer)
            throws ArgumentException
    {
        if (other != null)
        {
            if (other instanceof ValueTuple1 objTuple)
            {
                return comparer.Compare(Item1, objTuple.Item1);
            }

            // ThrowHelper.ThrowArgumentException_TupleIncorrectType(this);
            throw new ArgumentException("The current tuple type is incorrect.");
        } else {
            return 1;
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    @ExplicitInterfaceDeclaration(value = IStructuralEquatable.class, ShouldBePrivate = true)
    public boolean Equals(@AllowNull Object other, IEqualityComparer comparer)
    {
        return other instanceof ValueTuple1 vt && comparer.Equals(Item1, vt.Item1);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public boolean Equals(Object obj) { return obj instanceof ValueTuple1 tuple && Equals((ValueTuple1<T1>) tuple); }

    /**
     * Returns a value that indicates whether the current ValueTuple<T1> instance is equal to a specified ValueTuple<T1> instance.
     *
     * @param other The value tuple to compare with this instance.
     * @return true if the current instance is equal to the specified tuple; otherwise, false.
     * @apiNote The other argument is considered to be equal to the current instance under the following conditions: <br /> <br />
     *   - Its components are of the same types as those of the current instance. <br /> <br />
     *   - Its components are equal to those of the current instance. Equality is determined by the default equality comparer for each component.
     */
    public boolean Equals(ValueTuple1<T1> other)
    {
        ValueType.ValidateNonNullStructure(other);
        return Item1 != null && Item1.equals(other.Item1);
    }

    /**
     * Compares the current {@link ValueTuple1} instance to a specified {@link ValueTuple1} instance.
     * @param other The tuple to compare with this instance.
     * @return {@inheritDoc}
     */
    @Override
    public int CompareTo(ValueTuple1<T1> other)
    {
        ValueType.ValidateNonNullStructure(other);
        return Comparer.GetDefault().Compare(Item1, other.Item1);
    }

    /**
     * Calculates the hash code for the current {@link ValueTuple1} instance.
     * @return The hash code for the current {@link ValueTuple1} instance.
     */
    @Override
    public int GetHashCode() { return Item1 == null ? 0 : Item1.hashCode(); }

    @Override
    @ExplicitInterfaceDeclaration(value = IValueTupleInternal.class, ShouldBePrivate = true)
    @ExplicitInterfaceDeclaration(value = IStructuralEquatable.class, ShouldBePrivate = true)
    public int GetHashCode(IEqualityComparer comparer) { return comparer.GetHashCode(Item1); }

    @Override
    @ExplicitInterfaceDeclaration(value = IValueTupleInternal.class, ShouldBePrivate = true)
    public String ToStringEnd() { return Item1 == null ? StringUtils.Empty : Item1.toString() + ")"; }

    /**
     * Returns a string that represents the value of this {@link ValueTuple1} instance.
     * @return The string representation of this {@link ValueTuple1} instance.
     * @apiNote The string returned by this method takes the form {@code (Item1)},
     * where {@code Item1} represents the value of {@link #Item1}. If the field is {@code null},
     * it is represented as {@link StringUtils#Empty}.
     */
    @Override
    public String ToString() { return "(" + (Item1 == null ? StringUtils.Empty : Item1.toString()) + ")"; }

    /**
     * The number of positions in this data structure.
     */
    @Override
    public int GetLength() { return 1; }

    /**
     * Get the element at position {@code index}.
     */
    @Override
    public Object getItem(Integer integer)
    {
        if (integer == 0) {
            return Item1;
        } else {
            throw new IndexOutOfRangeException();
        }
    }
}
