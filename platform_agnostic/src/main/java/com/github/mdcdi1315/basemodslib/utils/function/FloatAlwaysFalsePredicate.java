package com.github.mdcdi1315.basemodslib.utils.function;

class FloatAlwaysFalsePredicate
    extends AlwaysFalsePredicate<Float>
    implements FloatPredicate
{
    @Override
    public boolean predicate(float value) { return false; }

    @Override
    public FloatAlwaysTruePredicate AsAlwaysTrue() { return new FloatAlwaysTruePredicate(); }
}
