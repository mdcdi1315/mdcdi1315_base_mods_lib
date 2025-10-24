package com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices;

import com.github.mdcdi1315.DotNetLayer.System.Attribute;
import com.github.mdcdi1315.DotNetLayer.System.AttributeTargets;
import com.github.mdcdi1315.DotNetLayer.System.AttributeUsage;

import java.lang.annotation.Documented;

/**
 * Indicates that a method is an extension method, or that a class or assembly contains extension methods.
 */
@Attribute
@Documented
@AttributeUsage(value = {AttributeTargets.Class , AttributeTargets.Method} , AllowMultiple = false , Inherited = false)
public @interface Extension {}
