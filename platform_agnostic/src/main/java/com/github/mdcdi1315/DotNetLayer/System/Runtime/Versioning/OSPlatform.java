package com.github.mdcdi1315.DotNetLayer.System.Runtime.Versioning;

import com.github.mdcdi1315.DotNetLayer.System.Attribute;

import java.lang.annotation.Documented;

/**
 * Base type for all platform-specific API attributes.
 */
@Attribute
@Documented
public @interface OSPlatform
{
    /**
     * Gets the name and optional version of the platform that the attribute applies to.
     * @return The applicable platform name and optional version.
     */
    String PlatformName() default "";
}
