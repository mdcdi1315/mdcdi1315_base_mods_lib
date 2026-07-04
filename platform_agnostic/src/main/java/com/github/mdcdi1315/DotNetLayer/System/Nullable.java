package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.ClassIsDotNetStruct;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

/**
 * Represents a value type (i.e. {@link ValueType}) that can be assigned {@code null}.
 * @param <T> The underlying value type of the {@link Nullable} generic type.
 */
@ClassIsDotNetStruct
public final class Nullable<T extends ValueType> // Java: T extends ValueType C#: where T : struct
    extends ValueType
{
    private final boolean hasValue; // Do not rename (binary serialization)
    private T value; // Do not rename (binary serialization) or make readonly (can be mutated in ToString, etc.)

    /**
     * Empty constructor, as defined in {@link ValueType} class.
     */
    public Nullable()
    {
        super();
        hasValue = false;
        this.value = null;
    }

    /**
     * Initializes a new instance of the {@link Nullable} structure to the specified value.
     * @param value A value type.
     */
    public Nullable(T value)
    {
        super();
        ValidateNonNullStructure(value);
        this.value = value;
        hasValue = true;
    }

    /**
     * Gets a value indicating whether the current {@link Nullable} object has a valid value of its underlying type.
     * @return {@code true} if the current {@link Nullable} object has a value; {@code false} if the current {@link Nullable} object has no value.
     */
    // [NonVersionable]
    // readonly
    public boolean GetHasValue() { return hasValue; }

    /**
     * Gets the value of the current {@link Nullable} object if it has been assigned a valid underlying value.
     * @return The value of the current {@link Nullable} object if the {@link #GetHasValue} method is true.
     * An exception is thrown if the {@link #GetHasValue} method is false.
     * @throws InvalidOperationException The {@link Nullable} is empty.
     */
    // readonly
    @NotNull
    public T GetValue()
        throws InvalidOperationException
    {
        if (!hasValue) {
            // ThrowHelper.ThrowInvalidOperationException_InvalidOperation_NoValue();
            throw new InvalidOperationException("No value was assigned for this instance.");
        } else {
            return value;
        }
    }

    /**
     * Retrieves the value of the current {@link Nullable} object, or the default value of the underlying type.
     * @return The value of the {@link #GetValue} method if the {@link #GetHasValue} method is {@code true};
     * otherwise, the default value of the underlying type.
     */
    // [NonVersionable]
    // readonly
    @MaybeNull
    public T GetValueOrDefault() { return value; }

    /**
     * Retrieves the value of the current {@link Nullable} object, or the specified default value.
     * @param defaultValue A value to return if the {@link #GetHasValue} method is false.
     * @return The value of the {@link #GetValue} method if the {@link #GetHasValue} method is {@code true}; otherwise, the {@code defaultValue} parameter.
     */
    // [NonVersionable]
    // readonly
    @MaybeNull
    public T GetValueOrDefault(@AllowNull T defaultValue) { return hasValue ? value : defaultValue; }

    /**
     * Indicates whether the current {@link Nullable} object is equal to a specified object.
     * @param other An object.
     * @return {@code true} if the {@code other} parameter is equal to the current {@link Nullable} object; otherwise, {@code false}.
     */
    public boolean Equals(@MaybeNull Object other) {
        return (hasValue) ? (other != null && value.Equals(other)) : other == null;
    }

    /**
     * Retrieves the hash code of the object returned by the {@link #GetValue} method.
     * @return The hash code of the object returned by the {@link #GetValue} method if the {@link #GetHasValue} method is {@code true}, or zero if the {@link #GetHasValue} method is {@code false}.
     */
    public int GetHashCode() { return hasValue ? value.GetHashCode() : 0; }

    /**
     * Returns the text representation of the value of the current {@link Nullable} object.
     * @return The text representation of the value of the current {@link Nullable} object
     * if the {@link #GetHasValue} method is {@code true}, or an empty string ("") if the {@link #GetHasValue} method is {@code false}.
     */
    @MaybeNull
    public String ToString() { return hasValue ? value.ToString() : StringUtils.Empty; }
}
