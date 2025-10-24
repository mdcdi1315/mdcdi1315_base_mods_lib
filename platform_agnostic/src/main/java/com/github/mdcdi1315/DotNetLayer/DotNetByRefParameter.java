package com.github.mdcdi1315.DotNetLayer;

import java.lang.annotation.*;

/**
 * Specified to a '.NET special type parameter and method parameter' to indicate it's original intent and purpose.
 * <p>
 *     Note that, for type parameters, only the {@link ByRefParameterType#IN} and {@link ByRefParameterType#OUT} semantics are valid.
 * </p>
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(value = {ElementType.TYPE_PARAMETER , ElementType.PARAMETER})
public @interface DotNetByRefParameter {

    ByRefParameterType[] value();

}
