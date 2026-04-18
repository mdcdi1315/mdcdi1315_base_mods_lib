package com.github.mdcdi1315.basemodslib.utils.function;

class ShortAlwaysFalsePredicate
    extends AlwaysFalsePredicate<Short>
    implements ShortPredicate
{
    @Override
    public boolean predicate(short value) { return false; }

    @Override
    public ShortAlwaysTruePredicate AsAlwaysTrue() { return new ShortAlwaysTruePredicate(); }
}
