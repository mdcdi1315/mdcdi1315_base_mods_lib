package com.github.mdcdi1315.basemodslib.config.reflect.constraints;

import java.lang.annotation.*;

/**
 * Defined to ALL those annotation interfaces to declare them as being mod configuration data constraints.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ModConfigConstraint { }