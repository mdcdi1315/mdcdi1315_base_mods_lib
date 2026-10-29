package com.github.mdcdi1315.DotNetLayer.System.Diagnostics.Contracts;

import com.github.mdcdi1315.DotNetLayer.System.Attribute;
import com.github.mdcdi1315.DotNetLayer.System.AttributeUsage;
import com.github.mdcdi1315.DotNetLayer.System.AttributeTargets;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.Conditional;

import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks a method as being the invariant method for a class.
 */
@Attribute
@Documented
@Retention(RetentionPolicy.CLASS)
@AttributeUsage(AttributeTargets.Method)
@Conditional(ConditionString = "CONTRACTS_FULL")
public @interface ContractInvariantMethod { }
