package com.github.mdcdi1315.DotNetLayer.System.Diagnostics.Contracts;

import com.github.mdcdi1315.DotNetLayer.System.Attribute;
import com.github.mdcdi1315.DotNetLayer.System.AttributeUsage;
import com.github.mdcdi1315.DotNetLayer.System.AttributeTargets;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.Conditional;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates that a type or method is pure, that is, it does not make any visible state changes.
 */
@Attribute
@Inherited
@Documented
@Retention(RetentionPolicy.CLASS)
@Conditional(ConditionString = "CONTRACTS_FULL")
@AttributeUsage(value = {
        AttributeTargets.Constructor,
        AttributeTargets.Method,
        AttributeTargets.Class,
        AttributeTargets.Parameter
}, Inherited = true)
public @interface Pure { }
