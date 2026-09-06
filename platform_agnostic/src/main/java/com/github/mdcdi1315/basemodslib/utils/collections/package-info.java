/**
 * <h4>The collections package in BML</h4>
 * The BML collections package is developed by these four different directions:
 * <ol>
 *     <li>Ensure better memory and execution requirements.</li>
 *     <li>Use {@link com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable} instead of {@link java.lang.Iterable}.</li>
 *     <li>Provide useful utilities, and make it compatible with the {@link com.github.mdcdi1315.basemodslib.utils.function} package.</li>
 *     <li>Ensure that the mods and the library itself depend on a stable and performant API.</li>
 * </ol>
 * All of these directions have driven the development of this package.
 * It currently provides (as of 1.0.35) almost everything a developer needs:
 * <ol>
 *     <li>Custom, better and faster collection API's and collection interface declarations.</li>
 *     <li>Interoperability between the collection interfaces themselves</li>
 *     <li>Easy, pluggable and fully documented API over the variety of collections.</li>
 *     <li>Ease of extensibility as needed to.</li>
 *     <li>Interoperability between {@link java.util} collections framework and these API's.</li>
 *     <li>Exercised algorithms, plus hacks for making several collection implementations faster.</li>
 *     <li>Custom projections and specializations for all primitive-type based enumerable collections.</li>
 *     <li>All kinds of manipulation (filtering, slicing, concatenation, mapping) exposed through simple attachment interfaces so that users can consume them directly.</li>
 * </ol>
 * <h5>Thread safety</h5>
 * Most of the BML collections provide, among the thread-<em>unsafe</em>
 * collections, thread-safe counterparts that can be used by multiple
 * threads at a time, but they incur additional semaphore locking during
 * calls on their mutation and getter methods. <br />
 * You can test whether an unknown BML collection enumerable is thread-safe,
 * by querying the {@link com.github.mdcdi1315.basemodslib.utils.ISynchronized}
 * interface on them. Note, however, that how each collection implements
 * thread-safety and in which methods is used is implementation-specific.
 * See the documentation supplied with the thread-safe wrapper for more
 * information. <br />
 * Note also that some collections are inherently thread-safe; for
 * example, see the {@link com.github.mdcdi1315.basemodslib.utils.collections.ArrayView} class.
 * @since 1.0.17
 */
package com.github.mdcdi1315.basemodslib.utils.collections;