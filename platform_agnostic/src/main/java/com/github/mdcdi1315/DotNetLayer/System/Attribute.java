package com.github.mdcdi1315.DotNetLayer.System;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Special annotation to indicate that the specified annotation does exactly correspond to a .NET attribute.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@AttributeUsage(value = AttributeTargets.All , AllowMultiple = true)
public @interface Attribute {
    /**
     * Defines the base type for this attribute. By default, it is this annotation class. <br />
     * This does not exist in actual .NET; It exists only and only for the needs of the translation layer.
     * @return The base class type for this attribute class.
     */
    Class<?> base_type() default Attribute.class;
}
