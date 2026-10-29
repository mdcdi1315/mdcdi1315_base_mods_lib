package com.github.mdcdi1315.DotNetLayer.System.Diagnostics.Contracts;

import com.github.mdcdi1315.DotNetLayer.System.Attribute;
import com.github.mdcdi1315.DotNetLayer.System.AttributeUsage;
import com.github.mdcdi1315.DotNetLayer.System.AttributeTargets;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.Conditional;

import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import java.lang.annotation.RetentionPolicy;

/**
 * Instructs analysis tools to assume the correctness of an assembly, type, or member without performing static verification.
 */
@Attribute
@Documented
@Retention(RetentionPolicy.CLASS)
@Conditional(ConditionString = "CONTRACTS_FULL")
@AttributeUsage({
        AttributeTargets.Method,
        AttributeTargets.Class,
        AttributeTargets.Constructor,
        AttributeTargets.Field
})
public @interface ContractVerification
{
    /**
     * Gets the value that indicates whether to verify the contract of the target.
     * @return {@code true} if verification is required; otherwise, {@code false}.
     */
    boolean Value();
}
