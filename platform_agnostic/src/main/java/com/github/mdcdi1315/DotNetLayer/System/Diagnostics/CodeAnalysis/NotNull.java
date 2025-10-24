package com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis;

import com.github.mdcdi1315.DotNetLayer.System.Attribute;
import com.github.mdcdi1315.DotNetLayer.System.AttributeTargets;
import com.github.mdcdi1315.DotNetLayer.System.AttributeUsage;

import javax.annotation.Nonnull;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import javax.annotation.meta.TypeQualifier;
import java.lang.annotation.RetentionPolicy;

/**
 * Specifies that an output is not null even if the corresponding type allows it.
 * <p>Specifies that an input argument was not null when the call returns.</p>
 */
@Nonnull // For Java compatibility and argument/field nullness recognition
@Attribute
@Documented
@TypeQualifier
@Retention(RetentionPolicy.CLASS)
@AttributeUsage(value = { AttributeTargets.Parameter , AttributeTargets.Field , AttributeTargets.ReturnValue } , AllowMultiple = false)
public @interface NotNull {}
