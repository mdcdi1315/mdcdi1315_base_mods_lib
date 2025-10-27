package com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis;

import com.github.mdcdi1315.DotNetLayer.System.Attribute;
import com.github.mdcdi1315.DotNetLayer.System.AttributeUsage;
import com.github.mdcdi1315.DotNetLayer.System.AttributeTargets;

import java.lang.annotation.Documented;

/**
 * Indicates that certain members on a specified Type are accessed dynamically, for example, through System.Reflection.
 */
@Attribute
@Documented
@AttributeUsage(value = {
        AttributeTargets.Class ,
        AttributeTargets.Field,
        AttributeTargets.GenericParameter,
        AttributeTargets.Interface,
        AttributeTargets.Method,
        AttributeTargets.Parameter,
        AttributeTargets.ReturnValue
}, Inherited = false)
public @interface DynamicallyAccessedMembers
{
    /**
     * Gets the {@link DynamicallyAccessedMemberTypes} that specifies the type of dynamically accessed members.
     * @return
     */
    DynamicallyAccessedMemberTypes MemberTypes();
}
