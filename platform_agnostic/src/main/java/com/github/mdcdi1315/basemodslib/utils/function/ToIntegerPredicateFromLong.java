package com.github.mdcdi1315.basemodslib.utils.function;

record ToIntegerPredicateFromLong(LongPredicate predicate)
    implements IntegerPredicate
{
    @Override
    public boolean predicate(int value) { return predicate.predicate(value); }

    @Override
    public boolean test(Integer obj) { return predicate.predicate(obj.longValue()); }

    @Override
    public boolean predicate(Integer obj) { return predicate.predicate(obj.longValue()); }
}
