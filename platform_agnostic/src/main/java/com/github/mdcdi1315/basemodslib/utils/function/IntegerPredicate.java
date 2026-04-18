package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

/**
 * Provides a predicate for signatures with {@link Integer} primitive values as their input argument. <br />
 * Extends the {@link NumericPredicate} functional interface.
 * @since 1.0.26
 */
@FunctionalInterface
public interface IntegerPredicate
        extends NumericPredicate<Integer>
{
    /**
     * Tests whether the specified {@link Integer int} value meets the specified criteria.
     * @param value The value to compare against the criteria defined within the method represented by this delegate.
     * @return {@code true} if {@code value} meets the criteria defined within the method represented by this delegate; otherwise, {@code false}.
     */
    boolean predicate(int value);

    @Override
    default boolean test(Integer obj) { return predicate(obj.intValue()); }

    @Override
    default boolean predicate(Integer obj) { return predicate(obj.intValue()); }

    /**
     * Returns a {@link IntegerPredicate} that does return {@code true}, regardlessly of the input value.
     * @return A new {@link IntegerPredicate} that does always return {@code true}.
     * @apiNote The returned value has the same semantics, properties and guarantees as the {@link FunctionManipulations#AlwaysTrue()} method return value. <br />
     *          Additionally, the {@link FunctionManipulations#IsAlwaysTrue(java.util.function.Predicate)} method can be normally used for this return value.
     */
    @NotNull
    public static IntegerPredicate AlwaysTrue() { return new IntegerAlwaysTruePredicate(); }

    /**
     * Returns a {@link IntegerPredicate} that does return {@code false}, regardlessly of the input value.
     * @return A new {@link IntegerPredicate} that does always return {@code false}.
     * @apiNote The returned value has the same semantics, properties and guarantees as the {@link FunctionManipulations#AlwaysFalse()} method return value. <br />
     *          Additionally, the {@link FunctionManipulations#IsAlwaysFalse(java.util.function.Predicate)} method can be normally used for this return value.
     */
    @NotNull
    public static IntegerPredicate AlwaysFalse() { return new IntegerAlwaysFalsePredicate(); }
}
