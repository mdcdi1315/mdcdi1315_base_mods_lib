package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.ITuple;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.TypeForwardedFrom;

/**
 * Represents a 1-tuple, or singleton.
 * @param <T1> The type of the tuple's only component.
 */
@TypeForwardedFrom(AssemblyFullName = "mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089")
public class Tuple1<T1>
    implements ITuple
{
    private final T1 m_Item1;

    /**
     * Initializes a new instance of the {@link Tuple1} class.
     * @param item1 The value of the tuple's only component.
     */
    public Tuple1(T1 item1) { m_Item1 = item1; }

    /**
     * Gets the value of the {@link Tuple1} object's single component.
     * @return The value of the current {@link Tuple1} object's single component.
     */
    public T1 GetItem1() { return m_Item1; }

    @Override
    public int GetLength() { return 1; }

    @Override
    public Object getItem(Integer integer)
    {
        if (integer != 0) {
            throw new IndexOutOfRangeException();
        } else {
            return m_Item1;
        }
    }

    @Override
    public void setItem(Integer integer, Object object) {}

    @Override
    public String toString() { return StringUtils.Concat("(", m_Item1, ")"); }
}
