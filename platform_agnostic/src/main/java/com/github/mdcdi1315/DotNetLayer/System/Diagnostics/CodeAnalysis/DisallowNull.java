package com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis;

import com.github.mdcdi1315.DotNetLayer.System.Attribute;
import com.github.mdcdi1315.DotNetLayer.System.AttributeTargets;
import com.github.mdcdi1315.DotNetLayer.System.AttributeUsage;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifier;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Specifies that null is disallowed as an input even if the corresponding type allows it.
 */
@Nonnull // For Java compatibility and argument/field nullness recognition
@Attribute
@Documented
@TypeQualifier
@Retention(RetentionPolicy.CLASS)
@AttributeUsage(value = {AttributeTargets.Parameter , AttributeTargets.Field} , Inherited = false)
public @interface DisallowNull {}

