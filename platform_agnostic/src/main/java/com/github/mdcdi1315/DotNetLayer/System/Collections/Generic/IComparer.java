package com.github.mdcdi1315.DotNetLayer.System.Collections.Generic;

import com.github.mdcdi1315.DotNetLayer.ByRefParameterType;
import com.github.mdcdi1315.DotNetLayer.DotNetByRefParameter;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

/**
 * Defines a method that a type implements to compare two objects.
 * @param <T> The type of objects to compare.
 */
public interface IComparer<@DotNetByRefParameter(ByRefParameterType.IN) T>
{
    /**
     * Compares two objects and returns a value indicating whether one is less than, equal to, or greater than the other.
     * @param x The first object to compare.
     * @param y The second object to compare.
     * @return A signed integer that indicates the relative values of x and y, as shown in the following table. <br />
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
     *              <td>{@code x} is less than {@code y}.</td>
     *          </tr>
     *          <tr>
     *              <td>Zero</td>
     *              <td>{@code x} equals {@code y}.</td>
     *          </tr>
     *          <tr>
     *              <td>Greater than zero</td>
     *              <td>{@code x} is greater than {@code y}.</td>
     *          </tr>
     *      </tbody>
     * </table>
     */
    int Compare(@AllowNull T x, @AllowNull T y);
}
