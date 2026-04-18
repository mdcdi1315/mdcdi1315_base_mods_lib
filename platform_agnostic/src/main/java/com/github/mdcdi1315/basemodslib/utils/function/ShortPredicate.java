package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

/**
 * Provides a predicate for signatures with {@link Short} primitive values as their input argument. <br />
 * Extends the {@link NumericPredicate} functional interface.
 * @since 1.0.26
 */
@FunctionalInterface
public interface ShortPredicate
        extends NumericPredicate<Short>
{
    /**
     * Tests whether the specified {@link Short short} value meets the specified criteria.
     * @param value The value to compare against the criteria defined within the method represented by this delegate.
     * @return {@code true} if {@code value} meets the criteria defined within the method represented by this delegate; otherwise, {@code false}.
     */
    boolean predicate(short value);

    @Override
    default boolean test(Short obj) { return predicate(obj.shortValue()); }

    @Override
    default boolean predicate(Short obj) { return predicate(obj.shortValue()); }

    /**
     * Returns a {@link ShortPredicate} that does return {@code true}, regardlessly of the input value.
     * @return A new {@link ShortPredicate} that does always return {@code true}.
     * @apiNote The returned value has the same semantics, properties and guarantees as the {@link FunctionManipulations#AlwaysTrue()} method return value. <br />
     *          Additionally, the {@link FunctionManipulations#IsAlwaysTrue(java.util.function.Predicate)} method can be normally used for this return value.
     */
    @NotNull
    public static ShortPredicate AlwaysTrue() { return new ShortAlwaysTruePredicate(); }

    /**
     * Returns a {@link ShortPredicate} that does return {@code false}, regardlessly of the input value.
     * @return A new {@link ShortPredicate} that does always return {@code false}.
     * @apiNote The returned value has the same semantics, properties and guarantees as the {@link FunctionManipulations#AlwaysFalse()} method return value. <br />
     *          Additionally, the {@link FunctionManipulations#IsAlwaysFalse(java.util.function.Predicate)} method can be normally used for this return value.
     */
    @NotNull
    public static ShortPredicate AlwaysFalse() { return new ShortAlwaysFalsePredicate(); }
}
