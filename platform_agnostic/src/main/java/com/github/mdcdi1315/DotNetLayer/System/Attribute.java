package com.github.mdcdi1315.DotNetLayer.System;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Special annotation to indicate that the specified annotation does exactly correspond to a .NET attribute.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@AttributeUsage(value = AttributeTargets.All , AllowMultiple = true)
public @interface Attribute {}
