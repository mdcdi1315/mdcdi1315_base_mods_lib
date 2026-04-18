package com.github.mdcdi1315.basemodslib.utils.function;

class ShortAlwaysTruePredicate
    extends AlwaysTruePredicate<Short>
    implements ShortPredicate
{
    @Override
    public boolean predicate(short value) { return true; }

    @Override
    public ShortAlwaysFalsePredicate AsAlwaysFalse() { return new ShortAlwaysFalsePredicate(); }
}
