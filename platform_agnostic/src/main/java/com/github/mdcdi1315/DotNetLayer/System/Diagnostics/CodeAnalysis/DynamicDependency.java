package com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis;

import com.github.mdcdi1315.DotNetLayer.System.Attribute;
import com.github.mdcdi1315.DotNetLayer.System.AttributeUsage;
import com.github.mdcdi1315.DotNetLayer.System.AttributeTargets;

import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import java.lang.annotation.RetentionPolicy;

/**
 * States a dependency that one member has on another.
 */
@Attribute
@Documented
@Retention(RetentionPolicy.CLASS)
@AttributeUsage(value = {AttributeTargets.Constructor, AttributeTargets.Field, AttributeTargets.Method}, AllowMultiple = true, Inherited = false)
public @interface DynamicDependency
{
    String TypeName() default "";

    String AssemblyName() default "";

    Class<?> Type() default void.class;

    String MemberSignature() default "";

    DynamicallyAccessedMemberTypes[] MemberTypes() default {};
}
