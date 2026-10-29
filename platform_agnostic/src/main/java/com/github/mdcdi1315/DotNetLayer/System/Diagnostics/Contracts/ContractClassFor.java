package com.github.mdcdi1315.DotNetLayer.System.Diagnostics.Contracts;

import com.github.mdcdi1315.DotNetLayer.System.Attribute;
import com.github.mdcdi1315.DotNetLayer.System.AttributeUsage;
import com.github.mdcdi1315.DotNetLayer.System.AttributeTargets;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.Conditional;

import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import java.lang.annotation.RetentionPolicy;

/**
 * Specifies that a class is a contract for a type.
 */
@Attribute
@Documented
@Retention(RetentionPolicy.CLASS)
@AttributeUsage(AttributeTargets.Class)
@Conditional(ConditionString = "CONTRACTS_FULL")
public @interface ContractClassFor
{
    /**
     * Gets the type that this code contract applies to.
     * @return The type that this contract applies to.
     */
    Class<?> TypeContractsAreFor();
}
