package com.github.mdcdi1315.DotNetLayer.System;

/**
 * Defines the different possible targets where an attribute can be applied to.
 */
public enum AttributeTargets
{
    /**
     * The attribute can be applied on a class.
     */
    Class,
    /**
     * The attribute can be applied on a field.
     */
    Field,
    /**
     * The attribute can be applied to a method.
     */
    Method,
    /**
     * The attribute can be applied to a constructor.
     */
    Constructor,
    /**
     * The attribute can be applied to an enumeration type.
     */
    Enum,
    /**
     * The attribute can be applied to an interface.
     */
    Interface,
    /**
     * The attribute can be applied to an annotation.
     */
    AnnotationInterface,
    /**
     * The attribute can be applied to a parameter.
     */
    Parameter,
    /**
     * The attribute can be applied to a method's return value.
     */
    ReturnValue,
    /**
     * The attribute can be applied to any valid code element.
     */
    All
}
