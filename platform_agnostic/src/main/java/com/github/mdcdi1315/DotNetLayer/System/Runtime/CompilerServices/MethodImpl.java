package com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices;

import com.github.mdcdi1315.DotNetLayer.System.Attribute;
import com.github.mdcdi1315.DotNetLayer.System.AttributeUsage;
import com.github.mdcdi1315.DotNetLayer.System.AttributeTargets;

import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import java.lang.annotation.RetentionPolicy;

/**
 * Specifies the details of how a method is implemented. This class cannot be inherited.
 */
@Attribute
@Documented
@Retention(RetentionPolicy.CLASS)
@AttributeUsage(value = {AttributeTargets.Method, AttributeTargets.Constructor}, Inherited = false)
public @interface MethodImpl
{
    /**
     * Gets the {@link MethodImplOptions} value describing the attributed method.
     * @return The {@link MethodImplOptions} value describing the attributed method.
     */
    MethodImplOptions[] GetValue();
}
