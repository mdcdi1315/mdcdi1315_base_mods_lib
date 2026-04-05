package com.github.mdcdi1315.DotNetLayer.System.Reflection;

import com.github.mdcdi1315.DotNetLayer.System.Attribute;
import com.github.mdcdi1315.DotNetLayer.System.AttributeUsage;
import com.github.mdcdi1315.DotNetLayer.System.AttributeTargets;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import java.lang.annotation.RetentionPolicy;

/**
 * Defines the member of a type that is the default member used by InvokeMember(String, BindingFlags, Binder, Object, Object[], ParameterModifier[], CultureInfo, String[]).
 */
@Attribute
@Documented
@Retention(RetentionPolicy.CLASS)
@AttributeUsage({AttributeTargets.Class, AttributeTargets.Interface, AttributeTargets.AnnotationInterface})
public @interface DefaultMember
{
    /**
     * Gets the name from the attribute.
     * @return A string representing the member name.
     */
    @NotNull
    String MemberName();
}
