package com.github.mdcdi1315.basemodslib.utils.function;

class FloatAlwaysTruePredicate
    extends AlwaysTruePredicate<Float>
    implements FloatPredicate
{
    @Override
    public boolean predicate(float value) { return true; }

    @Override
    public FloatAlwaysFalsePredicate AsAlwaysFalse() { return new FloatAlwaysFalsePredicate(); }
}
