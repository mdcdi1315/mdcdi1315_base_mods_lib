package com.github.mdcdi1315.DotNetLayer;

/**
 * Defines distinct .NET parameter types.
 */
public enum ByRefParameterType
{
    /**
     * The parameter should be treated as a mutable reference (That means, it's reference to the object is manipulated.)
     */
    REF,
    /**
     * The parameter should be treated as a read-only input reference.
     */
    IN,
    /**
     * The parameter should be treated as a write-only input reference.
     */
    OUT,
    /**
     * The parameter should be treated as a 'ref readonly' input reference.
     * Both {@link #REF} and {@link #IN} variable types are acceptable.
     */
    READONLY,
}
