package com.github.mdcdi1315.DotNetLayer;

import java.lang.annotation.*;

/**
 * Applied to a method signature to indicate that it should be normally an overload of another class or interface.
 * The value specifies the name of the method that should have anyway been overloaded.
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface OverloadedMethod {
    String value();
}
