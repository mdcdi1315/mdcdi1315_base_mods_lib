package com.github.mdcdi1315.DotNetLayer.System.Diagnostics.Contracts;

import com.github.mdcdi1315.DotNetLayer.System.Attribute;
import com.github.mdcdi1315.DotNetLayer.System.AttributeUsage;
import com.github.mdcdi1315.DotNetLayer.System.AttributeTargets;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.Conditional;

import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import java.lang.annotation.RetentionPolicy;

/**
 * Specifies that a separate type contains the code contracts for this type.
 */
@Attribute
@Documented
@Retention(RetentionPolicy.CLASS)
@Conditional(ConditionString = "DEBUG")
@Conditional(ConditionString = "CONTRACTS_FULL")
@AttributeUsage({
        AttributeTargets.Class,
        AttributeTargets.Interface
})
public @interface ContractClass
{
    /**
     * Gets the type that contains the code contracts for this type.
     * @return The type that contains the code contracts for this type.
     */
    Class<?> TypeContainingContracts();
}
