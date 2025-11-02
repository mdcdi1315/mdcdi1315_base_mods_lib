package com.github.mdcdi1315.DotNetLayer.System;

import java.lang.annotation.Documented;

/**
 * Indicates that an enumeration can be treated as a bit field; that is, a set of flags.
 */
@Attribute
@Documented
@AttributeUsage(value = AttributeTargets.Enum, Inherited = false)
public @interface Flags { }
