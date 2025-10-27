package com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis;

import com.github.mdcdi1315.DotNetLayer.System.Attribute;
import com.github.mdcdi1315.DotNetLayer.System.AttributeUsage;
import com.github.mdcdi1315.DotNetLayer.System.AttributeTargets;

import java.lang.annotation.Documented;

/**
 * Indicates that the specified method parameter expects a constant.
 */
@Attribute
@Documented
@AttributeUsage(AttributeTargets.Parameter)
public @interface ConstantExpected { }
