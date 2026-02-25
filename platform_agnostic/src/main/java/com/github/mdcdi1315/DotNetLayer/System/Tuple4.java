package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.ITuple;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.TypeForwardedFrom;

/**
 * Represents a 4-tuple, or quadruple.
 * @param <T1> The type of the tuple's first component.
 * @param <T2> The type of the tuple's second component.
 * @param <T3> The type of the tuple's third component.
 * @param <T4> The type of the tuple's fourth component.
 */
@TypeForwardedFrom(AssemblyFullName = "mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089")
public class Tuple4<T1, T2, T3, T4>
    implements ITuple
{
    private final T1 m_Item1; // Do not rename (binary serialization)
    private final T2 m_Item2; // Do not rename (binary serialization)
    private final T3 m_Item3; // Do not rename (binary serialization)
    private final T4 m_Item4; // Do not rename (binary serialization)

    /**
     * Gets the value of the current {@link Tuple4} object's first component.
     * @return The value of the current {@link Tuple4} object's first component.
     */
    public T1 GetItem1() { return m_Item1; }

    /**
     * Gets the value of the current {@link Tuple4} object's second component.
     * @return The value of the current {@link Tuple4} object's second component.
     */
    public T2 GetItem2() { return m_Item2; }

    /**
     * Gets the value of the current {@link Tuple4} object's third component.
     * @return The value of the current {@link Tuple4} object's third component.
     */
    public T3 GetItem3() { return m_Item3; }

    /**
     * Gets the value of the current {@link Tuple4} object's fourth component.
     * @return The value of the current {@link Tuple4} object's fourth component.
     */
    public T4 GetItem4() { return m_Item4; }

    /**
     * Initializes a new instance of the {@link Tuple4} class.
     * @param item1 The value of the tuple's first component.
     * @param item2 The value of the tuple's second component.
     * @param item3 The value of the tuple's third component.
     * @param item4 The value of the tuple's fourth component.
     */
    public Tuple4(T1 item1, T2 item2, T3 item3, T4 item4)
    {
        m_Item1 = item1;
        m_Item2 = item2;
        m_Item3 = item3;
        m_Item4 = item4;
    }

    @Override
    public int GetLength() { return 4; }

    @Override
    public Object getItem(Integer integer) {
        return switch (integer) {
            case 0 -> m_Item1;
            case 1 -> m_Item2;
            case 2 -> m_Item3;
            case 3 -> m_Item4;
            default -> throw new IndexOutOfRangeException();
        };
    }

    @Override
    public void setItem(Integer integer, Object object) {}

    @Override
    public String toString() { return StringUtils.Concat("(", m_Item1, ", ", m_Item2, ", ", m_Item3, ", ", m_Item4, ")"); }
}
