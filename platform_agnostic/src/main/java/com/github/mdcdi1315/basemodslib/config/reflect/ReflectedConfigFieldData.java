package com.github.mdcdi1315.basemodslib.config.reflect;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.config.ListField;
import com.github.mdcdi1315.basemodslib.config.IModConfig;
import com.github.mdcdi1315.basemodslib.config.ConfigField;
import com.github.mdcdi1315.basemodslib.config.reflect.constraints.*;
import com.github.mdcdi1315.basemodslib.utils.ReflectionUtils;
import com.github.mdcdi1315.basemodslib.utils.collections.ArrayEnumerator;
import com.github.mdcdi1315.basemodslib.utils.collections.SingletonEnumeratorEnumerable;

import java.util.List;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.annotation.Annotation;

/**
 * Provides reflected field data for a configuration field.
 */
public final class ReflectedConfigFieldData
{
    private final Field reflected_field;
    private final ConfigField field_data;

    /**
     * Initializes a new instance of the {@link ReflectedConfigFieldData} class.
     * @param field The reflected configuration field to initialize the {@link ReflectedConfigFieldData} class from.
     * @throws ArgumentNullException {@code field} is {@code null}.
     * @throws ArgumentException {@code field} is not annotated with the {@code ConfigField} annotation. <br /> <br />
     * -or- <br /> <br />
     * {@code field} is {@code final}. <br /> <br />
     * -or- <br /> <br />
     * {@code field} is not {@code public}.
     */
    public ReflectedConfigFieldData(Field field)
            throws ArgumentNullException, ArgumentException
    {
        ArgumentNullException.ThrowIfNull(reflected_field = field, "field");
        if (Modifier.isFinal(reflected_field.getModifiers()) || Modifier.isStatic(reflected_field.getModifiers())) {
            throw new ArgumentException("field", "The specified field instance cannot be assigned to.");
        } else if (!Modifier.isPublic(reflected_field.getModifiers())) {
            throw new ArgumentException("field", "The specified field instance is not publicly accessible.");
        } else if ((field_data = reflected_field.getAnnotation(ConfigField.class)) == null) {
            throw new ArgumentException("field", "The specified field instance does not provide the ConfigField annotation.");
        } else {
            Class<?> inspected_class = GetFieldClass();
            IEnumerator<IConfigFieldConstraint> constraints = GetConstraints().GetEnumerator();
            try {
                IConfigFieldConstraint constraint;
                while (constraints.MoveNext())
                {
                    constraint = constraints.getCurrent();
                    Class<?> constraint_class = constraint.AppliesTo();
                    if (
                            (
                                    (constraint_class.isLocalClass() || constraint_class.isMemberClass() || constraint_class.isAnonymousClass()) && (!ReflectionUtils.ExtendsClass(inspected_class, constraint_class))
                            ) || (
                                    constraint_class.isInterface() && (!ReflectionUtils.ImplementsInterface(inspected_class, constraint_class))
                            )
                    ) {
                        throw new InvalidFieldConstraintDefinitionException(
                                constraint,
                                inspected_class
                        );
                    }
                }
            } finally {
                constraints.Dispose();
            }
        }
    }

    /**
     * Gets the name under which the current field must be written to a configuration storage.
     * @return The name that will be utilized during serialization.
     */
    @NotNull
    public String GetSerializedFieldName()
    {
        String name = field_data.field_name();
        if (StringUtils.IsNullOrEmpty(name)) { name = reflected_field.getName(); }
        return name;
    }

    /**
     * Gets the name under which the current field is declared in the current mod config instance.
     * @return The field name of the currently reflected mod config field.
     */
    @NotNull
    public String GetFieldName() { return reflected_field.getName(); }

    /**
     * Gets the {@link Class} object that the current field backs at run-time.
     * @return The {@link Class} that the current {@link ReflectedConfigFieldData} can save and retrieve.
     */
    @NotNull
    public Class<?> GetFieldClass() { return reflected_field.getType(); }

    /**
     * Gets the comment of this configuration field.
     * @return The comment of the currently reflected mod config field.
     */
    @NotNull
    public String GetComment()
    {
        String cmt = field_data.comment();
        return StringUtils.IsNullOrEmpty(cmt) ? StringUtils.Empty : cmt;
    }

    @MaybeNull
    public <TA extends Annotation> TA GetDeclaredAnnotation(Class<TA> annotation_class) { return reflected_field.getAnnotation(annotation_class); }

    @MaybeNull
    public Object GetValue(IModConfig config_instance)
            throws UnauthorizedAccessException
    {
        try {
            return reflected_field.get(config_instance);
        } catch (IllegalAccessException e) {
            throw new UnauthorizedAccessException(StringUtils.Format(
                    "Could not access the current config field with name {0}!\nException data: {1}",
                    e,
                    reflected_field.getName()
            ));
        }
    }

    public void SetValue(IModConfig config_instance, Object new_value)
            throws UnauthorizedAccessException
    {
        try {
            reflected_field.set(config_instance, new_value);
        } catch (IllegalAccessException e) {
            throw new UnauthorizedAccessException(StringUtils.Format(
                    "Could not access the current config field with name {0}!\nException data: {1}",
                    e,
                    reflected_field.getName()
            ));
        }
    }

    @MaybeNull
    public Class<?> GetListFieldClass()
            throws InvalidFieldLayoutException
    {
        if (reflected_field.getType() == List.class) {
            ListField lfd = reflected_field.getAnnotation(ListField.class);
            if (lfd == null) {
                throw new InvalidFieldLayoutException(StringUtils.Format("The field named as {0} cannot be decoded because a ListField annotation is missing.", reflected_field.getName()));
            } else {
                Class<?> e_class = lfd.ElementClass();
                if (e_class == null) {
                    throw new InvalidFieldLayoutException(StringUtils.Format("Could not construct element class of the list because an invalid value was specified."));
                } else {
                    return e_class;
                }
            }
        } else {
            return null;
        }
    }

    private static final class ConstraintEnumerator
        implements IEnumerator<IConfigFieldConstraint>
    {
        private IConfigFieldConstraint current;
        private final ArrayEnumerator<Annotation> base_enumerator;

        public ConstraintEnumerator(Annotation[] annotations) { base_enumerator = ArrayEnumerator.Of(annotations); }

        @Override
        public IConfigFieldConstraint getCurrent() { return current; }

        @Override
        public boolean MoveNext()
                throws InvalidOperationException
        {
            Annotation a_c;
            while (base_enumerator.MoveNext())
            {
                if ((a_c = base_enumerator.getCurrent()).getClass().getAnnotation(ModConfigConstraint.class) != null)
                {
                    current = ConfigFieldConstraintRegistry.ConstructConfigFieldConstraint(a_c);
                    return true;
                }
            }
            return false;
        }

        @Override
        public void Reset() throws InvalidOperationException { base_enumerator.Reset(); }

        @Override
        public void Dispose() { base_enumerator.Dispose(); }
    }

    @NotNull
    public IEnumerable<IConfigFieldConstraint> GetConstraints()
    {
        return new SingletonEnumeratorEnumerable<>(
                new ConstraintEnumerator(reflected_field.getAnnotations())
        );
    }
}
