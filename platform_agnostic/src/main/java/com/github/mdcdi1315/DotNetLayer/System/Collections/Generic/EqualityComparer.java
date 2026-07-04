package com.github.mdcdi1315.DotNetLayer.System.Collections.Generic;

import com.github.mdcdi1315.DotNetLayer.System.*;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNullWhen;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;

import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.MethodImpl;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.MethodImplOptions;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.TypeForwardedFrom;

/**
 * Provides a base class for implementations of the {@link IEqualityComparer} generic interface.
 * @param <T> The type of objects to compare.
 */
@TypeForwardedFrom(AssemblyFullName = "mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089")
public abstract class EqualityComparer<T>
    implements IEqualityComparer<T>
{
    /**
     * Creates an {@link EqualityComparer} by using the specified delegates as the implementation of the comparer's
     * {@link IEqualityComparer#Equals(Object, Object)} and {@link IEqualityComparer#GetHashCode(Object)} methods.
     * @param equals The delegate to use to implement the {@link IEqualityComparer#Equals(Object, Object)} method.
     * @param getHashCode The delegate to use to implement the {@link IEqualityComparer#GetHashCode(Object)} method.
     * If no delegate is supplied, calls to the resulting comparer's {@link IEqualityComparer#GetHashCode(Object)}
     * will throw {@link NotSupportedException}.
     * @return The new comparer.
     * @param <T> The type of input object to define the comparison operations for.
     * @throws ArgumentNullException The {@code equals} delegate is {@code null}.
     */
    public static <T> EqualityComparer<T> Create(Func3<T, T, Boolean> equals, @AllowNull Func2<T, Integer> getHashCode)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(equals, "equals");

        if (getHashCode == null) { getHashCode = new EmptyHashCodeFuncDef<>(); }

        return new DelegateEqualityComparer<>(equals, getHashCode);
    }

    /**
     * Returns a default equality comparer for the type specified by the generic argument.
     * @return The default instance of the {@link EqualityComparer} class for type {@link T}.
     * @param <T> The type of objects to compare.
     * @implNote Because Java does not have the ability to infer the class of type {@link T}, this method
     * does always return an instance of the {@link ObjectEqualityComparer} class, and as such, some comparisons may fail. <br />
     * The best way to tackle this is to use the appropriate comparer for the type, by using one of the exposed comparers.
     */
    public static <T> EqualityComparer<T> GetDefault() { return new ObjectEqualityComparer<>(); }

    private record EmptyHashCodeFuncDef<T>()
        implements Func2<T, Integer>
    {
        @Override
        public Integer function(T input) { throw new NotSupportedException(); }
    }

    private static final class DelegateEqualityComparer<T>
            extends EqualityComparer<T>
    {
        private final Func3<T, T, Boolean> _equals;
        private final Func2<T, Integer> _getHashCode;

        public DelegateEqualityComparer(Func3<T, T, Boolean> equals, Func2<T, Integer> getHashCode)
        {
            _equals = equals;
            _getHashCode = getHashCode;
        }

        @Override
        public int GetHashCode(@DisallowNull T obj) { return _getHashCode.function(obj); }

        @Override
        public boolean Equals(@AllowNull T x, @AllowNull T y) { return _equals.function(x, y); }

        @Override
        @SuppressWarnings("rawtypes")
        public boolean equals(@AllowNull @NotNullWhen(ReturnValue = true) Object obj)
        {
            return obj instanceof DelegateEqualityComparer other &&
                _equals == other._equals &&
                _getHashCode == other._getHashCode;
        }

        @Override
        public int hashCode() { return HashCode.Combine(_equals, _getHashCode); }
    }

    @TypeForwardedFrom(AssemblyFullName = "mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089")
    public static final class GenericEqualityComparer<T extends IEquatable<T>>
        extends EqualityComparer<T>
    {
        @Override
        public boolean Equals(T x, T y)
        {
            boolean y_is_null = y == null;
            return (x == null) ? y_is_null : (!y_is_null && x.Equals(y));
        }

        @Override
        @MethodImpl(GetValue = MethodImplOptions.AggressiveInlining)
        public int GetHashCode(T obj) { return obj == null ? 0 : obj.hashCode(); }

        @Override
        public boolean equals(@AllowNull @NotNullWhen(ReturnValue = true) Object obj) { return obj != null && getClass().equals(obj.getClass()); }

        @Override
        public int hashCode() { return getClass().hashCode(); }
    }

    @TypeForwardedFrom(AssemblyFullName = "mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089")
    public static final class NullableEqualityComparer<T extends ValueType>
        extends EqualityComparer<Nullable<T>>
    {
        @Override
        public boolean Equals(Nullable<T> x, Nullable<T> y)
        {
            ValueType.ValidateNonNullStructure(x);
            ValueType.ValidateNonNullStructure(y);
            boolean y_has_value = y.GetHasValue();
            return (x.GetHasValue()) ?
                    (y_has_value && GetDefault().Equals(x.GetValueOrDefault(), y.GetValueOrDefault())) :
                    !y_has_value;
        }

        @Override
        public int GetHashCode(Nullable<T> obj)
        {
            ValueType.ValidateNonNullStructure(obj);
            return obj.hashCode();
        }

        @Override
        // Equals method for the comparer itself.
        public boolean equals(@AllowNull @NotNullWhen(ReturnValue = true) Object obj) { return obj != null && getClass().equals(obj.getClass()); }

        @Override
        public int hashCode() { return getClass().hashCode(); }
    }

    @TypeForwardedFrom(AssemblyFullName = "mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089")
    public static final class ObjectEqualityComparer<T>
        extends EqualityComparer<T>
    {
        @Override
        public boolean Equals(T x, T y)
        {
            boolean y_is_null = y == null;
            return (x == null) ? y_is_null : (!y_is_null && x.equals(y));
        }

        @Override
        @MethodImpl(GetValue = MethodImplOptions.AggressiveInlining)
        public int GetHashCode(T obj) { return obj == null ? 0 : obj.hashCode(); }

        @Override
        // Equals method for the comparer itself.
        public boolean equals(@AllowNull @NotNullWhen(ReturnValue = true) Object obj) { return obj != null && getClass().equals(obj.getClass()); }

        @Override
        public int hashCode() { return getClass().hashCode(); }
    }

    @TypeForwardedFrom(AssemblyFullName = "mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089")
    public static final class ByteEqualityComparer
        extends EqualityComparer<Byte>
    {
        @Override
        @MethodImpl(GetValue = MethodImplOptions.AggressiveInlining)
        public boolean Equals(Byte x, Byte y) { return x != null && x.equals(y); }

        @Override
        @MethodImpl(GetValue = MethodImplOptions.AggressiveInlining)
        public int GetHashCode(Byte obj) { return obj.hashCode(); }

        @Override
        // Equals method for the comparer itself.
        public boolean equals(@AllowNull @NotNullWhen(ReturnValue = true) Object obj) { return obj != null && getClass().equals(obj.getClass()); }

        @Override
        public int hashCode() { return getClass().hashCode(); }
    }

    @TypeForwardedFrom(AssemblyFullName = "mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089")
    public static final class EnumEqualityComparer<T extends Enum<T>>
        extends EqualityComparer<T>
    {
        // The .NET runtime marks Equals as runtime-specific, but since we have Java's Enum as base class, we can use Enum.equals method directly.

        @Override
        public boolean Equals(T x, T y) { return x != null && x.equals(y); }

        @Override
        @MethodImpl(GetValue = MethodImplOptions.AggressiveInlining)
        public int GetHashCode(T obj) { return obj == null ? 0 : obj.hashCode(); }

        @Override
        // Equals method for the comparer itself.
        public boolean equals(@AllowNull @NotNullWhen(ReturnValue = true) Object obj) { return obj != null && getClass().equals(obj.getClass()); }

        @Override
        public int hashCode() { return getClass().hashCode(); }
    }

    @TypeForwardedFrom(AssemblyFullName = "mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089")
    public static final class StringEqualityComparer
        extends EqualityComparer<CharSequence>
    {
        @Override
        public boolean Equals(CharSequence x, CharSequence y)
        {
            boolean y_is_null = y == null;
            return (x == null || y_is_null) ? y_is_null : CharSequence.compare(x, y) == 0;
        }

        @Override
        public int GetHashCode(CharSequence obj) { return obj == null ? 0 : obj.hashCode(); }

        @Override
        public boolean equals(@AllowNull @NotNullWhen(ReturnValue = true) Object obj) { return obj instanceof StringEqualityComparer; }

        @Override
        public int hashCode() { return getClass().hashCode(); }
    }
}
