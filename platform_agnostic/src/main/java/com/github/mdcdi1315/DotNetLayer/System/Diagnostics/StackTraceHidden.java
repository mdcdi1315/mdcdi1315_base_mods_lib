package com.github.mdcdi1315.DotNetLayer.System.Diagnostics;

import com.github.mdcdi1315.DotNetLayer.System.Attribute;
import com.github.mdcdi1315.DotNetLayer.System.AttributeUsage;
import com.github.mdcdi1315.DotNetLayer.System.AttributeTargets;

import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import java.lang.annotation.RetentionPolicy;

/**
 * Types and Methods attributed with {@link StackTraceHidden} will be omitted from the stack trace text shown in StackTrace.ToString() and Exception.StackTrace
 */
@Attribute
@Documented
@Retention(RetentionPolicy.RUNTIME)
@AttributeUsage({AttributeTargets.Class, AttributeTargets.Constructor, AttributeTargets.Method})
public @interface StackTraceHidden { }
