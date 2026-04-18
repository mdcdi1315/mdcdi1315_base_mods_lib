package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Predicate;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

/**
 * Interface for predicates that are always true. <br />
 * Note: This feature is internal and should not be referred outside the package.
 */
interface IAlwaysTruePredicate<T>
    extends Predicate<T> // For binding to Predicate directly and avoid typecasts
{
    /**
     * Converts this {@link IAlwaysTruePredicate} to an instance of the same type that is a {@link IAlwaysFalsePredicate}.
     * @return A new {@link IAlwaysFalsePredicate} instance.
     */
    @NotNull
    IAlwaysFalsePredicate<T> AsAlwaysFalse();
}