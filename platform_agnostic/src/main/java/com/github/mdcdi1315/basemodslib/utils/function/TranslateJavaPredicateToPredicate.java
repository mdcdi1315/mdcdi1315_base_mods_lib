package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Predicate;

record TranslateJavaPredicateToPredicate<T>(java.util.function.Predicate<T> predicate)
    implements Predicate<T>
{
    @Override
    public boolean test(T t) { return predicate.test(t); }

    @Override
    public boolean predicate(T obj) { return predicate.test(obj); }

    @Override
    public Predicate<T> negate() { return new NegatedPredicate<>(this); }

    @Override
    public Predicate<T> or(java.util.function.Predicate<? super T> other) { return new CompatibleOrPredicateImpl<>(this, other); }

    @Override
    public Predicate<T> and(java.util.function.Predicate<? super T> other) { return new CompatibleAndPredicateImpl<>(this, other); }
}
