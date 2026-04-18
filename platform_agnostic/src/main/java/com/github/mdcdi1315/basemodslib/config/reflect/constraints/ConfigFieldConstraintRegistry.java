package com.github.mdcdi1315.basemodslib.config.reflect.constraints;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.github.mdcdi1315.basemodslib.config.reflect.constraints.lib_provided.*;

import java.util.HashMap;
import java.lang.annotation.Annotation;

/**
 * Provides the way to map annotation instances to in-code {@link IConfigFieldConstraint} instances.
 */
public final class ConfigFieldConstraintRegistry
{
    private ConfigFieldConstraintRegistry() {}

    private static final HashMap<Class<? extends Annotation>, ConfigFieldConstraintCreator<? extends Annotation>> constraints;

    static {
        constraints = new HashMap<>(10);

        PutConstraint(NumberMustBeInRange.class, NumberMustInRangeConstraintImpl::new);
        PutConstraint(StringMustNotBeNull.class, StringMustNotBeNullConfigFieldConstraintImpl::new);
    }

    private static <T extends Annotation> void PutConstraint(Class<T> annotation_class, ConfigFieldConstraintCreator<T> creator) { constraints.put(annotation_class, creator); }

    /**
     * Adds a new configuration field constraint creator of the specified {@code annotation_class}.
     * @param annotation_class The annotation class to register.
     * @param creator The function that accepts an annotation instance of type {@link T} and returns an instance of type {@link IConfigFieldConstraint}.
     * @param <T> The type of the annotation to register a constraint creator for.
     * @throws ArgumentNullException {@code annotation_class} and/or {@code creator} are {@code null}.
     * @throws InvalidOperationException The annotation type that the {@code annotation_class} backs does not specify the {@link ModConfigConstraint} annotation.
     */
    public static <T extends Annotation> void AddConfigFieldConstraintCreator(Class<T> annotation_class, ConfigFieldConstraintCreator<T> creator)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(creator, "creator");
        ArgumentNullException.ThrowIfNull(annotation_class, "annotation_class");
        if (annotation_class.getAnnotation(ModConfigConstraint.class) == null) {
            throw new InvalidOperationException("The specified annotation is not a mod config constraint.");
        } else {
            synchronized (constraints) { PutConstraint(annotation_class, creator); }
        }
    }

    /**
     * Invokes the creator for a given {@code annotation_instance} and returns the constructed {@link IConfigFieldConstraint} to the caller.
     * @param annotation_instance The {@link Annotation} to create the constraint from.
     * @return The {@linkplain IConfigFieldConstraint configuration field constraint} instance.
     * @throws ArgumentNullException {@code annotation_instance} is {@code null}.
     * @throws InvalidOperationException The {@link Class} of {@code annotation_instance} is not registered to the {@link ConfigFieldConstraintRegistry}.
     */
    public static IConfigFieldConstraint ConstructConfigFieldConstraint(Annotation annotation_instance)
            throws ArgumentNullException, InvalidOperationException
    {
        ArgumentNullException.ThrowIfNull(annotation_instance, "annotation_instance");
        ConfigFieldConstraintCreator<?> creator = constraints.get(annotation_instance.getClass());
        if (creator == null) {
            throw new InvalidOperationException("The specified annotation is not bound to a config field constraint instance!");
        } else {
            return ConstructInternal(creator, annotation_instance);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Annotation> IConfigFieldConstraint ConstructInternal(ConfigFieldConstraintCreator<T> creator, Annotation annotation) { return creator.function((T)annotation); }
}
