package com.github.mdcdi1315.DotNetLayer;

import java.lang.annotation.*;

/**
 * Applied to a method signature to indicate that it should be normally an overload of another class or interface.
 * The value specifies the name of the method that should have anyway been overloaded.
 */
@Inherited
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface OverloadedMethod
{
    /**
     * Gets the actual name of the method as declared in .NET.
     * @return The actual method's name.
     */
    String value();
}
