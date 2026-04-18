package com.github.mdcdi1315.basemodslib.config.reflect.constraints.lib_provided;

import com.github.mdcdi1315.basemodslib.config.reflect.constraints.IConfigFieldConstraint;

public abstract class GenericConfigFieldConstraint<T>
    implements IConfigFieldConstraint
{
    public abstract boolean IsSatisfiedGeneric(T raw_value);

    @Override
    public abstract Class<T> AppliesTo();

    @Override
    public final boolean IsSatisfied(Object raw_value)
    {
        T rv;
        try { rv = AppliesTo().cast(raw_value); } catch (ClassCastException cce) { return false; }
        return IsSatisfiedGeneric(rv);
    }
}
