package com.github.mdcdi1315.basemodslib.config.reflect.constraints.lib_provided;

import com.github.mdcdi1315.basemodslib.config.reflect.constraints.ModConfigConstraint;

import java.lang.annotation.*;

/**
 * String fields annotated with this annotation enforce that they should not become {@code null} under any circumstance.
 */
@Documented
@ModConfigConstraint
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StringMustNotBeNull { }
