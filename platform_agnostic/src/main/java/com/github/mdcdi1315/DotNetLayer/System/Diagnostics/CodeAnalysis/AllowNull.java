package com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis;

import com.github.mdcdi1315.DotNetLayer.System.Attribute;
import com.github.mdcdi1315.DotNetLayer.System.AttributeUsage;
import com.github.mdcdi1315.DotNetLayer.System.AttributeTargets;

import javax.annotation.Nullable;
import java.lang.annotation.Documented;

/**
 * Specifies that {@code null} is allowed as an input even if the corresponding type disallows it.
 */
@Nullable
@Attribute
@Documented
@AttributeUsage({AttributeTargets.Parameter, AttributeTargets.Field})
public @interface AllowNull { }
