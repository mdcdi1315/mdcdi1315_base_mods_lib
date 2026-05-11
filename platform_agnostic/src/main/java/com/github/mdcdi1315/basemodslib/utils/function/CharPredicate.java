package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Predicate;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

/**
 * Provides a predicate for signatures with {@link Character} primitive values as their input argument. <br />
 * Extends the {@link Predicate} functional interface.
 * @since 1.0.26
 */
@FunctionalInterface
public interface CharPredicate
        extends Predicate<Character>
{
    /**
     * Tests whether the specified {@link Character char} value meets the specified criteria.
     * @param value The value to compare against the criteria defined within the method represented by this delegate.
     * @return {@code true} if {@code value} meets the criteria defined within the method represented by this delegate; otherwise, {@code false}.
     */
    boolean predicate(char value);

    @Override
    default boolean test(Character obj) { return predicate(obj.charValue()); }

    @Override
    default boolean predicate(Character obj) { return predicate(obj.charValue()); }

    @Override
    default java.util.function.Predicate<Character> or(java.util.function.Predicate<? super Character> other) { return new CompatibleOrPredicateImpl<>(this, other); }

    @Override
    default java.util.function.Predicate<Character> and(java.util.function.Predicate<? super Character> other) { return new CompatibleAndPredicateImpl<>(this, other); }

    /**
     * From a given {@link CharPredicate}, it builds a {@link Predicate} of type {@link CharSequence}
     * that can check whether all characters in the {@link CharSequence} do pass the given {@code predicate}.
     * @param predicate The {@link Predicate} of a single character to build the {@link CharSequence} predicate from.
     * @return The built {@link CharSequence} predicate.
     * @throws ArgumentNullException {@code predicate} is {@code null}.
     */
    @NotNull
    public static Predicate<CharSequence> TrueForAll(CharPredicate predicate)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(predicate, "predicate");
        if (predicate instanceof CharAlwaysTruePredicate) {
            return FunctionManipulations.AlwaysTrue(); // There is no meaning to enumerate all the characters if there is provided an 'always true character' predicate.
        } else if (predicate instanceof CharAlwaysFalsePredicate) {
            return FunctionManipulations.AlwaysFalse(); // There is no meaning to enumerate all the characters if there is provided an 'always false character' predicate.
        } else {
            return new CharSeq_TrueForAll_CharPredicate(predicate);
        }
    }

    /**
     * Returns a {@link CharPredicate} that does return {@code true}, regardlessly of the input value.
     * @return A new {@link CharPredicate} that does always return {@code true}.
     * @apiNote The returned value has the same semantics, properties and guarantees as the {@link FunctionManipulations#AlwaysTrue()} method return value. <br />
     *          Additionally, the {@link FunctionManipulations#IsAlwaysTrue(java.util.function.Predicate)} method can be normally used for this return value.
     */
    @NotNull
    public static CharPredicate AlwaysTrue() { return new CharAlwaysTruePredicate(); }

    /**
     * Returns a {@link CharPredicate} that does return {@code false}, regardlessly of the input value.
     * @return A new {@link CharPredicate} that does always return {@code false}.
     * @apiNote The returned value has the same semantics, properties and guarantees as the {@link FunctionManipulations#AlwaysFalse()} method return value. <br />
     *          Additionally, the {@link FunctionManipulations#IsAlwaysFalse(java.util.function.Predicate)} method can be normally used for this return value.
     */
    @NotNull
    public static CharPredicate AlwaysFalse() { return new CharAlwaysFalsePredicate(); }
}
