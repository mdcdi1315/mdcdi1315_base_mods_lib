package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.ByRefParameterType;
import com.github.mdcdi1315.DotNetLayer.DotNetByRefParameter;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

/**
 * Defines a generalized comparison method that a value type or class implements to create a type-specific comparison method for ordering or sorting its instances.
 * @param <T> The type of object to compare.
 */
public interface IComparable<@DotNetByRefParameter(ByRefParameterType.IN) T>
{
    /**
     * Compares the current instance with another object of the same type and returns an integer that indicates whether the current instance precedes, follows, or occurs in the same position in the sort order as the other object.
     * @param other An object to compare with this instance.
     * @return A value that indicates the relative order of the objects being compared. The return value has these meanings:
     * <table>
     *      <thead>
     *          <tr>
     *              <th>Value</th>
     *              <th>Meaning</th>
     *          </tr>
     *      </thead>
     *      <tbody>
     *          <tr>
     *              <td>Less than zero</td>
     *              <td>This instance precedes other in the sort order.</td>
     *          </tr>
     *          <tr>
     *              <td>Zero</td>
     *              <td>This instance occurs in the same position in the sort order as {@code other}.</td>
     *          </tr>
     *          <tr>
     *              <td>Greater than zero</td>
     *              <td>This instance follows {@code other} in the sort order.</td>
     *          </tr>
     *      </tbody>
     * </table>
     */
    int CompareTo(@MaybeNull T other);
}
