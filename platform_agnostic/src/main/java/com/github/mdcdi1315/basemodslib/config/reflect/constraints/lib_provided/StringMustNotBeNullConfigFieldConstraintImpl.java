package com.github.mdcdi1315.basemodslib.config.reflect.constraints.lib_provided;

public final class StringMustNotBeNullConfigFieldConstraintImpl
    extends GenericConfigFieldConstraint<String>
{
    public StringMustNotBeNullConfigFieldConstraintImpl(StringMustNotBeNull d) { }

    @Override
    public Class<String> AppliesTo() { return String.class; }

    @Override
    public boolean IsSatisfiedGeneric(String raw_value) { return raw_value != null; }
}
