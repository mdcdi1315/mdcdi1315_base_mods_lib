package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.ITuple;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.TypeForwardedFrom;

/**
 * Represents a 2-tuple, or pair.
 * @param <T1> The type of the tuple's first component.
 * @param <T2> The type of the tuple's second component.
 */
@TypeForwardedFrom(AssemblyFullName = "mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089")
public class Tuple2<T1, T2>
    implements ITuple
{
    private final T1 m_Item1; // Do not rename (binary serialization)
    private final T2 m_Item2; // Do not rename (binary serialization)

    /**
     * Gets the value of the current {@link Tuple2} object's first component.
     * @return The value of the current {@link Tuple2} object's first component.
     */
    public T1 GetItem1() { return m_Item1; }

    /**
     * Gets the value of the current {@link Tuple2} object's second component.
     * @return The value of the current {@link Tuple2} object's second component.
     */
    public T2 GetItem2() { return m_Item2; }

    /**
     * Initializes a new instance of the {@link Tuple2} class.
     * @param item1 The value of the tuple's first component.
     * @param item2 The value of the tuple's second component.
     */
    public Tuple2(T1 item1, T2 item2)
    {
        m_Item1 = item1;
        m_Item2 = item2;
    }

    @Override
    public int GetLength() { return 2; }

    @Override
    public Object getItem(Integer integer) {
        return switch (integer) {
            case 0 -> m_Item1;
            case 1 -> m_Item2;
            default -> throw new IndexOutOfRangeException();
        };
    }

    @Override
    public void setItem(Integer integer, Object object) {}

    @Override
    public String toString() { return StringUtils.Concat("(", m_Item1, ", ", m_Item2, ")"); }
}
