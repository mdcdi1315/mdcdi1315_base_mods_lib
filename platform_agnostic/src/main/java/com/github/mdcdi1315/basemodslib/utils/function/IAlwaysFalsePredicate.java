package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Predicate;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

/**
 * Interface for predicates that are always false. <br />
 * Note: This feature is internal and should not be referred outside the package.
 */
interface IAlwaysFalsePredicate<T>
    extends Predicate<T> // For binding to Predicate directly and avoid typecasts
{
    /**
     * Converts this {@link IAlwaysFalsePredicate} to an instance of the same type that is a {@link IAlwaysTruePredicate}.
     * @return A new {@link IAlwaysTruePredicate} instance.
     */
    @NotNull
    IAlwaysTruePredicate<T> AsAlwaysTrue();
}