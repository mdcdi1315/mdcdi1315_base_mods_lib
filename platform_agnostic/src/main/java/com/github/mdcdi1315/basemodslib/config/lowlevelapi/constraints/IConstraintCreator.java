package com.github.mdcdi1315.basemodslib.config.lowlevelapi.constraints;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;

import com.github.mdcdi1315.basemodslib.config.lowlevelapi.IConfigFieldConstraint;

import java.lang.annotation.Annotation;

/**
 * Creates a {@link IConfigFieldConstraint} by parsing an annotation.
 * @param <T> The field type that the created constraint applies to.
 * @since 1.0.15
 */
@FunctionalInterface
public interface IConstraintCreator<T>
{
    /**
     * Constructs an {@link IConfigFieldConstraint} for the given bridged annotation type.
     * @param annotation_instance The annotation interface instance to get information to construct the constraint.
     * @return The constructed configuration field constraint.
     */
    @NotNull
    IConfigFieldConstraint<T> ConstructConstraint(@DisallowNull Annotation annotation_instance);
}
