package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Predicate;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

record AndPredicateFromTwo<T>(Predicate<T> p1, Predicate<T> p2)
    implements Predicate<T>
{
    @Override
    public boolean test(T t) { return p1.predicate(t) && p2.predicate(t); }

    @Override // We can override this!
    public @NotNull Predicate<T> negate() { return new NegatedPredicate<>(this); }

    @Override
    public boolean predicate(T obj) { return p1.predicate(obj) && p2.predicate(obj); }

    @Override
    public Predicate<T> or(java.util.function.Predicate<? super T> other) { return new CompatibleOrPredicateImpl<>(this, other); }

    @Override
    public Predicate<T> and(java.util.function.Predicate<? super T> other) { return new CompatibleAndPredicateImpl<>(this, other); }
}
