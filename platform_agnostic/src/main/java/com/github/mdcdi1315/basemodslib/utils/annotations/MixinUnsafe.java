package com.github.mdcdi1315.basemodslib.utils.annotations;

import java.lang.annotation.*;

/**
 * Defines that the specified method call cannot be guaranteed about its integrity when used in a Mixin class.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface MixinUnsafe { }
