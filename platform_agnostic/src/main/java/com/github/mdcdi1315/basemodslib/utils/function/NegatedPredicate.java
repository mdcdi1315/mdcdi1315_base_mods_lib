package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Predicate;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

record NegatedPredicate<T>(Predicate<T> p)
    implements Predicate<T>
{
    @NotNull
    @Override // Note, negating again is like doing: ((predicate_value == false) == false), which by definition can be simplified to (predicate_value).
    public Predicate<T> negate() { return p; }

    @Override
    public boolean test(T t) { return !p.predicate(t); }

    @Override
    public boolean predicate(T obj) { return !p.predicate(obj); }

    @Override
    public Predicate<T> or(java.util.function.Predicate<? super T> other) { return new CompatibleOrPredicateImpl<>(this, other); }

    @Override
    public Predicate<T> and(java.util.function.Predicate<? super T> other) { return new CompatibleAndPredicateImpl<>(this, other); }
}
