package com.github.mdcdi1315.basemodslib.config.reflect.constraints;

import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import java.lang.annotation.Annotation;

/**
 * Provides the way for creating {@link IConfigFieldConstraint} instances out of raw {@link Annotation} instances of type {@link T}.
 * @param <T> The type of the {@link Annotation} to create an {@link IConfigFieldConstraint} from.
 */
@FunctionalInterface
public interface ConfigFieldConstraintCreator<T extends Annotation>
    extends Func2<T, IConfigFieldConstraint>
{
    /**
     * Returns a {@link ConfigFieldConstraintCreator} that does always return the specified {@link IConfigFieldConstraint} instance.
     * @param constraint The {@link IConfigFieldConstraint} instance to reinterpret.
     * @return A new {@link ConfigFieldConstraintCreator} that upon invoking, it does return the {@link IConfigFieldConstraint} specified in the {@code constraint} parameter.
     * @param <T> The type of the {@link Annotation} that the config field constraint creator supports.
     * @throws ArgumentNullException {@code constraint} is {@code null}.
     */
    @NotNull
    public static <T extends Annotation> ConfigFieldConstraintCreator<T> OfStaticInstance(IConfigFieldConstraint constraint)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(constraint, "constraint");
        return new ConfigFieldConstraintCreator_OfStaticInstanceImpl<>(constraint);
    }
}
