package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Predicate;

record CompatibleOrPredicateImpl<T>(Predicate<T> predicate, java.util.function.Predicate<? super T> other)
    implements Predicate<T>
{
    @Override
    public Predicate<T> negate() { return new NegatedPredicate<>(this); }

    @Override
    public boolean test(T obj) { return predicate.predicate(obj) || other.test(obj); }

    @Override
    public boolean predicate(T obj) { return predicate.predicate(obj) || other.test(obj); }

    @Override
    public Predicate<T> or(java.util.function.Predicate<? super T> other) { return new CompatibleOrPredicateImpl<>(this, other); }

    @Override
    public Predicate<T> and(java.util.function.Predicate<? super T> other) { return new CompatibleAndPredicateImpl<>(this, other); }
}