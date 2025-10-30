package com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis;

import com.github.mdcdi1315.DotNetLayer.System.Attribute;
import com.github.mdcdi1315.DotNetLayer.System.AttributeUsage;
import com.github.mdcdi1315.DotNetLayer.System.AttributeTargets;

import java.lang.annotation.Documented;

/**
 * Specifies that the method will not return if the associated {@link Boolean} parameter is passed the specified value.
 */
@Attribute
@Documented
@AttributeUsage(AttributeTargets.Parameter)
public @interface DoesNotReturnIf
{
    /**
     * Gets the condition parameter value.
     * @return The condition parameter value.
     * Code after the method is considered unreachable by diagnostics if the argument to the associated parameter matches this value.
     */
    boolean ParameterValue();
}
