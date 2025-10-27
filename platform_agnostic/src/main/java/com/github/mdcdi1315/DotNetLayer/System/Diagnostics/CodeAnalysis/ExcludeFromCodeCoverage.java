package com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis;

import com.github.mdcdi1315.DotNetLayer.System.Attribute;
import com.github.mdcdi1315.DotNetLayer.System.AttributeUsage;
import com.github.mdcdi1315.DotNetLayer.System.AttributeTargets;

import java.lang.annotation.Documented;

/**
 * Specifies that the attributed code should be excluded from code coverage information.
 */
@Attribute
@Documented
@AttributeUsage({ AttributeTargets.Class, AttributeTargets.Constructor, AttributeTargets.Method })
public @interface ExcludeFromCodeCoverage
{
    /**
     * Gets or sets the justification for excluding the member from code coverage.
     */
    String Justification();
}
