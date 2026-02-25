package com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices;

import com.github.mdcdi1315.DotNetLayer.System.Attribute;
import com.github.mdcdi1315.DotNetLayer.System.AttributeTargets;
import com.github.mdcdi1315.DotNetLayer.System.AttributeUsage;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Specifies a source {@link Class} in another assembly. <br />
 * Remarks: <br />
 * This attribute is specified for the internal markings of the .NET Layer. <br />
 * It is not useful or relevant on Java.
 */
@Attribute
@Retention(RetentionPolicy.CLASS)
@AttributeUsage({AttributeTargets.Class, AttributeTargets.AnnotationInterface, AttributeTargets.Enum, AttributeTargets.Interface})
public @interface TypeForwardedFrom
{
    /**
     * Gets the assembly-qualified name of the source type.
     * @return The assembly-qualified name of the source type.
     */
    String AssemblyFullName();
}
