package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

/**
 * Provides a functional interface for bi-predicates, that is predicates that are accepting two input arguments instead of one.
 * @param <T1> The first input argument type.
 * @param <T2> The second input argument type.
 * @since 1.0.31
 */
@FunctionalInterface
public interface BiPredicate<T1, T2>
    extends java.util.function.BiPredicate<T1, T2>
{
    /**
     * Evaluates the bi-predicate, given the two input arguments.
     * @param input_1 The first input argument.
     * @param input_2 The second input argument.
     * @return {@code true} if the input arguments match the predicate, otherwise {@code false}.
     */
    boolean predicate(T1 input_1, T2 input_2);

    @Override
    default boolean test(T1 input_1, T2 input_2) { return predicate(input_1, input_2); }

    @NotNull
    @Override
    default BiPredicate<T1, T2> negate() { return new NegatedBiPredicate<>(this); }

    @NotNull
    @Override
    default BiPredicate<T1, T2> or(java.util.function.BiPredicate<? super T1, ? super T2> other)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(other, "other");
        return new OrBiPredicateImpl<>(this, other);
    }

    @NotNull
    @Override
    default BiPredicate<T1, T2> and(java.util.function.BiPredicate<? super T1, ? super T2> other)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(other, "other");
        return new AndBiPredicateImpl<>(this, other);
    }

    /**
     * Returns a composed predicate that represents a short-circuiting logical XOR of this predicate and another.
     *
     * <p>Any exceptions thrown during evaluation of either predicate are relayed
     * to the caller; if evaluation of this predicate throws an exception, the
     * {@code other} predicate will not be evaluated.
     *
     * @param other A predicate that will be logically-XORed with this predicate
     * @return A composed predicate that represents the short-circuiting logical XOR of this predicate and the {@code other} predicate
     * @throws ArgumentNullException If {@code other} is {@code null}
     */
    @NotNull
    default BiPredicate<T1, T2> Xor(java.util.function.BiPredicate<? super T1, ? super T2> other)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(other, "other");
        return new XorBiPredicateImpl<>(this, other);
    }
}
