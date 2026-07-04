package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.ClassIsDotNetStruct;
import com.github.mdcdi1315.DotNetLayer.ExplicitInterfaceDeclaration;
import com.github.mdcdi1315.DotNetLayer.System.Collections.IComparer;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.Comparer;
import com.github.mdcdi1315.DotNetLayer.System.Collections.IEqualityComparer;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Collections.IStructuralEquatable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.IStructuralComparable;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNullWhen;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.EqualityComparer;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.TypeForwardedFrom;

/**
 * Represents a value tuple with 7 components.
 * @param <T1> The type of the value tuple's first element.
 * @param <T2> The type of the value tuple's second element.
 * @param <T3> The type of the value tuple's third element.
 * @param <T4> The type of the value tuple's fourth element.
 * @param <T5> The type of the value tuple's fifth element.
 * @param <T6> The type of the value tuple's sixth element.
 * @param <T7> The type of the value tuple's seventh element.
 */
@ClassIsDotNetStruct
@TypeForwardedFrom(AssemblyFullName = "mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089")
public final class ValueTuple7<T1, T2, T3, T4, T5, T6, T7>
        extends ValueType
        implements IStructuralEquatable,
        IStructuralComparable,
        IComparable<ValueTuple7<T1, T2, T3, T4, T5, T6, T7>>,
        IValueTupleInternal
{
    /**
     * The current {@link ValueTuple7} instance's first component.
     */
    public T1 Item1;
    /**
     * The current {@link ValueTuple7} instance's second component.
     */
    public T2 Item2;
    /**
     * The current {@link ValueTuple7} instance's third component.
     */
    public T3 Item3;
    /**
     * The current {@link ValueTuple7} instance's fourth component.
     */
    public T4 Item4;
    /**
     * The current {@link ValueTuple7} instance's fifth component.
     */
    public T5 Item5;
    /**
     * The current {@link ValueTuple7} instance's sixth component.
     */
    public T6 Item6;
    /**
     * The current {@link ValueTuple7} instance's seventh component.
     */
    public T7 Item7;

    public ValueTuple7()
    {
        Item1 = null;
        Item2 = null;
        Item3 = null;
        Item4 = null;
        Item5 = null;
        Item6 = null;
        Item7 = null;
    }

    /**
     * Initializes a new instance of the {@link ValueTuple7} value type.
     * @param item1 The value of the tuple's first component.
     * @param item2 The value of the tuple's second component.
     * @param item3 The value of the tuple's third component.
     * @param item4 The value of the tuple's fourth component.
     * @param item5 The value of the tuple's fifth component.
     * @param item6 The value of the tuple's sixth component.
     * @param item7 The value of the tuple's seventh component.
     */
    public ValueTuple7(T1 item1, T2 item2, T3 item3, T4 item4, T5 item5, T6 item6, T7 item7)
    {
        Item1 = item1;
        Item2 = item2;
        Item3 = item3;
        Item4 = item4;
        Item5 = item5;
        Item6 = item6;
        Item7 = item7;
    }

    @Override
    @SuppressWarnings("rawtypes")
    @ExplicitInterfaceDeclaration(value = IStructuralComparable.class, ShouldBePrivate = true)
    public int CompareTo(Object other, IComparer comparer)
            throws ArgumentException
    {
        if (other != null)
        {
            if (other instanceof ValueTuple7 objTuple)
            {
                int c = comparer.Compare(Item1, objTuple.Item1);
                if (c != 0) return c;

                c = comparer.Compare(Item2, objTuple.Item2);
                if (c != 0) return c;

                c = comparer.Compare(Item3, objTuple.Item3);
                if (c != 0) return c;

                c = comparer.Compare(Item4, objTuple.Item4);
                if (c != 0) return c;

                c = comparer.Compare(Item5, objTuple.Item5);
                if (c != 0) return c;

                c = comparer.Compare(Item6, objTuple.Item6);
                if (c != 0) return c;

                return comparer.Compare(Item7, objTuple.Item7);
            }

            // ThrowHelper.ThrowArgumentException_TupleIncorrectType(this);
            throw new ArgumentException("Incorrect tuple type was passed to CompareTo.");
        }

        return 1;
    }

    @Override
    @SuppressWarnings("rawtypes")
    @ExplicitInterfaceDeclaration(value = IStructuralEquatable.class, ShouldBePrivate = true)
    public boolean Equals(@AllowNull Object other, IEqualityComparer comparer)
    {
        return other instanceof ValueTuple7 vt &&
                comparer.Equals(Item1, vt.Item1) &&
                comparer.Equals(Item2, vt.Item2) &&
                comparer.Equals(Item3, vt.Item3) &&
                comparer.Equals(Item4, vt.Item4) &&
                comparer.Equals(Item5, vt.Item5) &&
                comparer.Equals(Item6, vt.Item6) &&
                comparer.Equals(Item7, vt.Item7);
    }

    @Override
    @ExplicitInterfaceDeclaration(value = IValueTupleInternal.class, ShouldBePrivate = true)
    @ExplicitInterfaceDeclaration(value = IStructuralEquatable.class, ShouldBePrivate = true)
    public int GetHashCode(IEqualityComparer comparer)
    {
        return HashCode.Combine(
                comparer.GetHashCode(Item1),
                comparer.GetHashCode(Item2),
                comparer.GetHashCode(Item3),
                comparer.GetHashCode(Item4),
                comparer.GetHashCode(Item5),
                comparer.GetHashCode(Item6),
                comparer.GetHashCode(Item7)
        );
    }

    /**
     * Returns a value that indicates whether the current {@link ValueTuple7} instance is equal to a specified object.
     * @param obj The object to compare with the current instance.
     * @return {@code true} if the current instance is equal to the specified object; otherwise, {@code false}.
     */
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public boolean Equals(@AllowNull @NotNullWhen(ReturnValue = true) Object obj) { return obj instanceof ValueTuple7 vt && Equals((ValueTuple7<T1, T2, T3, T4, T5, T6, T7>) vt); }

    /**
     * Returns a value that indicates whether the current {@link ValueTuple7} instance is equal to a specified {@link ValueTuple7} instance.
     * @param other The value tuple to compare with this instance.
     * @return {@code true} if the current instance is equal to the specified tuple; otherwise, {@code false}.
     */
    public boolean Equals(ValueTuple7<T1, T2, T3, T4, T5, T6, T7> other)
    {
        ValidateNonNullStructure(other);
        return EqualityComparer.GetDefault().Equals(Item1, other.Item1)
                && EqualityComparer.GetDefault().Equals(Item2, other.Item2)
                && EqualityComparer.GetDefault().Equals(Item3, other.Item3)
                && EqualityComparer.GetDefault().Equals(Item4, other.Item4)
                && EqualityComparer.GetDefault().Equals(Item5, other.Item5)
                && EqualityComparer.GetDefault().Equals(Item6, other.Item6)
                && EqualityComparer.GetDefault().Equals(Item7, other.Item7);
    }

    @Override
    public int CompareTo(ValueTuple7<T1, T2, T3, T4, T5, T6, T7> other)
    {
        ValidateNonNullStructure(other);
        int c = Comparer.GetDefault().Compare(Item1, other.Item1);
        if (c != 0) return c;

        c = Comparer.GetDefault().Compare(Item2, other.Item2);
        if (c != 0) return c;

        c = Comparer.GetDefault().Compare(Item3, other.Item3);
        if (c != 0) return c;

        c = Comparer.GetDefault().Compare(Item4, other.Item4);
        if (c != 0) return c;

        c = Comparer.GetDefault().Compare(Item5, other.Item5);
        if (c != 0) return c;

        c = Comparer.GetDefault().Compare(Item6, other.Item6);
        if (c != 0) return c;

        return Comparer.GetDefault().Compare(Item7, other.Item7);
    }

    @Override
    public int GetLength() { return 7; }

    @Override
    public Object getItem(Integer integer)
    {
        return switch (integer)
        {
            case 0 -> Item1;
            case 1 -> Item2;
            case 2 -> Item3;
            case 3 -> Item4;
            case 4 -> Item5;
            case 5 -> Item6;
            case 6 -> Item7;
            default -> throw new IndexOutOfBoundsException();
        };
    }

    @NotNull
    @Override
    public String ToStringEnd()
    {
        return String.format(
                "%s, %s, %s, %s, %s, %s, %s)",
                Item1 == null ? StringUtils.Empty : Item1.toString(),
                Item2 == null ? StringUtils.Empty : Item2.toString(),
                Item3 == null ? StringUtils.Empty : Item3.toString(),
                Item4 == null ? StringUtils.Empty : Item4.toString(),
                Item5 == null ? StringUtils.Empty : Item5.toString(),
                Item6 == null ? StringUtils.Empty : Item6.toString(),
                Item7 == null ? StringUtils.Empty : Item7.toString()
        );
    }

    /**
     * Returns a string that represents the value of this {@link ValueTuple7} instance.
     * @return The string representation of this {@link ValueTuple7} instance.
     */
    @NotNull
    @Override
    public String ToString()
    {
        return String.format(
                "(%s, %s, %s, %s, %s, %s, %s)",
                Item1 == null ? StringUtils.Empty : Item1.toString(),
                Item2 == null ? StringUtils.Empty : Item2.toString(),
                Item3 == null ? StringUtils.Empty : Item3.toString(),
                Item4 == null ? StringUtils.Empty : Item4.toString(),
                Item5 == null ? StringUtils.Empty : Item5.toString(),
                Item6 == null ? StringUtils.Empty : Item6.toString(),
                Item7 == null ? StringUtils.Empty : Item7.toString()
        );
    }

    /**
     * Calculates the hash code for the current {@link ValueTuple7} instance.
     * @return The hash code for the current {@link ValueTuple7} instance.
     */
    @Override
    public int GetHashCode()
    {
        return HashCode.Combine(
                Item1 == null ? 0 : Item1.hashCode(),
                Item2 == null ? 0 : Item2.hashCode(),
                Item3 == null ? 0 : Item3.hashCode(),
                Item4 == null ? 0 : Item4.hashCode(),
                Item5 == null ? 0 : Item5.hashCode(),
                Item6 == null ? 0 : Item6.hashCode(),
                Item7 == null ? 0 : Item7.hashCode()
        );
    }
}