package com.github.mdcdi1315.DotNetLayer.System.Runtime.Versioning;

import com.github.mdcdi1315.DotNetLayer.System.Attribute;
import com.github.mdcdi1315.DotNetLayer.System.AttributeUsage;
import com.github.mdcdi1315.DotNetLayer.System.AttributeTargets;

import java.lang.annotation.Documented;

/**
 * Annotates the custom guard field, property or method with an unsupported platform name and optional version.
 * Multiple attributes can be applied to indicate guard for multiple unsupported platforms.
 */
@Documented
@Attribute(base_type = OSPlatform.class)
@AttributeUsage(value = {AttributeTargets.Field, AttributeTargets.Method}, AllowMultiple = true)
public @interface UnsupportedOSPlatformGuard
{
    /**
     * Gets the name and optional version of the platform that the attribute applies to.
     * @return The applicable platform name and optional version.
     */
    String PlatformName() default "";
}
