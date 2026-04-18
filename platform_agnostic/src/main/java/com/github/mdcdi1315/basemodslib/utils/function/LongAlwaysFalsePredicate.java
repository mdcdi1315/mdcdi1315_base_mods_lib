package com.github.mdcdi1315.basemodslib.utils.function;

class LongAlwaysFalsePredicate
    extends AlwaysFalsePredicate<Long>
    implements LongPredicate
{
    @Override
    public boolean predicate(long value) { return false; }

    @Override
    public LongAlwaysTruePredicate AsAlwaysTrue() { return new LongAlwaysTruePredicate(); }
}
