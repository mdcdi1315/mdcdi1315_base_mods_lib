package com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis;

/**
 * Specifies the types of members that are dynamically accessed.
 * This enumeration has a FlagsAttribute attribute that allows a bitwise combination of its member values. <br /> <br />
 *
 * This enumeration supports a bitwise combination of its member values.
 */
public enum DynamicallyAccessedMemberTypes
{
    /**
     * Specifies all members.
     */
    All(-1),
    /**
     * Specifies no members.
     */
    None(0),
    /**
     * Specifies the default, parameterless public constructor.
     */
    PublicParameterlessConstructor(1),
    /**
     * Specifies all public constructors.
     */
    PublicConstructors(3),
    /**
     * Specifies all non-public constructors.
     */
    NonPublicConstructors(4),
    /**
     * Specifies all public methods.
     */
    PublicMethods(8),
    /**
     * Specifies all non-public methods.
     */
    NonPublicMethods(16),
    /**
     * Specifies all public fields.
     */
    PublicFields(32),
    /**
     * Specifies all non-public fields.
     */
    NonPublicFields(64),
    /**
     * Specifies all public nested types.
     */
    PublicNestedTypes(128),
    /**
     * Specifies all non-public nested types.
     */
    NonPublicNestedTypes(256),
    /**
     * Specifies all interfaces implemented by the type.
     */
    Interfaces(8192);

    // Note: Values 512..4096 specify .NET stuff that cannot be found in Java.

    private final int value__;

    DynamicallyAccessedMemberTypes(int v) {
        value__ = v;
    }
}
