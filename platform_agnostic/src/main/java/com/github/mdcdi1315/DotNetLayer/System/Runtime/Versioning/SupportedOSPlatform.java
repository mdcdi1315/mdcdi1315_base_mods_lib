package com.github.mdcdi1315.DotNetLayer.System.Runtime.Versioning;

import com.github.mdcdi1315.DotNetLayer.System.Attribute;
import com.github.mdcdi1315.DotNetLayer.System.AttributeUsage;
import com.github.mdcdi1315.DotNetLayer.System.AttributeTargets;

import java.lang.annotation.Documented;

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
public @interface SupportedOSPlatform
{
    /**
     * Gets the name and optional version of the platform that the attribute applies to.
     * @return The applicable platform name and optional version.
     */
    String PlatformName() default "";
}
