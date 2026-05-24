package com.github.mdcdi1315.basemodslib.utils.annotations;

import java.lang.annotation.*;

/**
 * Defines that while a value is guaranteed to be non-{@code null}
 * in general context, it can be {@code null} when used in a Mixin class. <br />
 * How that value can be null in Mixin is purely dependent on code and has a different
 * meaning on each declaration of this value.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface MaybeNullInMixin { }
