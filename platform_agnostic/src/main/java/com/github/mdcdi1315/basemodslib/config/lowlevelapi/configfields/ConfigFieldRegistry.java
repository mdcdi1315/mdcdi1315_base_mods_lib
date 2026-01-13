package com.github.mdcdi1315.basemodslib.config.lowlevelapi.configfields;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.config.ListField;
import com.github.mdcdi1315.basemodslib.config.ConfigList;
import com.github.mdcdi1315.basemodslib.config.IModConfig;
import com.github.mdcdi1315.basemodslib.config.ConfigField;
import com.github.mdcdi1315.basemodslib.utils.EmptyIterable;
import com.github.mdcdi1315.basemodslib.config.lowlevelapi.IConfigField;
import com.github.mdcdi1315.basemodslib.config.lowlevelapi.IConfigFieldConstraint;
import com.github.mdcdi1315.basemodslib.config.lowlevelapi.constraints.ConstraintRegistry;

import com.google.common.collect.ImmutableList;

import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Field;
import java.lang.annotation.Annotation;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides a backing storage for storing configuration field creators
 * and accessing them at run-time.
 * @since 1.0.15
 */
public final class ConfigFieldRegistry
{
    private ConfigFieldRegistry() {}

    private static final ConcurrentHashMap<Class<?>, IConfigFieldCreator<?>> creators;

    static {
        creators = new ConcurrentHashMap<>(16);

        creators.put(
                String.class,
                (IConfigFieldCreator<String>)StringConfigField::new
        );
        creators.put(
                ResourceLocation.class,
                (IConfigFieldCreator<ResourceLocation>)ResourceLocationConfigField::new
        );
        creators.put(
                Boolean.class,
                (IConfigFieldCreator<Boolean>)BooleanConfigField::new
        );
        creators.put(
                boolean.class,
                (IConfigFieldCreator<Boolean>)BooleanConfigField::new
        );

        creators.put(
                Byte.class,
                (IConfigFieldCreator<Byte>)ByteConfigField::new
        );
        creators.put(
                byte.class,
                (IConfigFieldCreator<Byte>)ByteConfigField::new
        );
        creators.put(
                Short.class,
                (IConfigFieldCreator<Short>)ShortConfigField::new
        );
        creators.put(
                short.class,
                (IConfigFieldCreator<Short>)ShortConfigField::new
        );
        creators.put(
                Integer.class,
                (IConfigFieldCreator<Integer>)IntConfigField::new
        );
        creators.put(
                int.class,
                (IConfigFieldCreator<Integer>)IntConfigField::new
        );
        creators.put(
                Long.class,
                (IConfigFieldCreator<Long>)LongConfigField::new
        );
        creators.put(
                long.class,
                (IConfigFieldCreator<Long>)LongConfigField::new
        );

        creators.put(
                Float.class,
                (IConfigFieldCreator<Float>)FloatConfigField::new
        );
        creators.put(
                float.class,
                (IConfigFieldCreator<Float>)FloatConfigField::new
        );
        creators.put(
                Double.class,
                (IConfigFieldCreator<Double>)DoubleConfigField::new
        );
        creators.put(
                double.class,
                (IConfigFieldCreator<Double>)DoubleConfigField::new
        );
    }

    public static <T> void RegisterConfigFieldCreator(Class<T> field_class, IConfigFieldCreator<T> creator)
            throws ArgumentNullException, InvalidOperationException
    {
        ArgumentNullException.ThrowIfNull(creator, "creator");
        ArgumentNullException.ThrowIfNull(field_class, "field_class");
        if (creators.putIfAbsent(field_class, creator) != null) {
            throw new InvalidOperationException(StringUtils.Format("Attempted to modify the config field creator for class type {0}", field_class.getName()));
        }
    }

    @MaybeNull
    public static IConfigField<?> CreateConfigField(IModConfig config, Field f)
            throws ArgumentException, NotSupportedException
    {
        ArgumentNullException.ThrowIfNull(f, "f");
        ArgumentNullException.ThrowIfNull(config, "config");
        // The below is done on purpose so that the type T can be resolved out and any generics depending on it to properly work.
        return CreateConfigField_Indirection(config, f, f.getType());
    }

    private static <T> IConfigField<?> CreateConfigField_Indirection(IModConfig config, Field f, Class<T> field_type)
    {
        IConfigFieldCreator<?> c = creators.get(field_type);
        if (c == null && field_type != ConfigList.class) {
            throw new NotSupportedException(StringUtils.Format("Field of type {0} is not supported for configuration data.", f.getType().getName()));
        } else {
            ConfigField cfd = f.getAnnotation(ConfigField.class);
            if (cfd == null) { return null; }
            String name = cfd.field_name().isEmpty() ? f.getName() : cfd.field_name();
            try {
                T value = (T)f.get(config);
                if (value instanceof IModConfig md) {
                    return new NestedConfigField(name, cfd.comment(), md, new EmptyIterable<>());
                } else if (value instanceof ConfigList clt) {
                    ListField lfd = f.getAnnotation(ListField.class);
                    if (lfd == null) { return null; }
                    return new ListConfigField(name, cfd.comment(), clt, lfd.ElementClass(), new EmptyIterable<>());
                } else {
                    IConfigFieldConstraint<T> ct;
                    ImmutableList.Builder<IConfigFieldConstraint<T>> b = ImmutableList.builder();
                    for (Annotation a : f.getAnnotations()) {
                        ct = ConstraintRegistry.ConstructConstraint(a);
                        if (ct != null) { b.add(ct); }
                    }
                    return ((IConfigFieldCreator<T>) c).CreateConfigField(name, cfd.comment(), value, b.build());
                }
            } catch (IllegalAccessException e) {
                throw new ArgumentException("Cannot read the given field.");
            }
        }
    }
}
