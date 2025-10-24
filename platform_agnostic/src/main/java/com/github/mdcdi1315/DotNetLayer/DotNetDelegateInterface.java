package com.github.mdcdi1315.DotNetLayer;

import java.lang.annotation.*;

/**
 * Applied to any ported delegate from .NET into a functional Java interface.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface DotNetDelegateInterface
{
    /**
     * The actual type name exposed by .NET.
     * @return The actual type name expressed with it's original class name only.
     */
    String ActualTypeName() default "";
}
