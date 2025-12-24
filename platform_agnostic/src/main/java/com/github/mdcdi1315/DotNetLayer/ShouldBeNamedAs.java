package com.github.mdcdi1315.DotNetLayer;

import java.lang.annotation.*;

/**
 * Defined to classes and/or method names to indicate which is the actual name of that class/method in .NET . <br />
 * If deemed necessary for classes, the value of the annotation can also take the full namespaced name of the class as is defined in .NET .
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ShouldBeNamedAs
{
    /**
     * Gets the name of the class or the method that corresponds to .NET .
     * @return The name of the class or method as named in the .NET Runtime.
     */
    String value();
}
