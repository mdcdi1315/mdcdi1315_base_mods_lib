package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.ClassIsDotNetStruct;
import com.github.mdcdi1315.DotNetLayer.ExplicitInterfaceDeclaration;
import com.github.mdcdi1315.DotNetLayer.System.Collections.IComparer;
import com.github.mdcdi1315.DotNetLayer.System.Collections.IEqualityComparer;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Collections.IStructuralEquatable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.IStructuralComparable;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

/**
 * Provides static methods for creating value tuples.
 */
@ClassIsDotNetStruct
public final class ValueTuple
    extends ValueType
    implements
        IValueTupleInternal,
        IStructuralEquatable,
        IStructuralComparable,
        IComparable<ValueTuple>
{
    @Override
    @ExplicitInterfaceDeclaration(value = IStructuralComparable.class, ShouldBePrivate = true)
    public int CompareTo(@AllowNull Object other, IComparer comparer)
            throws ArgumentException
    {
        if (other == null) {
            return 1;
        } else if (!(other instanceof ValueTuple)) {
            // ThrowHelper.ThrowArgumentException_TupleIncorrectType(this);
            throw new ArgumentException("The provided tuple type is incorrect.");
        } else {
            return 0;
        }
    }

    @Override
    public int GetLength() { return 0; }

    @Override
    public int GetHashCode() { return 0; }

    @NotNull
    @Override
    public String ToString() { return "()"; }

    @NotNull
    @Override
    @ExplicitInterfaceDeclaration(value = IValueTupleInternal.class, ShouldBePrivate = true)
    public String ToStringEnd() { return ")"; }

    @Override
    public int CompareTo(ValueTuple other) { return 0; }

    @Override
    @ExplicitInterfaceDeclaration(value = IValueTupleInternal.class, ShouldBePrivate = true)
    @ExplicitInterfaceDeclaration(value = IStructuralEquatable.class, ShouldBePrivate = true)
    public int GetHashCode(IEqualityComparer comparer) { return 0; }

    @Override
    public Object getItem(Integer integer) { throw new IndexOutOfRangeException(); }

    @Override
    @ExplicitInterfaceDeclaration(value = IStructuralEquatable.class, ShouldBePrivate = true)
    public boolean Equals(@AllowNull Object other, IEqualityComparer comparer) { return other instanceof ValueTuple; }

    /**
     * Creates a new value tuple with zero components.
     * @return A new value tuple with no components.
     */
    @NotNull
    public static ValueTuple Create() { return new ValueTuple(); }

    /**
     * Creates a new value tuple with 1 component (a singleton).
     * @param item1 The value of the value tuple's only component.
     * @return A value tuple with 1 component.
     * @param <T1> The type of the value tuple's only component.
     */
    @NotNull
    public static <T1> ValueTuple1<T1> Create(@AllowNull T1 item1) { return new ValueTuple1<>(item1); }

    /**
     * Creates a new value tuple with 2 components (a pair).
     * @param item1 The type of the value tuple's first component.
     * @param item2 The type of the value tuple's second component.
     * @return A value tuple with 2 components.
     * @param <T1> The value of the value tuple's first component.
     * @param <T2> The value of the value tuple's second component.
     */
    @NotNull
    public static <T1, T2> ValueTuple2<T1, T2> Create(@AllowNull T1 item1, @AllowNull T2 item2) { return new ValueTuple2<>(item1, item2); }

    /**
     * Creates a new value tuple with 3 components (a triple).
     * @param item1 The value of the value tuple's first component.
     * @param item2 The value of the value tuple's second component.
     * @param item3 The value of the value tuple's third component.
     * @return A value tuple with 3 components.
     * @param <T1> The type of the value tuple's first component.
     * @param <T2> The type of the value tuple's second component.
     * @param <T3> The type of the value tuple's third component.
     */
    @NotNull
    public static <T1, T2, T3> ValueTuple3<T1, T2, T3> Create(
            @AllowNull T1 item1, @AllowNull T2 item2, @AllowNull T3 item3
    ) { return new ValueTuple3<>(item1, item2, item3); }

    /**
     * Creates a new value tuple with 4 components (a quadruple).
     * @param item1 The value of the value tuple's first component.
     * @param item2 The value of the value tuple's second component.
     * @param item3 The value of the value tuple's third component.
     * @param item4 The value of the value tuple's fourth component.
     * @return A value tuple with 4 components.
     * @param <T1> The type of the value tuple's first component.
     * @param <T2> The type of the value tuple's second component.
     * @param <T3> The type of the value tuple's third component.
     * @param <T4> The type of the value tuple's fourth component.
     */
    @NotNull
    public static <T1, T2, T3, T4> ValueTuple4<T1, T2, T3, T4> Create(
            @AllowNull T1 item1, @AllowNull T2 item2,
            @AllowNull T3 item3, @AllowNull T4 item4
    ) { return new ValueTuple4<>(item1, item2, item3, item4); }

    /**
     * Creates a new value tuple with 5 components (a quintuple).
     * @param item1 The value of the value tuple's first component.
     * @param item2 The value of the value tuple's second component.
     * @param item3 The value of the value tuple's third component.
     * @param item4 The value of the value tuple's fourth component.
     * @param item5 The value of the value tuple's fifth component.
     * @return A value tuple with 5 components.
     * @param <T1> The type of the value tuple's first component.
     * @param <T2> The type of the value tuple's second component.
     * @param <T3> The type of the value tuple's third component.
     * @param <T4> The type of the value tuple's fourth component.
     * @param <T5> The type of the value tuple's fifth component.
     */
    @NotNull
    public static <T1, T2, T3, T4, T5> ValueTuple5<T1, T2, T3, T4, T5> Create(
            @AllowNull T1 item1, @AllowNull T2 item2,
            @AllowNull T3 item3, @AllowNull T4 item4,
            @AllowNull T5 item5
    ) { return new ValueTuple5<>(item1, item2, item3, item4, item5); }

    /**
     * Creates a new value tuple with 6 components (a sexuple).
     * @param item1 The value of the value tuple's first component.
     * @param item2 The value of the value tuple's second component.
     * @param item3 The value of the value tuple's third component.
     * @param item4 The value of the value tuple's fourth component.
     * @param item5 The value of the value tuple's fifth component.
     * @param item6 The value of the value tuple's sixth component.
     * @return A value tuple with 6 components.
     * @param <T1> The type of the value tuple's first component.
     * @param <T2> The type of the value tuple's second component.
     * @param <T3> The type of the value tuple's third component.
     * @param <T4> The type of the value tuple's fourth component.
     * @param <T5> The type of the value tuple's fifth component.
     * @param <T6> The type of the value tuple's sixth component.
     */
    @NotNull
    public static <T1, T2, T3, T4, T5, T6> ValueTuple6<T1, T2, T3, T4, T5, T6> Create(
            @AllowNull T1 item1, @AllowNull T2 item2,
            @AllowNull T3 item3, @AllowNull T4 item4,
            @AllowNull T5 item5, @AllowNull T6 item6
    ) { return new ValueTuple6<>(item1, item2, item3, item4, item5, item6); }

    /**
     * Creates a new value tuple with 7 components (a septuple).
     * @param item1 The value of the value tuple's first component.
     * @param item2 The value of the value tuple's second component.
     * @param item3 The value of the value tuple's third component.
     * @param item4 The value of the value tuple's fourth component.
     * @param item5 The value of the value tuple's fifth component.
     * @param item6 The value of the value tuple's sixth component.
     * @param item7 The value of the value tuple's seventh component.
     * @return A value tuple with 7 components.
     * @param <T1> The type of the value tuple's first component.
     * @param <T2> The type of the value tuple's second component.
     * @param <T3> The type of the value tuple's third component.
     * @param <T4> The type of the value tuple's fourth component.
     * @param <T5> The type of the value tuple's fifth component.
     * @param <T6> The type of the value tuple's sixth component.
     * @param <T7> The type of the value tuple's seventh component.
     */
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7> ValueTuple7<T1, T2, T3, T4, T5, T6, T7> Create(
            @AllowNull T1 item1, @AllowNull T2 item2,
            @AllowNull T3 item3, @AllowNull T4 item4,
            @AllowNull T5 item5, @AllowNull T6 item6,
            @AllowNull T7 item7
    ) { return new ValueTuple7<>(item1, item2, item3, item4, item5, item6, item7); }

    /**
     * Creates a new value tuple with 8 components (an octuple).
     * @param item1 The value of the value tuple's first component.
     * @param item2 The value of the value tuple's second component.
     * @param item3 The value of the value tuple's third component.
     * @param item4 The value of the value tuple's fourth component.
     * @param item5 The value of the value tuple's fifth component.
     * @param item6 The value of the value tuple's sixth component.
     * @param item7 The value of the value tuple's seventh component.
     * @param item8 The value of the value tuple's eighth component.
     * @return A value tuple with 8 components.
     * @param <T1> The type of the value tuple's first component.
     * @param <T2> The type of the value tuple's second component.
     * @param <T3> The type of the value tuple's third component.
     * @param <T4> The type of the value tuple's fourth component.
     * @param <T5> The type of the value tuple's fifth component.
     * @param <T6> The type of the value tuple's sixth component.
     * @param <T7> The type of the value tuple's seventh component.
     * @param <T8> The type of the value tuple's eighth component.
     */
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8> ValueTuple8<T1, T2, T3, T4, T5, T6, T7, ValueTuple1<T8>> Create(
            @AllowNull T1 item1, @AllowNull T2 item2,
            @AllowNull T3 item3, @AllowNull T4 item4,
            @AllowNull T5 item5, @AllowNull T6 item6,
            @AllowNull T7 item7, @AllowNull T8 item8
    ) { return new ValueTuple8<>(item1, item2, item3, item4, item5, item6, item7, new ValueTuple1<>(item8)); }
}
