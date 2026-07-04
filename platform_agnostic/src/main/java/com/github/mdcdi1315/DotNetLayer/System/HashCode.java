/*
 * The current implementation of the hash code class is obtained from the Apache's Commons library:
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.ClassIsDotNetStruct;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEqualityComparer;

/**
 * Combines the hash code for multiple values into a single hash code.
 */
@ClassIsDotNetStruct
public final class HashCode
    extends ValueType
{
    /**
     * The default initial value to use.
     */
    private static final int DEFAULT_INITIAL_VALUE = 17;

    /**
     * The default multiplier value to use.
     */
    private static final int DEFAULT_MULTIPLIER_VALUE = 37;

    /**
     * Constant to use in building the hashCode.
     */
    private final int iConstant;

    /**
     * Running total of the hashCode.
     */
    private int iTotal;

    public HashCode()
    {
        iConstant = DEFAULT_MULTIPLIER_VALUE;
        iTotal = DEFAULT_INITIAL_VALUE;
    }

    /**
     * Diffuses the hash code returned by the specified value.
     * @param object The value to add to the hash code.
     * @return The hash code that represents the single value.
     * @param <T> The type of the value to add the hash code.
     * @apiNote Some data structures assume that hash codes are diffused across a large range.
     * This method improves the quality of the hash code returned by the value and is useful when the underlying data type is simple, for example, an integer value.
     */
    public static <T> int Combine(@AllowNull T object)
    {
        HashCode hash = new HashCode();
        hash.Add(object);
        return hash.ToHashCode();
    }

    /**
     * Combines two values into a hash code.
     * @param value1 The first value to combine into the hash code.
     * @param value2 The second value to combine into the hash code.
     * @return The hash code that represents the two values.
     * @param <T1> The type of the first value to combine into the hash code.
     * @param <T2> The type of the second value to combine into the hash code.
     */
    public static <T1, T2> int Combine(@AllowNull T1 value1, @AllowNull T2 value2)
    {
        HashCode hash = new HashCode();
        hash.Add(value1);
        hash.Add(value2);
        return hash.ToHashCode();
    }

    /**
     * Combines three values into a hash code.
     * @param value1 The first value to combine into the hash code.
     * @param value2 The second value to combine into the hash code.
     * @param value3 The third value to combine into the hash code.
     * @return The hash code that represents the three values.
     * @param <T1> The type of the first value to combine into the hash code.
     * @param <T2> The type of the second value to combine into the hash code.
     * @param <T3> The type of the third value to combine into the hash code.
     */
    public static <T1, T2, T3> int Combine(@AllowNull T1 value1, @AllowNull T2 value2, @AllowNull T3 value3)
    {
        HashCode hash = new HashCode();
        hash.Add(value1);
        hash.Add(value2);
        hash.Add(value3);
        return hash.ToHashCode();
    }

    /**
     * Combines four values into a hash code.
     * @param value1 The first value to combine into the hash code.
     * @param value2 The second value to combine into the hash code.
     * @param value3 The third value to combine into the hash code.
     * @param value4 The fourth value to combine into the hash code.
     * @return The hash code that represents the four values.
     * @param <T1> The type of the first value to combine into the hash code.
     * @param <T2> The type of the second value to combine into the hash code.
     * @param <T3> The type of the third value to combine into the hash code.
     * @param <T4> The type of the fourth value to combine into the hash code.
     */
    public static <T1, T2, T3, T4> int Combine(@AllowNull T1 value1, @AllowNull T2 value2, @AllowNull T3 value3, @AllowNull T4 value4)
    {
        HashCode hash = new HashCode();
        hash.Add(value1);
        hash.Add(value2);
        hash.Add(value3);
        hash.Add(value4);
        return hash.ToHashCode();
    }

    /**
     * Combines five values into a hash code.
     * @param value1 The first value to combine into the hash code.
     * @param value2 The second value to combine into the hash code.
     * @param value3 The third value to combine into the hash code.
     * @param value4 The fourth value to combine into the hash code.
     * @param value5 The fifth value to combine into the hash code.
     * @return The hash code that represents the five values.
     * @param <T1> The type of the first value to combine into the hash code.
     * @param <T2> The type of the second value to combine into the hash code.
     * @param <T3> The type of the third value to combine into the hash code.
     * @param <T4> The type of the fourth value to combine into the hash code.
     * @param <T5> The type of the fifth value to combine into the hash code.
     */
    public static <T1, T2, T3, T4, T5> int Combine(@AllowNull T1 value1, @AllowNull T2 value2, @AllowNull T3 value3, @AllowNull T4 value4, @AllowNull T5 value5)
    {
        HashCode hash = new HashCode();
        hash.Add(value1);
        hash.Add(value2);
        hash.Add(value3);
        hash.Add(value4);
        hash.Add(value5);
        return hash.ToHashCode();
    }

    /**
     * Combines six values into a hash code.
     * @param value1 The first value to combine into the hash code.
     * @param value2 The second value to combine into the hash code.
     * @param value3 The third value to combine into the hash code.
     * @param value4 The fourth value to combine into the hash code.
     * @param value5 The fifth value to combine into the hash code.
     * @param value6 The sixth value to combine into the hash code.
     * @return The hash code that represents the six values.
     * @param <T1> The type of the first value to combine into the hash code.
     * @param <T2> The type of the second value to combine into the hash code.
     * @param <T3> The type of the third value to combine into the hash code.
     * @param <T4> The type of the fourth value to combine into the hash code.
     * @param <T5> The type of the fifth value to combine into the hash code.
     * @param <T6> The type of the sixth value to combine into the hash code.
     */
    public static <T1, T2, T3, T4, T5, T6> int Combine(@AllowNull T1 value1, @AllowNull T2 value2, @AllowNull T3 value3, @AllowNull T4 value4, @AllowNull T5 value5, @AllowNull T6 value6)
    {
        HashCode hash = new HashCode();
        hash.Add(value1);
        hash.Add(value2);
        hash.Add(value3);
        hash.Add(value4);
        hash.Add(value5);
        hash.Add(value6);
        return hash.ToHashCode();
    }

    /**
     * Combines seven values into a hash code.
     * @param value1 The first value to combine into the hash code.
     * @param value2 The second value to combine into the hash code.
     * @param value3 The third value to combine into the hash code.
     * @param value4 The fourth value to combine into the hash code.
     * @param value5 The fifth value to combine into the hash code.
     * @param value6 The sixth value to combine into the hash code.
     * @param value7 The seventh value to combine into the hash code.
     * @return The hash code that represents the seven values.
     * @param <T1> The type of the first value to combine into the hash code.
     * @param <T2> The type of the second value to combine into the hash code.
     * @param <T3> The type of the third value to combine into the hash code.
     * @param <T4> The type of the fourth value to combine into the hash code.
     * @param <T5> The type of the fifth value to combine into the hash code.
     * @param <T6> The type of the sixth value to combine into the hash code.
     * @param <T7> The type of the seventh value to combine into the hash code.
     */
    public static <T1, T2, T3, T4, T5, T6, T7> int Combine(@AllowNull T1 value1, @AllowNull T2 value2, @AllowNull T3 value3, @AllowNull T4 value4, @AllowNull T5 value5, @AllowNull T6 value6, @AllowNull T7 value7)
    {
        HashCode hash = new HashCode();
        hash.Add(value1);
        hash.Add(value2);
        hash.Add(value3);
        hash.Add(value4);
        hash.Add(value5);
        hash.Add(value6);
        hash.Add(value7);
        return hash.ToHashCode();
    }

    /**
     * Combines eight values into a hash code.
     * @param value1 The first value to combine into the hash code.
     * @param value2 The second value to combine into the hash code.
     * @param value3 The third value to combine into the hash code.
     * @param value4 The fourth value to combine into the hash code.
     * @param value5 The fifth value to combine into the hash code.
     * @param value6 The sixth value to combine into the hash code.
     * @param value7 The seventh value to combine into the hash code.
     * @param value8 The eighth value to combine into the hash code.
     * @return The hash code that represents the seven values.
     * @param <T1> The type of the first value to combine into the hash code.
     * @param <T2> The type of the second value to combine into the hash code.
     * @param <T3> The type of the third value to combine into the hash code.
     * @param <T4> The type of the fourth value to combine into the hash code.
     * @param <T5> The type of the fifth value to combine into the hash code.
     * @param <T6> The type of the sixth value to combine into the hash code.
     * @param <T7> The type of the seventh value to combine into the hash code.
     * @param <T8> The type of the eighth value to combine into the hash code.
     */
    public static <T1, T2, T3, T4, T5, T6, T7, T8> int Combine(@AllowNull T1 value1, @AllowNull T2 value2, @AllowNull T3 value3, @AllowNull T4 value4, @AllowNull T5 value5, @AllowNull T6 value6, @AllowNull T7 value7, @AllowNull T8 value8)
    {
        HashCode hash = new HashCode();
        hash.Add(value1);
        hash.Add(value2);
        hash.Add(value3);
        hash.Add(value4);
        hash.Add(value5);
        hash.Add(value6);
        hash.Add(value7);
        hash.Add(value8);
        return hash.ToHashCode();
    }

    /**
     * Adds a single value to the hash code.
     * @param object The value to add to the hash code.
     * @param <T> The type of the value to add to the hash code.
     */
    public <T> void Add(@AllowNull T object)
    {
        if (object == null) {
            iTotal = iTotal * iConstant;
        } else {
            iTotal = iTotal * iConstant + object.hashCode();
        }
    }

    /**
     * Adds a single value to the hash code, specifying the type that provides the hash code function.
     * @param value The value to add to the hash code.
     * @param comparer The {@link IEqualityComparer} to use to calculate the hash code.
     *                 This value can be a {@code null} reference ({@code Nothing} in Visual Basic), which will use the default equality comparer for {@link T}.
     * @param <T> The type of the value to add to the hash code.
     */
    public <T> void Add(T value, IEqualityComparer<T> comparer)
    {
        if (comparer == null) {
            Add(value);
        } else {
            iTotal = iTotal * iConstant + comparer.GetHashCode(value);
        }
    }

    /**
     * Adds an array of bytes to the hash code.
     * @param bytes The array to add.
     * @throws ArgumentNullException {@code bytes} is {@code null}.
     */
    public void AddBytes(byte[] bytes)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(bytes, "bytes");

        iTotal = iTotal * iConstant + bytes.length;

        for (byte b : bytes) { iTotal = iTotal * iConstant + b; }
    }

    /**
     * Calculates the final hash code after consecutive {@link #Add} invocations.
     * @return The calculated hash code.
     * @apiNote This method must be called at most once per instance of {@link HashCode}.
     */
    public int ToHashCode() { return iTotal; }

    /**
     * {@link HashCode} is a mutable struct and should not be compared with other HashCodes.
     * Use {@link #ToHashCode} to retrieve the computed hash code.
     */
    @Override
    @Deprecated
    public int GetHashCode() { throw new NotSupportedException("Equality is not supported on HashCode instances."); }

    /**
     * {@link HashCode} is a mutable struct and should not be compared with other HashCodes.
     */
    @Override
    @Deprecated
    public boolean Equals(Object any) { throw new NotSupportedException("Equality is not supported on HashCode instances."); }
}
