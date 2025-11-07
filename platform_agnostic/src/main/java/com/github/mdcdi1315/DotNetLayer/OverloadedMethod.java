package com.github.mdcdi1315.DotNetLayer;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * Applied to a method signature to indicate that it should be normally an overload of another class or interface.
 * The value specifies the name of the method that should have anyway been overloaded.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface OverloadedMethod {
    String value();
}
