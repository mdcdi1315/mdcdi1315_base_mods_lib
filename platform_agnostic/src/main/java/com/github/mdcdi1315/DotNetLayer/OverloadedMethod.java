package com.github.mdcdi1315.DotNetLayer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Applied to a method signature to indicate that it should be normally an overload of another class or interface.
 * The value specifies the name of the method that should have anyway been overloaded.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface OverloadedMethod {
    String value();
}
