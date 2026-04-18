package com.github.mdcdi1315.basemodslib.utils.function;

import java.util.function.Predicate;

record ToIntegerPredicateFromFloat(FloatPredicate predicate)
        implements IntegerPredicate
{
    @Override
    public boolean predicate(int value) { return predicate.predicate(value); }

    @Override
    public Predicate<Integer> negate() { return new NegatedPredicate<>(this); }

    @Override
    public boolean test(Integer obj) { return predicate.predicate(obj.floatValue()); }

    @Override
    public boolean predicate(Integer obj) { return predicate.predicate(obj.floatValue()); }
}
