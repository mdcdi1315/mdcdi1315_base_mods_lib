package com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis;

import com.github.mdcdi1315.DotNetLayer.System.Attribute;
import com.github.mdcdi1315.DotNetLayer.System.AttributeUsage;
import com.github.mdcdi1315.DotNetLayer.System.AttributeTargets;

import javax.annotation.Nonnull;
import javax.annotation.meta.When;
import java.lang.annotation.Documented;

/**
 * Specifies that when a method returns {@link #ReturnValue}, the parameter will not be null even if the corresponding type allows it.
 */
@Attribute
@Documented
@Nonnull(when = When.MAYBE)
@AttributeUsage({AttributeTargets.Parameter})
public @interface NotNullWhen {

    /**
     * Gets the return value condition.
     * @return The return value condition. If the method returns this value, the associated parameter will not be {@code null}.
     */
    boolean ReturnValue();

}
