package com.github.mdcdi1315.DotNetLayer.System.Runtime.Versioning;

import com.github.mdcdi1315.DotNetLayer.System.Attribute;
import com.github.mdcdi1315.DotNetLayer.System.AttributeUsage;
import com.github.mdcdi1315.DotNetLayer.System.AttributeTargets;

import java.lang.annotation.Documented;

/**
 * Annotates a custom guard field, property or method with a supported platform name and optional version.
 * Multiple attributes can be applied to indicate guard for multiple supported platforms.
 */
@Documented
@Attribute(base_type = OSPlatform.class)
@AttributeUsage(value = {AttributeTargets.Field, AttributeTargets.Method}, AllowMultiple = true, Inherited = false)
public @interface SupportedOSPlatformGuard
{
    /**
     * Gets the name and optional version of the platform that the attribute applies to.
     * @return The applicable platform name and optional version.
     */
    String PlatformName() default "";
}
