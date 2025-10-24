package com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis;

import com.github.mdcdi1315.DotNetLayer.System.*;

import javax.annotation.Nullable;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import javax.annotation.meta.TypeQualifier;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation specified to a parameter or method return value to indicate that a value may be null even if it's type disallows it.
 */
@Nullable // For Java compatibility and argument/field nullness recognition
@Attribute
@Documented
@TypeQualifier
@Retention(RetentionPolicy.CLASS)
@AttributeUsage(value = { AttributeTargets.Parameter , AttributeTargets.ReturnValue }, AllowMultiple = false)
public @interface MaybeNull {}