package com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis;

import com.github.mdcdi1315.DotNetLayer.System.Attribute;
import com.github.mdcdi1315.DotNetLayer.System.AttributeUsage;
import com.github.mdcdi1315.DotNetLayer.System.AttributeTargets;

import java.lang.annotation.Documented;

/**
 * Indicates that the specified method requires dynamic access to code that is not referenced statically, for example, through {@link java.lang.reflect}.
 */
@Attribute
@Documented
@AttributeUsage({ AttributeTargets.Class, AttributeTargets.Constructor, AttributeTargets.Method })
public @interface RequiresUnreferencedCode
{
    /**
     * Gets a message that contains information about the usage of unreferenced code.
     */
    String Message();

    /**
     * Gets or sets an optional URL that contains more information about the method, why it requires unreferenced code, and what options a consumer has to deal with it.
     */
    @MaybeNull
    String Url() default "";
}
