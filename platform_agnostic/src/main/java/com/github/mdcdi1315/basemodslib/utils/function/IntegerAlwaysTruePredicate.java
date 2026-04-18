package com.github.mdcdi1315.basemodslib.utils.function;

class IntegerAlwaysTruePredicate
    extends AlwaysTruePredicate<Integer>
    implements IntegerPredicate
{
    @Override
    public boolean predicate(int value) { return true; }

    @Override
    public IntegerAlwaysFalsePredicate AsAlwaysFalse() { return new IntegerAlwaysFalsePredicate(); }
}
