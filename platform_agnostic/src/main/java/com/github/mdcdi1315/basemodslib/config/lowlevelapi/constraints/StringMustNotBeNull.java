package com.github.mdcdi1315.basemodslib.config.lowlevelapi.constraints;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * String fields annotated with this annotation enforce that they should not become {@code null} under any circumstance.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface StringMustNotBeNull { }
