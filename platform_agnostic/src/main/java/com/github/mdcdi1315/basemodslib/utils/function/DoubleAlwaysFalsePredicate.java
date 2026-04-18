package com.github.mdcdi1315.basemodslib.utils.function;

class DoubleAlwaysFalsePredicate
    extends AlwaysFalsePredicate<Double>
    implements DoublePredicate
{
    @Override
    public boolean predicate(double value) { return false; }

    @Override
    public DoubleAlwaysTruePredicate AsAlwaysTrue() { return new DoubleAlwaysTruePredicate(); }
}
