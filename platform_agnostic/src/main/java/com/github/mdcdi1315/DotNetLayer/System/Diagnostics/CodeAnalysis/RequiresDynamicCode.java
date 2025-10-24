package com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis;

import com.github.mdcdi1315.DotNetLayer.System.Attribute;
import com.github.mdcdi1315.DotNetLayer.System.AttributeUsage;
import com.github.mdcdi1315.DotNetLayer.System.AttributeTargets;

import java.lang.annotation.Documented;

/**
 * Indicates that the specified method requires the ability to generate new code at runtime, for example through {@link java.lang.reflect}.
 */
@Attribute
@Documented
@AttributeUsage(value = {AttributeTargets.Class , AttributeTargets.Method , AttributeTargets.Constructor} , Inherited = false)
public @interface RequiresDynamicCode
{
    /**
     * Gets a message that contains information about the usage of dynamic code.
     * @return A message string that contains information about the usage of dynamic code.
     */
    String Message();

    /**
     * Gets or sets an optional URL that contains more information about the method,
     * why it requires dynamic code, and what options a consumer has to deal with it.
     */
    String Url() default "";
}
