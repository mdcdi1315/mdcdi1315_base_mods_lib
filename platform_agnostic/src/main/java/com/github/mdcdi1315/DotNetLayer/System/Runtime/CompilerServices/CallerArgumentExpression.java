package com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices;

import com.github.mdcdi1315.DotNetLayer.System.Attribute;
import com.github.mdcdi1315.DotNetLayer.System.AttributeTargets;
import com.github.mdcdi1315.DotNetLayer.System.AttributeUsage;

import java.lang.annotation.Documented;

/**
 * Indicates that a parameter captures the expression passed for another parameter as a string.
 */
@Attribute
@Documented
@AttributeUsage(value = AttributeTargets.Parameter , AllowMultiple = false)
public @interface CallerArgumentExpression
{
    /**
     * Gets the name of the parameter whose expression should be captured as a string.
     * @return The name of the parameter whose expression should be captured.
     */
    public String ParameterName();
}
