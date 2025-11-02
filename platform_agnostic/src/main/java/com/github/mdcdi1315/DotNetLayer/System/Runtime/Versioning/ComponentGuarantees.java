package com.github.mdcdi1315.DotNetLayer.System.Runtime.Versioning;

import com.github.mdcdi1315.DotNetLayer.System.Attribute;
import com.github.mdcdi1315.DotNetLayer.System.AttributeUsage;
import com.github.mdcdi1315.DotNetLayer.System.AttributeTargets;

import java.lang.annotation.Documented;

/**
 * Defines the compatibility guarantee of a component, type, or type member that may span multiple versions.
 */
@Attribute
@Documented
@AttributeUsage({
        AttributeTargets.Class,
        AttributeTargets.Constructor,
        AttributeTargets.Enum,
        AttributeTargets.Field,
        AttributeTargets.Interface,
        AttributeTargets.Method
})
public @interface ComponentGuarantees
{
    /**
     * Gets a value that indicates the guaranteed level of compatibility of a library, type, or type member that spans multiple versions.
     * @return One of the enumeration values that specifies the level of compatibility that is guaranteed across multiple versions.
     */
    ComponentGuaranteesOptions[] Guarantees();
}
