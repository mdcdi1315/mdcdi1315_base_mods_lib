package com.github.mdcdi1315.DotNetLayer.System.Collections;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

/**
 * Exposes a method that compares two objects.
 */
public interface IComparer
{
    /**
     * Compares two objects and returns a value indicating whether one is less than, equal to, or greater than the other.
     * @param x The first object to compare.
     * @param y The second object to compare.
     * @return A signed integer that indicates the relative values of x and y:
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
    int Compare(@AllowNull Object x, @AllowNull Object y);
}
