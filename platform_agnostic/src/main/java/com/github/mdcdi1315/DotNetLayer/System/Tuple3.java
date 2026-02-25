package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.ITuple;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.TypeForwardedFrom;

/**
 * Represents a 3-tuple, or triple.
 * @param <T1> The type of the tuple's first component.
 * @param <T2> The type of the tuple's second component.
 * @param <T3> The type of the tuple's third component.
 */
@TypeForwardedFrom(AssemblyFullName = "mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089")
public class Tuple3<T1, T2, T3>
    implements ITuple
{
    private final T1 m_Item1; // Do not rename (binary serialization)
    private final T2 m_Item2; // Do not rename (binary serialization)
    private final T3 m_Item3; // Do not rename (binary serialization)

    /**
     * Gets the value of the current {@link Tuple3} object's first component.
     * @return The value of the current {@link Tuple3} object's first component.
     */
    public T1 GetItem1() { return m_Item1; }

    /**
     * Gets the value of the current {@link Tuple3} object's second component.
     * @return The value of the current {@link Tuple3} object's second component.
     */
    public T2 GetItem2() { return m_Item2; }

    /**
     * Gets the value of the current {@link Tuple3} object's third component.
     * @return The value of the current {@link Tuple3} object's third component.
     */
    public T3 GetItem3() { return m_Item3; }

    /**
     * Initializes a new instance of the {@link Tuple3} class.
     * @param item1 The value of the tuple's first component.
     * @param item2 The value of the tuple's second component.
     * @param item3 The value of the tuple's third component.
     */
    public Tuple3(T1 item1, T2 item2, T3 item3)
    {
        m_Item1 = item1;
        m_Item2 = item2;
        m_Item3 = item3;
    }

    @Override
    public int GetLength() { return 3; }

    @Override
    public Object getItem(Integer integer) {
        return switch (integer) {
            case 0 -> m_Item1;
            case 1 -> m_Item2;
            case 2 -> m_Item3;
            default -> throw new IndexOutOfRangeException();
        };
    }

    @Override
    public void setItem(Integer integer, Object object) {}

    @Override
    public String toString() { return StringUtils.Concat("(", m_Item1, ", ", m_Item2, ", ", m_Item3, ")"); }
}
