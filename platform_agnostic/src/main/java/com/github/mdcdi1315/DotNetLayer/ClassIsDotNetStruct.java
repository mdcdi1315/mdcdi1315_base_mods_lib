package com.github.mdcdi1315.DotNetLayer;

import java.lang.annotation.*;

/**
 * Applied to a {@literal final class} or to a {@literal record} to indicate that the type was in .NET a structure.
 * <p>
 *     In Java only class types do exist. Closest to the structure of .NET is the final class in Java that extends from the {@link com.github.mdcdi1315.DotNetLayer.System.ValueType} class.
 * </p>
 * <p>
 *     That does also mean that an empty, parameterless constructor for the particular class must be also provided.
 * </p>
 */
@Documented
@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface ClassIsDotNetStruct {}
