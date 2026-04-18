package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

/**
 * Provides a predicate for signatures with {@link Float} primitive values as their input argument. <br />
 * Extends the {@link NumericPredicate} functional interface.
 * @since 1.0.26
 */
@FunctionalInterface
public interface FloatPredicate
        extends NumericPredicate<Float>
{
    /**
     * Tests whether the specified {@link Float float} value meets the specified criteria.
     * @param value The value to compare against the criteria defined within the method represented by this delegate.
     * @return {@code true} if {@code value} meets the criteria defined within the method represented by this delegate; otherwise, {@code false}.
     */
    boolean predicate(float value);

    @Override
    default boolean test(Float obj) { return predicate(obj.floatValue()); }

    @Override
    default boolean predicate(Float obj) { return predicate(obj.floatValue()); }

    /**
     * Translates the specified {@link FloatPredicate} as an {@link IntegerPredicate}.
     * @param predicate The {@link FloatPredicate} to translate as an {@link IntegerPredicate}.
     * @return The given {@link FloatPredicate}, reinterpreted as an {@link IntegerPredicate}.
     * @throws ArgumentNullException {@code predicate} is {@code null}.
     */
    @NotNull
    public static IntegerPredicate AsIntegerPredicate(FloatPredicate predicate)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(predicate, "predicate");
        return new ToIntegerPredicateFromFloat(predicate);
    }

    /**
     * Returns a {@link FloatPredicate} that does return {@code true}, regardlessly of the input value.
     * @return A new {@link FloatPredicate} that does always return {@code true}.
     * @apiNote The returned value has the same semantics, properties and guarantees as the {@link FunctionManipulations#AlwaysTrue()} method return value. <br />
     *          Additionally, the {@link FunctionManipulations#IsAlwaysTrue(java.util.function.Predicate)} method can be normally used for this return value.
     */
    @NotNull
    public static FloatPredicate AlwaysTrue() { return new FloatAlwaysTruePredicate(); }

    /**
     * Returns a {@link FloatPredicate} that does return {@code false}, regardlessly of the input value.
     * @return A new {@link FloatPredicate} that does always return {@code false}.
     * @apiNote The returned value has the same semantics, properties and guarantees as the {@link FunctionManipulations#AlwaysFalse()} method return value. <br />
     *          Additionally, the {@link FunctionManipulations#IsAlwaysFalse(java.util.function.Predicate)} method can be normally used for this return value.
     */
    @NotNull
    public static FloatPredicate AlwaysFalse() { return new FloatAlwaysFalsePredicate(); }
}
