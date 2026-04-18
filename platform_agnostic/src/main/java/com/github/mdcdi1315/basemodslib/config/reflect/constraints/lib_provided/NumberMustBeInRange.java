package com.github.mdcdi1315.basemodslib.config.reflect.constraints.lib_provided;

import com.github.mdcdi1315.basemodslib.config.reflect.constraints.ModConfigConstraint;

import java.lang.annotation.*;

/**
 * Numeric fields annotated with this annotation must be into the specified numeric range.
 */
@Documented
@ModConfigConstraint
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NumberMustBeInRange
{
    /**
     * The minimum inclusive bound of the valid values for the applied field.
     * @return The minimum inclusive bound.
     */
    int min_inclusive();

    /**
     * The maximum inclusive bound of the valid values for the applied field.
     * @return The maximum inclusive bound.
     */
    int max_inclusive();
}
