package com.github.mdcdi1315.basemodslib.utils.function;

import java.util.function.Predicate;

record ToFloatPredicateFromDouble(DoublePredicate predicate)
    implements FloatPredicate
{
    @Override
    public Predicate<Float> negate() { return new NegatedPredicate<>(this); }

    @Override
    public boolean predicate(float value) { return predicate.predicate(value); }

    @Override
    public boolean test(Float obj) { return predicate.predicate(obj.doubleValue()); }

    @Override
    public boolean predicate(Float obj) { return predicate.predicate(obj.doubleValue()); }
}
