/**
 * Provides {@link com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable} types that better handle
 * Java's special primitive type handling in collection instances. <br />
 * Note that the enumerators can also be just attachments, but it would be much more useful if implementors
 * override the {@code getUncastedCurrent} method. <br />
 * This package is called this way because we are projecting the boxed primitive type as an unboxed primitive type.
 * @since 1.0.35
 */
package com.github.mdcdi1315.basemodslib.utils.collections.projections;