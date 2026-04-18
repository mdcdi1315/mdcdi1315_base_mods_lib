package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

/**
 * Provides a predicate for signatures with {@link Byte} primitive values as their input argument. <br />
 * Extends the {@link NumericPredicate} functional interface.
 * @since 1.0.26
 */
@FunctionalInterface
public interface BytePredicate
    extends NumericPredicate<Byte>
{
    /**
     * Tests whether the specified {@link Byte byte} value meets the specified criteria.
     * @param value The value to compare against the criteria defined within the method represented by this delegate.
     * @return {@code true} if {@code value} meets the criteria defined within the method represented by this delegate; otherwise, {@code false}.
     */
    boolean predicate(byte value);

    @Override
    default boolean test(Byte obj) { return predicate(obj.byteValue()); }

    @Override
    default boolean predicate(Byte obj) { return predicate(obj.byteValue()); }

    /**
     * Returns a {@link BytePredicate} that does return {@code true}, regardlessly of the input value.
     * @return A new {@link BytePredicate} that does always return {@code true}.
     * @apiNote The returned value has the same semantics, properties and guarantees as the {@link FunctionManipulations#AlwaysTrue()} method return value. <br />
     *          Additionally, the {@link FunctionManipulations#IsAlwaysTrue(java.util.function.Predicate)} method can be normally used for this return value.
     */
    @NotNull
    public static BytePredicate AlwaysTrue() { return new ByteAlwaysTruePredicate(); }

    /**
     * Returns a {@link BytePredicate} that does return {@code false}, regardlessly of the input value.
     * @return A new {@link BytePredicate} that does always return {@code false}.
     * @apiNote The returned value has the same semantics, properties and guarantees as the {@link FunctionManipulations#AlwaysFalse()} method return value. <br />
     *          Additionally, the {@link FunctionManipulations#IsAlwaysFalse(java.util.function.Predicate)} method can be normally used for this return value.
     */
    @NotNull
    public static BytePredicate AlwaysFalse() { return new ByteAlwaysFalsePredicate(); }
}
