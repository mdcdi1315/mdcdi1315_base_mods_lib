package com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis;

import com.github.mdcdi1315.DotNetLayer.System.Attribute;
import com.github.mdcdi1315.DotNetLayer.System.AttributeUsage;
import com.github.mdcdi1315.DotNetLayer.System.AttributeTargets;

import org.jspecify.annotations.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import java.lang.annotation.RetentionPolicy;

/**
 * Specifies that null is disallowed as an input even if the corresponding type allows it.
 */
@Nullable
@Attribute
@Documented
@Retention(RetentionPolicy.CLASS)
@SuppressWarnings("NullableProblems")
@AttributeUsage(value = {AttributeTargets.Parameter , AttributeTargets.Field} , Inherited = false)
public @interface DisallowNull { }