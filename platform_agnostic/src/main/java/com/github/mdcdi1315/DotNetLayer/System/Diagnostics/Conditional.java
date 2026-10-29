package com.github.mdcdi1315.DotNetLayer.System.Diagnostics;

import com.github.mdcdi1315.DotNetLayer.System.Attribute;
import com.github.mdcdi1315.DotNetLayer.System.AttributeTargets;
import com.github.mdcdi1315.DotNetLayer.System.AttributeUsage;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates to compilers that a method call or attribute should be ignored unless a specified conditional compilation symbol is defined.
 */
@Attribute
@Documented
@Retention(RetentionPolicy.CLASS)
@Repeatable(Conditional.RepeatableDef.class)
@AttributeUsage(value = {AttributeTargets.Class, AttributeTargets.Method}, AllowMultiple = true)
public @interface Conditional
{
    /**
     * Gets the conditional compilation symbol that is associated with the {@link Conditional} attribute.
     * @return A string that specifies the case-sensitive conditional compilation symbol that is associated with the {@link Conditional} attribute.
     */
    String ConditionString();

    @Documented
    @interface RepeatableDef { Conditional[] value(); }
}
