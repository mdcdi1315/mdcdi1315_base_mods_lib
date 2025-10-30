package com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis;

import com.github.mdcdi1315.DotNetLayer.System.Attribute;
import com.github.mdcdi1315.DotNetLayer.System.AttributeUsage;
import com.github.mdcdi1315.DotNetLayer.System.AttributeTargets;

import java.lang.annotation.Documented;

/**
 * Specifies that a method will never return under any circumstance. <br />
 * (That is, will always throw an exception)
 */
@Attribute
@Documented
@AttributeUsage(AttributeTargets.Method)
public @interface DoesNotReturn { }