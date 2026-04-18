package com.github.mdcdi1315.basemodslib.utils.function;

class LongAlwaysTruePredicate
    extends AlwaysTruePredicate<Long>
    implements LongPredicate
{
    @Override
    public boolean predicate(long value) { return true; }

    @Override
    public LongAlwaysFalsePredicate AsAlwaysFalse() { return new LongAlwaysFalsePredicate(); }
}
