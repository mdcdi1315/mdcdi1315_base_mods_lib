package com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis;

import com.github.mdcdi1315.DotNetLayer.System.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import org.jspecify.annotations.Nullable;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation specified to a parameter or method return value to indicate that a value may be null even if it's type disallows it.
 */
@Nullable
@Attribute
@Documented
@Retention(RetentionPolicy.CLASS)
@SuppressWarnings("NullableProblems")
@AttributeUsage(value = { AttributeTargets.Parameter , AttributeTargets.ReturnValue }, AllowMultiple = false)
public @interface MaybeNull {}