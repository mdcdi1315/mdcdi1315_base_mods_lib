package com.github.mdcdi1315.DotNetLayer.System;

import java.lang.annotation.Documented;

/**
 * Indicates whether a program element is compliant with the Common Language Specification (CLS). <br />
 * This class cannot be inherited.
 */
@Attribute
@Documented
@AttributeUsage(value = AttributeTargets.All, AllowMultiple = false , Inherited = true)
public @interface CLSCompliant
{
    /**
     * Gets the Boolean value indicating whether the indicated program element is CLS-compliant.
     * @return {@code true} if the program element is CLS-compliant; otherwise, {@code false}.
     */
    boolean IsCompliant();
}
