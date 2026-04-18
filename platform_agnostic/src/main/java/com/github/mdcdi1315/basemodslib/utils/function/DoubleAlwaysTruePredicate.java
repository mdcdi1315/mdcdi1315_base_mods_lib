package com.github.mdcdi1315.basemodslib.utils.function;

class DoubleAlwaysTruePredicate
    extends AlwaysTruePredicate<Double>
    implements DoublePredicate
{
    @Override
    public boolean predicate(double value) { return true; }

    @Override
    public DoubleAlwaysFalsePredicate AsAlwaysFalse() { return new DoubleAlwaysFalsePredicate(); }
}
