package com.github.mdcdi1315.DotNetLayer;

import java.lang.annotation.*;

/**
 * Specified to a '.NET special type parameter and method parameter' to indicate it's original intent and purpose. <br />
 * Note that, for type parameters, only the {@link ByRefParameterType#IN} and {@link ByRefParameterType#OUT} semantics are valid.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE_PARAMETER , ElementType.PARAMETER})
public @interface DotNetByRefParameter
{
    /**
     * Gets the parameter modifiers.
     * @return The modifiers defined for the current parameter.
     */
    ByRefParameterType[] value();
}
