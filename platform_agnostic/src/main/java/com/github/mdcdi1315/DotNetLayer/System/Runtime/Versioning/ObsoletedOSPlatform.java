package com.github.mdcdi1315.DotNetLayer.System.Runtime.Versioning;

import com.github.mdcdi1315.DotNetLayer.System.Attribute;
import com.github.mdcdi1315.DotNetLayer.System.AttributeUsage;
import com.github.mdcdi1315.DotNetLayer.System.AttributeTargets;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import java.lang.annotation.Documented;

/**
 * Marks APIs that were obsoleted in a given operating system version.
 */
@Documented
@Attribute(base_type = OSPlatform.class)
@AttributeUsage(value = {
        AttributeTargets.Class,
        AttributeTargets.Constructor,
        AttributeTargets.Enum,
        AttributeTargets.Field,
        AttributeTargets.Interface,
        AttributeTargets.Method,
}, AllowMultiple = true , Inherited = false)
public @interface ObsoletedOSPlatform
{
    /**
     * Gets the optional additional message provided for the attribute.
     * @return A string that represents the message, or null if there is none.
     */
    @MaybeNull
    String Message() default "";

    /**
     * Gets or sets the URL for corresponding documentation.
     * @return The string that represents a URL to corresponding documentation.
     */
    String Url() default "";

    /**
     * Gets the name and optional version of the platform that the attribute applies to.
     * @return The applicable platform name and optional version.
     */
    String PlatformName() default "";
}
