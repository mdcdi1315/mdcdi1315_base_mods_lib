package com.github.mdcdi1315.DotNetLayer.System;

/**
 * Supports cloning, which creates a new instance of a class with the same value as an existing instance.
 */
public interface ICloneable
{
    /**
     * Creates a new object that is a copy of the current instance.
     * @return A new object that is a copy of this instance.
     */
    Object Clone();
}
