package com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis;

import com.github.mdcdi1315.DotNetLayer.System.Attribute;
import com.github.mdcdi1315.DotNetLayer.System.AttributeTargets;
import com.github.mdcdi1315.DotNetLayer.System.AttributeUsage;

import javax.annotation.Nullable;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import javax.annotation.meta.TypeQualifier;
import java.lang.annotation.RetentionPolicy;

/**
 * Specifies that when a method returns {@link MaybeNullWhen#ReturnValue},
 * the parameter may be null even if the corresponding type disallows it.
 */
@Nullable // For Java compatibility and argument/field nullness recognition
@Attribute
@Documented
@TypeQualifier
@Retention(RetentionPolicy.CLASS)
@AttributeUsage(value = AttributeTargets.Parameter , Inherited = false)
public @interface MaybeNullWhen
{
    /**
     * Gets the return value condition.
     * @return The return value condition. If the method returns this value, the associated parameter may be null.
     */
    boolean ReturnValue();
}