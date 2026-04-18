package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

/**
 * Provides a predicate for signatures with {@link Long} primitive values as their input argument. <br />
 * Extends the {@link NumericPredicate} functional interface.
 * @since 1.0.26
 */
@FunctionalInterface
public interface LongPredicate
        extends NumericPredicate<Long>
{
    /**
     * Tests whether the specified {@link Long long} value meets the specified criteria.
     * @param value The value to compare against the criteria defined within the method represented by this delegate.
     * @return {@code true} if {@code value} meets the criteria defined within the method represented by this delegate; otherwise, {@code false}.
     */
    boolean predicate(long value);

    @Override
    default boolean test(Long obj) { return predicate(obj.longValue()); }

    @Override
    default boolean predicate(Long obj) { return predicate(obj.longValue()); }

    /**
     * Translates the specified {@link LongPredicate} as an {@link IntegerPredicate}.
     * @param predicate The {@link LongPredicate} to translate as an {@link IntegerPredicate}.
     * @return The given {@link LongPredicate}, reinterpreted as an {@link IntegerPredicate}.
     * @throws ArgumentNullException {@code predicate} is {@code null}.
     */
    @NotNull
    public static IntegerPredicate AsIntegerPredicate(LongPredicate predicate)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(predicate, "predicate");
        return new ToIntegerPredicateFromLong(predicate);
    }

    /**
     * Returns a {@link LongPredicate} that does return {@code true}, regardlessly of the input value.
     * @return A new {@link LongPredicate} that does always return {@code true}.
     * @apiNote The returned value has the same semantics, properties and guarantees as the {@link FunctionManipulations#AlwaysTrue()} method return value. <br />
     *          Additionally, the {@link FunctionManipulations#IsAlwaysTrue(java.util.function.Predicate)} method can be normally used for this return value.
     */
    @NotNull
    public static LongPredicate AlwaysTrue() { return new LongAlwaysTruePredicate(); }

    /**
     * Returns a {@link LongPredicate} that does return {@code false}, regardlessly of the input value.
     * @return A new {@link LongPredicate} that does always return {@code false}.
     * @apiNote The returned value has the same semantics, properties and guarantees as the {@link FunctionManipulations#AlwaysFalse()} method return value. <br />
     *          Additionally, the {@link FunctionManipulations#IsAlwaysFalse(java.util.function.Predicate)} method can be normally used for this return value.
     */
    @NotNull
    public static LongPredicate AlwaysFalse() { return new LongAlwaysFalsePredicate(); }
}
