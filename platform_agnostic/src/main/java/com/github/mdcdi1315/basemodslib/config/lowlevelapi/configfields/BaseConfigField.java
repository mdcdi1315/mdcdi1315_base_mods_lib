package com.github.mdcdi1315.basemodslib.config.lowlevelapi.configfields;

import com.github.mdcdi1315.DotNetLayer.System.StringUtils;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

import com.github.mdcdi1315.basemodslib.config.lowlevelapi.IConfigField;
import com.github.mdcdi1315.basemodslib.config.lowlevelapi.IConfigFieldConstraint;

import org.jetbrains.annotations.ApiStatus;

/**
 * Provides a base class for all the library-provided configuration fields. <br />
 * Users of the library should not derive from this class; it is meant to be used only by the library infrastructure itself.
 * @param <T> The backing type of the field.
 * @since 1.0.15
 */
@ApiStatus.Internal
public abstract class BaseConfigField<T>
    implements IConfigField<T>
{
    private final String name, comment;
    private final Iterable<IConfigFieldConstraint<T>> constraints;

    /**
     * Constructs a new instance of the {@link BaseConfigField} class.
     * @param name The name of the newly created field.
     * @param constraints The constraints to apply on the newly created field.
     * @throws ArgumentNullException {@code name} and/or {@code constraints} are {@code null}.
     */
    protected BaseConfigField(String name, Iterable<IConfigFieldConstraint<T>> constraints)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(name, "name");
        ArgumentNullException.ThrowIfNull(constraints, "constraints");
        this.name = name;
        this.constraints = constraints;
        this.comment = StringUtils.Empty;
    }

    /**
     * Constructs a new instance of the {@link BaseConfigField} class.
     * @param name The name of the newly created field.
     * @param comment The comment, if any, of the newly created field.
     * @param constraints The constraints to apply on the newly created field.
     * @throws ArgumentNullException {@code name} and/or {@code constraints} are {@code null}.
     */
    protected BaseConfigField(String name, @AllowNull String comment, Iterable<IConfigFieldConstraint<T>> constraints)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(name, "name");
        ArgumentNullException.ThrowIfNull(constraints, "constraints");
        this.name = name;
        this.comment = (comment == null) ? StringUtils.Empty : comment;
        this.constraints = constraints;
    }

    @Override
    public final String GetName() { return name; }

    @Override
    public final String GetComment() { return comment; }

    @Override
    public final Iterable<IConfigFieldConstraint<T>> GetConstraints() { return constraints; }
}
