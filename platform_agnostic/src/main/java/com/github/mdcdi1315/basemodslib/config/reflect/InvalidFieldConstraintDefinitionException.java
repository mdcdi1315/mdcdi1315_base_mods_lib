package com.github.mdcdi1315.basemodslib.config.reflect;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.config.reflect.constraints.IConfigFieldConstraint;

/**
 * Provides a special derivant of the {@link InvalidFieldLayoutException} in that
 * this defines that a constraint was applied in incorrect field backing type.
 */
public final class InvalidFieldConstraintDefinitionException
    extends InvalidFieldLayoutException
{
    private final IConfigFieldConstraint constraint;
    private final Class<?> attempted_for_field_class;

    public InvalidFieldConstraintDefinitionException(IConfigFieldConstraint constraint, Class<?> field_class)
    {
        super("A constraint was applied in incorrect field type.");
        ArgumentNullException.ThrowIfNull(constraint, "constraint");
        ArgumentNullException.ThrowIfNull(field_class, "field_class");
        this.constraint = constraint;
        this.attempted_for_field_class = field_class;
    }

    @NotNull
    public Class<?> GetFieldClass() { return attempted_for_field_class; }

    @NotNull
    public IConfigFieldConstraint GetInvalidConstraint() { return constraint; }

    @NotNull
    @Override
    public String getMessage()
    {
        StringBuilder sb = new StringBuilder(super.getMessage());
        sb.append('\n');
        sb.append("Constraint class name: ");
        sb.append(constraint.getClass().getName());
        sb.append('\n');
        sb.append("Expected field class name: ");
        sb.append(constraint.AppliesTo().getName());
        sb.append('\n');
        sb.append("Actual field class name: ");
        sb.append(attempted_for_field_class.getName());
        sb.append('\n');
        return sb.toString();
    }
}
