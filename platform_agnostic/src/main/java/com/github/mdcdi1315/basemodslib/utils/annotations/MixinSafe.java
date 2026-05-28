package com.github.mdcdi1315.basemodslib.utils.annotations;

import java.lang.annotation.*;

/**
 * Defines that the specified method call is guaranteed about its integrity when used in a Mixin class.
 * @since 1.0.34
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface MixinSafe { }
