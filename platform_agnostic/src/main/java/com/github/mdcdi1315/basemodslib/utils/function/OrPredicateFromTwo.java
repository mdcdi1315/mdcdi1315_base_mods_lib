package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Predicate;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

record OrPredicateFromTwo<T>(Predicate<T> p1, Predicate<T> p2)
    implements Predicate<T>
{
    @Override
    public boolean test(T obj) { return p1.predicate(obj) || p2.predicate(obj); }

    @Override // We can override this!
    public @NotNull Predicate<T> negate() { return new NegatedPredicate<>(this); }

    @Override
    public boolean predicate(T obj) { return p1.predicate(obj) || p2.predicate(obj); }
}
