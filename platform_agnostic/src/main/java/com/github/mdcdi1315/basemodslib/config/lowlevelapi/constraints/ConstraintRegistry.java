package com.github.mdcdi1315.basemodslib.config.lowlevelapi.constraints;

import com.github.mdcdi1315.DotNetLayer.System.StringUtils;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.config.lowlevelapi.IConfigFieldConstraint;

import java.lang.annotation.Annotation;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides the way to map an annotation interface to a {@link IConfigFieldConstraint} instance.
 */
public final class ConstraintRegistry
{
    private ConstraintRegistry() {}

    private static final ConcurrentHashMap<Class<? extends Annotation>, IConstraintCreator<?>> constraint_creators;

    private static StringMustNotBeNullConstraint GetInstance_StringMustNotBeNullConstraint(Annotation a) { return StringMustNotBeNullConstraint.INSTANCE; }

    static {
        constraint_creators = new ConcurrentHashMap<>(10);

        constraint_creators.put(
                StringMustNotBeNull.class,
                (IConstraintCreator<String>)ConstraintRegistry::GetInstance_StringMustNotBeNullConstraint
        );


    }

    public static <T> void RegisterConstraint(Class<? extends Annotation> annotation_class, IConstraintCreator<T> creator)
            throws ArgumentNullException, InvalidOperationException
    {
        ArgumentNullException.ThrowIfNull(creator, "creator");
        ArgumentNullException.ThrowIfNull(annotation_class, "annotation_class");
        if (constraint_creators.putIfAbsent(annotation_class, creator) != null) {
            throw new InvalidOperationException(StringUtils.Format("Attempted to modify the config field creator for class type {0}", annotation_class.getName()));
        }
    }

    @MaybeNull
    public static <T> IConfigFieldConstraint<T> ConstructConstraint(Annotation a)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(a, "a");
        IConstraintCreator<?> c = constraint_creators.get(a.annotationType());
        if (c == null) {
            return null;
        } else {
            return ((IConstraintCreator<T>)c).ConstructConstraint(a);
        }
    }
}
