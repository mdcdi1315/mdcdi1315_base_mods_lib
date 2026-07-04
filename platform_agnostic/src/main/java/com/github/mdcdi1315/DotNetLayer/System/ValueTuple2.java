package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.ClassIsDotNetStruct;
import com.github.mdcdi1315.DotNetLayer.ExplicitInterfaceDeclaration;

import com.github.mdcdi1315.DotNetLayer.System.Collections.IComparer;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.Comparer;
import com.github.mdcdi1315.DotNetLayer.System.Collections.IEqualityComparer;
import com.github.mdcdi1315.DotNetLayer.System.Collections.IStructuralEquatable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.IStructuralComparable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.EqualityComparer;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNullWhen;

import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.ITuple;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.TypeForwardedFrom;

/**
 * Represents a value tuple with 2 components.
 * @param <T1> The type of the value tuple's first element.
 * @param <T2> The type of the value tuple's second element.
 */
@ClassIsDotNetStruct
@TypeForwardedFrom(AssemblyFullName = "mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089")
public final class ValueTuple2<T1, T2>
    extends ValueType
    implements
        ITuple,
        IValueTupleInternal,
        IStructuralEquatable,
        IStructuralComparable,
        IComparable<ValueTuple2<T1, T2>>
{
    /**
     * Gets the value of the current {@link ValueTuple2} instance's first element.
     */
    public T1 Item1;

    /**
     * Gets the value of the current {@link ValueTuple2} instance's second element.
     */
    public T2 Item2;

    // Empty constructor declaration.
    public ValueTuple2()
    {
        Item1 = null;
        Item2 = null;
    }

    /**
     * Initializes a new {@link ValueTuple2} instance.
     * @param item1 The value of the tuple's first component.
     * @param item2 The value of the tuple's second component.
     */
    public ValueTuple2(T1 item1, T2 item2)
    {
        Item1 = item1;
        Item2 = item2;
    }

    /**
     * Returns a value that indicates whether the current {@link ValueTuple2} instance is equal to a specified object.
     * @param obj The object to compare with this instance.
     * @return {@code true} if the current instance is equal to the specified object; otherwise, {@code false}.
     * @apiNote The {@code obj} parameter is considered to be equal to the current instance under the following conditions:
     * <ul>
     *     <li>It is a {@link ValueTuple2} value type.</li>
     *     <li>Its components are of the same types as those of the current instance.</li>
     *     <li>Its components are equal to those of the current instance. Equality is determined by the default object equality comparer for each component.</li>
     * </ul>
     */
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public boolean Equals(@NotNullWhen(ReturnValue = true) @AllowNull Object obj)
    {
        return obj instanceof ValueTuple2 tuple && Equals((ValueTuple2<T1, T2>)tuple);
    }

    /**
     * Returns a value that indicates whether the current {@link ValueTuple2} instance is equal to a specified {@link ValueTuple2}.
     * @param other The tuple to compare with this instance.
     * @return {@code true} if the current instance is equal to the specified tuple; otherwise, {@code false}.
     * @apiNote The {@code other} parameter is considered to be equal to the current instance if each of its fields
     * are equal to that of the current instance, using the default comparer for that field's type.
     */
    public boolean Equals(ValueTuple2<T1, T2> other)
    {
        ValueType.ValidateNonNullStructure(other);
        return EqualityComparer.GetDefault().Equals(Item1, other.Item1)
                && EqualityComparer.GetDefault().Equals(Item2, other.Item2);
    }

    /**
     * Returns a value that indicates whether the current {@link ValueTuple2} instance is equal to a specified object based on a specified comparison method.
     * @param other The object to compare with the current instance.
     * @param comparer An object that determines whether the current instance and {@code other} are equal.
     * @return {@code true} if the current instance is equal to the specified object; otherwise, {@code false}.
     * @apiNote This member is an explicit interface member implementation. It can be used only when the
     * {@link ValueTuple2} instance is cast to an {@link IStructuralEquatable} interface. <br />
     *
     * The {@link IEqualityComparer#Equals} implementation is called only if <code>other</code> is not {@code null},
     * and if it can be successfully cast (in C#) or converted (in Visual Basic) to a {@link ValueTuple2}
     * whose components are of the same types as those of the current instance. The {@link IStructuralEquatable#Equals} method
     * first passes the {@code #Item2} values of the {@link ValueTuple2} objects to be compared to the
     * {@link IEqualityComparer#Equals} implementation. If this method call returns {@code true}, the method is
     * called again and passed the {@link #Item2} values of the two {@link ValueTuple2} instances.
     */
    @Override
    @SuppressWarnings("rawtypes")
    @ExplicitInterfaceDeclaration(value = IStructuralEquatable.class, ShouldBePrivate = true)
    public boolean Equals(@AllowNull Object other, IEqualityComparer comparer)
    {
        return other instanceof ValueTuple2 vt &&
            comparer.Equals(Item1, vt.Item1) &&
            comparer.Equals(Item2, vt.Item2);
    }

    /**
     * Compares this instance to a specified instance and returns an indication of their relative values.
     * @param other An object to compare with this instance.
     * @return A signed number indicating the relative values of this instance and {@code other}.
     * Returns less than zero if this instance is less than {@code other}, zero if this
     * instance is equal to {@code other}, and greater than zero if this instance is greater
     * than {@code other}.
     */
    @Override
    public int CompareTo(ValueTuple2<T1, T2> other)
    {
        ValueType.ValidateNonNullStructure(other);
        int c = Comparer.GetDefault().Compare(Item1, other.Item1);
        return c == 0 ? Comparer.GetDefault().Compare(Item2, other.Item2) : c;
    }

    @Override
    @SuppressWarnings("rawtypes")
    @ExplicitInterfaceDeclaration(IStructuralComparable.class)
    public int CompareTo(@AllowNull Object other, IComparer comparer)
    {
        if (other != null)
        {
            if (other instanceof ValueTuple2 objTuple)
            {
                int c = comparer.Compare(Item1, objTuple.Item1);
                if (c != 0) return c;

                return comparer.Compare(Item2, objTuple.Item2);
            }

            // ThrowHelper.ThrowArgumentException_TupleIncorrectType(this);
            throw new ArgumentException("Incorrect tuple type was passed to CompareTo.");
        }

        return 1;
    }

    /**
     * Returns the hash code for the current {@link ValueTuple2} instance.
     * @return A 32-bit signed integer hash code.
     */
    @Override
    public int GetHashCode()
    {
        return HashCode.Combine(
                Item1 == null ? 0 : Item1.hashCode(),
                Item2 == null ? 0 : Item2.hashCode()
        );
    }

    private int GetHashCodeCore(IEqualityComparer comparer) { return HashCode.Combine(comparer.GetHashCode(Item1), comparer.GetHashCode(Item2)); }

    /**
     * Calculates the hash code for the current {@link ValueTuple2} instance by using a specified computation method.
     * @param comparer An object that computes the hash code of the current object.
     * @return A 32-bit signed integer hash code.
     */
    @Override
    @ExplicitInterfaceDeclaration(IStructuralEquatable.class)
    @ExplicitInterfaceDeclaration(value = IValueTupleInternal.class, ShouldBePrivate = true)
    public int GetHashCode(IEqualityComparer comparer) { return GetHashCodeCore(comparer); }

    /**
     * Returns a string that represents the value of this {@link ValueTuple2} instance.
     * @return The string representation of this {@link ValueTuple2} instance.
     * @apiNote The string returned by this method takes the form <code>(Item1, Item2)</code>,
     * where {@link #Item1} and {@link #Item2} represent the values of the{@link #Item1}
     * and {@link #Item2} fields. If either field value is {@code null},
     * it is represented as {@link StringUtils#Empty}.
     */
    @Override
    public String ToString()
    {
        return String.format("(%s, %s)",
                (Item1 == null ? StringUtils.Empty : Item1.toString()),
                (Item2 == null ? StringUtils.Empty : Item2.toString())
        );
    }

    @Override
    @ExplicitInterfaceDeclaration(value = IValueTupleInternal.class, ShouldBePrivate = true)
    public String ToStringEnd()
    {
        return String.format("%s, %s)",
                (Item1 == null ? StringUtils.Empty : Item1.toString()),
                (Item2 == null ? StringUtils.Empty : Item2.toString())
        );
    }

    /**
     * The number of positions in this data structure.
     */
    @Override
    public int GetLength() { return 2; }

    @Override
    public Object getItem(Integer index)
    {
        return switch (index) {
            case 0 -> Item1;
            case 1 -> Item2;
            default -> throw new IndexOutOfRangeException();
        };
    }
}