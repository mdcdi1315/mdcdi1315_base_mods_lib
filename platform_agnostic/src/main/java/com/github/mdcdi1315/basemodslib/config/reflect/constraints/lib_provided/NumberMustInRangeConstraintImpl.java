package com.github.mdcdi1315.basemodslib.config.reflect.constraints.lib_provided;

public final class NumberMustInRangeConstraintImpl
    extends GenericConfigFieldConstraint<Number>
{
    private final int min, max;

    public NumberMustInRangeConstraintImpl(NumberMustBeInRange annotation)
    {
        min = annotation.min_inclusive();
        max = annotation.max_inclusive();
    }

    @Override
    public boolean IsSatisfiedGeneric(Number raw_value)
    {
        int rv = raw_value.intValue();
        return rv >= min && rv <= max;
    }

    @Override
    public Class<Number> AppliesTo() { return Number.class; }
}
