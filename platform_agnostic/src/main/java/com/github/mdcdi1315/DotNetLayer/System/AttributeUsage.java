package com.github.mdcdi1315.DotNetLayer.System;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Specifies where a new {@code Attribute} instance can be applied to.
 */
@Attribute
@Documented
@Retention(RetentionPolicy.SOURCE)
@AttributeUsage(value = AttributeTargets.All)
public @interface AttributeUsage
{
    /**
     * Specifies where this attribute can be applied to.
     * @return The code elements where the attributed attribute can be applied to.
     */
    AttributeTargets[] value();

    /**
     * Specifies whether multiple instances of the attribute can coexist.
     * @return A value whether multiple instances of the attribute can coexist.
     */
    boolean AllowMultiple() default false;

    /**
     * In .NET , attributes can be inherited.
     * Because Java's annotations cannot do that, using this does not have any effect.
     * @return A value whether this attribute can be inherited.
     */
    boolean Inherited() default false;
}
