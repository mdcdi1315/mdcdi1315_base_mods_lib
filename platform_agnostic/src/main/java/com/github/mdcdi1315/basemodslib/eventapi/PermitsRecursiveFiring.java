package com.github.mdcdi1315.basemodslib.eventapi;

import java.lang.annotation.*;

/**
 * Applied to an event class to designate that it allows to fire other events (or another instance of itself) while an event handler is executing. <br />
 * Since 1.0.36, recursive event calls are not permitted by default to avoid CPU starvation. <br />
 * This is only validated in development environments to avoid paying the cost of checking this in release environments.
 * @since 1.0.36
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PermitsRecursiveFiring { }
