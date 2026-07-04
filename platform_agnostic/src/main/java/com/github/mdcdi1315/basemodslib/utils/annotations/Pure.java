package com.github.mdcdi1315.basemodslib.utils.annotations;

import java.lang.annotation.*;

/**
 * Defines that a method executes non-exceptionally, and the only exceptions that it throws, if any, are coming from primitive instructions execution. <br />
 * When declared on a class, it means that all it's methods are pure, however it's constructors can still throw exceptions if needed.
 * @since 1.0.34
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE})
public @interface Pure { }
