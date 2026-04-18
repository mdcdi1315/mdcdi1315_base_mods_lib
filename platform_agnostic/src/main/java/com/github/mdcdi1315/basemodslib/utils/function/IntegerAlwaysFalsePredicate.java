package com.github.mdcdi1315.basemodslib.utils.function;

class IntegerAlwaysFalsePredicate
    extends AlwaysFalsePredicate<Integer>
    implements IntegerPredicate
{
    @Override
    public boolean predicate(int value) { return false; }

    @Override
    public IntegerAlwaysTruePredicate AsAlwaysTrue() { return new IntegerAlwaysTruePredicate(); }
}
