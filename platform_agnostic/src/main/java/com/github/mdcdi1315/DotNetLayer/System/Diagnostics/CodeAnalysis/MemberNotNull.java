package com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis;

import com.github.mdcdi1315.DotNetLayer.System.Attribute;
import com.github.mdcdi1315.DotNetLayer.System.AttributeUsage;
import com.github.mdcdi1315.DotNetLayer.System.AttributeTargets;

import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import java.lang.annotation.RetentionPolicy;

/**
 * Specifies that the method or property will ensure that the listed field and property members have values that aren't {@code null}.
 */
@Attribute
@Documented
@Retention(RetentionPolicy.CLASS)
@AttributeUsage(value = {AttributeTargets.Method}, AllowMultiple = true, Inherited = false)
public @interface MemberNotNull
{
    /**
     * Gets field or property member names.
     */
    String[] Members() default {};
}
