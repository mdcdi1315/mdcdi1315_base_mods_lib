package com.github.mdcdi1315.basemodslib.config.lowlevelapi.constraints;

import com.github.mdcdi1315.basemodslib.config.lowlevelapi.IConfigField;
import com.github.mdcdi1315.basemodslib.config.lowlevelapi.IConfigFieldConstraint;

/**
 * Provides a constraint that verifies that the specified field will never be {@code null}, otherwise configuration reading or writing will fail.
 */
public final class StringMustNotBeNullConstraint
    implements IConfigFieldConstraint<String>
{
    public static final StringMustNotBeNullConstraint INSTANCE = new StringMustNotBeNullConstraint();

    private StringMustNotBeNullConstraint() {}

    @Override
    public boolean IsSatisfied(IConfigField<String> field) { return field.GetValue() != null; }
}
