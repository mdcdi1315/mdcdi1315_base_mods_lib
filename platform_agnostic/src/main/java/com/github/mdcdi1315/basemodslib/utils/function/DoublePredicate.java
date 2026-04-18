package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

/**
 * Provides a predicate for signatures with {@link Double} primitive values as their input argument. <br />
 * Extends the {@link NumericPredicate} functional interface.
 * @since 1.0.26
 * @apiNote While there is the {@link java.util.function.DoublePredicate} predicate by Java,
 * this is an aggregated predicate and implements multiple predicates and does provide utility methods. <br />
 * Use this instead where appropriate.
 */
@FunctionalInterface
public interface DoublePredicate
        extends NumericPredicate<Double>
{
    /**
     * Tests whether the specified {@link Double double} value meets the specified criteria.
     * @param value The value to compare against the criteria defined within the method represented by this delegate.
     * @return {@code true} if {@code value} meets the criteria defined within the method represented by this delegate; otherwise, {@code false}.
     */
    boolean predicate(double value);

    @Override
    default boolean test(Double obj) { return predicate(obj.doubleValue()); }

    @Override
    default boolean predicate(Double obj) { return predicate(obj.doubleValue()); }

    /**
     * Translates the specified {@link DoublePredicate} as a {@link FloatPredicate}.
     * @param predicate The {@link DoublePredicate} to translate as a {@link FloatPredicate}.
     * @return The given {@link DoublePredicate}, reinterpreted as a {@link FloatPredicate}.
     * @throws ArgumentNullException {@code predicate} is {@code null}.
     */
    @NotNull
    public static FloatPredicate AsFloatPredicate(DoublePredicate predicate)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(predicate, "predicate");
        return new ToFloatPredicateFromDouble(predicate);
    }

    /**
     * Translates the specified {@link DoublePredicate} as an {@link IntegerPredicate}.
     * @param predicate The {@link DoublePredicate} to translate as an {@link IntegerPredicate}.
     * @return The given {@link DoublePredicate}, reinterpreted as an {@link IntegerPredicate}.
     * @throws ArgumentNullException {@code predicate} is {@code null}.
     */
    @NotNull
    public static IntegerPredicate AsIntegerPredicate(DoublePredicate predicate)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(predicate, "predicate");
        return new ToIntegerPredicateFromDouble(predicate);
    }

    /**
     * Returns a {@link DoublePredicate} that does return {@code true}, regardlessly of the input value.
     * @return A new {@link DoublePredicate} that does always return {@code true}.
     * @apiNote The returned value has the same semantics, properties and guarantees as the {@link FunctionManipulations#AlwaysTrue()} method return value. <br />
     *          Additionally, the {@link FunctionManipulations#IsAlwaysTrue(java.util.function.Predicate)} method can be normally used for this return value.
     */
    @NotNull
    public static DoublePredicate AlwaysTrue() { return new DoubleAlwaysTruePredicate(); }

    /**
     * Returns a {@link DoublePredicate} that does return {@code false}, regardlessly of the input value.
     * @return A new {@link DoublePredicate} that does always return {@code false}.
     * @apiNote The returned value has the same semantics, properties and guarantees as the {@link FunctionManipulations#AlwaysFalse()} method return value. <br />
     *          Additionally, the {@link FunctionManipulations#IsAlwaysFalse(java.util.function.Predicate)} method can be normally used for this return value.
     */
    @NotNull
    public static DoublePredicate AlwaysFalse() { return new DoubleAlwaysFalsePredicate(); }
}