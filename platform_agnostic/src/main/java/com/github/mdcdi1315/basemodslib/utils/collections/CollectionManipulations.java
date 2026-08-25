package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.*;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.utils.function.*;
import com.github.mdcdi1315.basemodslib.utils.EmptyEnumerable;
import com.github.mdcdi1315.basemodslib.utils.EmptyEnumerator;
import com.github.mdcdi1315.basemodslib.utils.collections.helpers.*;
import com.github.mdcdi1315.basemodslib.utils.collections.helpers.sorting.*;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.IIntEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.projections.ILongEnumerable;

/**
 * Provides utility and generalization methods around the {@link IEnumerable} type. <br />
 * Many of these are derived from the .NET <a href="https://learn.microsoft.com/en-us/dotnet/api/system.linq.enumerable">System.Linq.Enumerable</a> class.
 * @since 1.0.31
 */
@SuppressWarnings("unused")
public final class CollectionManipulations
{
    // Do not let anyone be able to instantiate this class.
    private CollectionManipulations() {}

    /**
     * Efficiently converts an {@link ICollection} instance to a
     * <a href="https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/doc-files/coll-index.html">Java Collections Framework</a>
     * {@link java.util.Collection} instance.
     * @param collection The {@link ICollection} instance to convert.
     * @return The converted instance represented as a {@link java.util.Collection} instance.
     *         The only method that is not supported by the returned wrapper is the {@link java.util.Collection#retainAll} method.
     * @param <T> The type of the elements of {@code collection}.
     * @throws ArgumentNullException {@code collection} is {@code null}.
     */
    @NotNull
    public static <T> java.util.Collection<T> AsJavaCollection(ICollection<T> collection)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(collection, "collection");
        return new WrappedJavaCollectionFromICollection<>(collection);
    }

    /**
     * Efficiently converts an {@link IList} instance to a
     * <a href="https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/doc-files/coll-index.html">Java Collections Framework</a>
     * {@link java.util.List} instance.
     * @param collection The {@link IList} instance to convert.
     * @return The converted instance represented as a {@link java.util.List} instance.
     * @param <T> The type of the elements of {@code collection}.
     * @throws ArgumentNullException {@code collection} is {@code null}.
     */
    @NotNull
    public static <T> java.util.List<T> AsJavaList(IList<T> collection)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(collection, "collection");
        return new WrappedJavaListFromIList<>(collection);
    }

    /**
     * Efficiently converts an {@link ITraversableQueue} instance to a
     * <a href="https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/doc-files/coll-index.html">Java Collections Framework</a>
     * {@link java.util.Queue} instance.
     * @param queue The {@link ITraversableQueue} instance to convert.
     * @return The converted instance represented as a {@link java.util.Queue} instance.
     * @param <T> The type of the elements of {@code queue}.
     * @throws ArgumentNullException {@code collection} is {@code null}.
     */
    @NotNull
    public static <T> java.util.Queue<T> AsJavaQueue(ITraversableQueue<T> queue)
    {
        ArgumentNullException.ThrowIfNull(queue, "queue");
        return new WrappedJavaQueueFromIQueue<>(queue);
    }

    /**
     * Efficiently converts a <a href="https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/doc-files/coll-index.html">Java Collections Framework</a>
     * {@link java.util.Collection} instance to an {@link ICollection} instance.
     * @param collection The Java collection instance to be converted.
     * @return The converted instance represented as an {@link ICollection} instance.
     * @param <T> The type of the elements of {@code collection}.
     * @throws ArgumentNullException {@code collection} is {@code null}.
     * @since 1.0.34
     */
    @NotNull
    public static <T> ICollection<T> AsCollection(java.util.Collection<T> collection)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(collection, "collection");
        return new WrappedICollectionFromJavaCollection<>(collection);
    }

    /**
     * Efficiently converts a <a href="https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/doc-files/coll-index.html">Java Collections Framework</a>
     * {@link java.util.List} instance to an {@link ICollection} instance.
     * @param list The Java list collection instance to be converted.
     * @return The converted instance represented as an {@link IList} instance.
     * @param <T> The type of the elements of {@code list}.
     * @throws ArgumentNullException {@code list} is {@code null}.
     * @since 1.0.34
     */
    @NotNull
    public static <T> IList<T> AsList(java.util.List<T> list)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(list, "list");
        return new WrappedIListFromJavaList<>(list);
    }

    /**
     * Efficiently converts a <a href="https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/doc-files/coll-index.html">Java Collections Framework</a>
     * {@link java.util.Queue} instance to an {@link IQueue} instance.
     * @param queue The Java queue collection instance to be converted.
     * @return The converted instance represented as an {@link IQueue} instance.
     * @param <T> The type of the elements of {@code queue}.
     * @throws ArgumentNullException {@code queue} is {@code null}.
     * @since 1.0.34
     * @apiNote Due to the fact that the {@link java.util.Queue} interface extends from the
     * {@link java.util.Collection} interface, the returned object does also implement the {@link ICollection} interface.
     */
    @NotNull
    public static <T> IQueue<T> AsQueue(java.util.Queue<T> queue)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(queue, "queue");
        return new WrappedIQueueFromJavaQueue<>(queue);
    }

    /**
     * Efficiently converts an instance of the {@link IReadOnlyList} interface, to an
     * {@link ITraversableCollection}, while also implementing the {@link IReadOnlyList}.
     * @param read_only The read-only list to convert to a traversable collection.
     * @return A new wrapper instance that references the given {@code read_only} list,
     * and delegates it's methods to it.
     * @param <T> The type of the elements of {@code read_only}.
     * @throws ArgumentNullException {@code read_only} is {@code null}.
     * @since 1.0.37
     */
    @NotNull
    public static <T> ITraversableCollection<T> AsTraversableCollection(IReadOnlyList<T> read_only)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(read_only, "read_only");
        if (read_only instanceof ReadOnlyListToTraversableCollection<T> c) {
            return c;
        } else {
            return new ReadOnlyListToTraversableCollection<>(read_only);
        }
    }

    /**
     * Translates a given {@link KeyValuePair} instance to a read-only {@link java.util.Map.Entry} instance.
     * @param pair The {@link KeyValuePair} to translate.
     * @return The translated {@link java.util.Map.Entry} value of {@code pair}.
     * @param <TKey> The type of the key of the input key-value pair.
     * @param <TValue> The type of the value of the input key-value pair.
     * @since 1.0.35
     */
    @NotNull
    public static <TKey, TValue> java.util.Map.Entry<TKey, TValue> AsMapEntry(KeyValuePair<TKey, TValue> pair) { return new KVPToMapEntry<>(pair); }

    /**
     * Translates a given {@link java.util.Map.Entry} to a new {@link KeyValuePair} instance.
     * @param map_entry The {@link java.util.Map.Entry} to translate.
     * @return The translated {@link KeyValuePair} value of {@code map_entry}.
     * @param <TKey> The type of the key of the input key-value pair.
     * @param <TValue> The type of the value of the input key-value pair.
     * @since 1.0.35
     * @throws ArgumentNullException {@code map_entry} is {@code null}.
     */
    @NotNull
    @SuppressWarnings("DeconstructionCanBeUsed")
    public static <TKey, TValue> KeyValuePair<TKey, TValue> AsKeyValuePair(java.util.Map.Entry<TKey, TValue> map_entry)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(map_entry, "map_entry");
        if (map_entry instanceof KVPToMapEntry<TKey, TValue> e) {
            return e.pair();
        } else {
            return new KeyValuePair<>(map_entry.getKey(), map_entry.getValue());
        }
    }

    /**
     * Gets the enumerator instance of the specified {@link IEnumerable} object safely,
     * even if the enumerable object is {@code null}, or if even it's dedicated {@link IEnumerable#GetEnumerator()} method returns {@code null}.
     * @param enumerable The enumerable object to retrieve an enumerator for it.
     * @return A new and unknown instance of the {@link IEnumerator} interface.
     * @param <T> The type of objects to be returned through the enumerator.
     * @apiNote This method does always return a non-null value, which must be disposed, even if {@code enumerable} is {@code null}.
     * @since 1.0.35
     */
    @NotNull
    @SuppressWarnings("resource")
    public static <T> IEnumerator<T> GetEnumeratorSafe(@AllowNull IEnumerable<T> enumerable)
    {
        if (enumerable == null || IsEmptyUnsafe(enumerable)) {
            return new EmptyEnumerator<>();
        } else {
            IEnumerator<T> e = enumerable.GetEnumerator();
            return (e == null) ? new EmptyEnumerator<>() : e;
        }
    }

    /**
     * Maps all the elements of the specified {@code enumerable} instance and by using a conversion function, it converts all the elements of the enumerable to type {@link TR}.
     * @param enumerable The source {@link IEnumerable} instance.
     * @param converter The function that is able to convert elements of type {@link TS} into elements of type {@link TR}.
     * @return The elements contained in {@code enumerable} converted using the function provided in the {@code converter} parameter.
     * @param <TS> The type of the elements of {@code enumerable}.
     * @param <TR> The type of the elements of the return value.
     * @throws ArgumentNullException {@code enumerable} and/or {@code converter} are {@code null}.
     */
    @NotNull
    public static <TS, TR> IEnumerable<TR> MapTo(IEnumerable<TS> enumerable, Converter<TS, TR> converter)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(converter, "converter");
        ArgumentNullException.ThrowIfNull(enumerable, "enumerable");
        if (IsEmptyUnsafe(enumerable)) {
            return new EmptyEnumerable<>();
        } else if (enumerable instanceof ISupportsDirectConversionTo<TS> c_supported) {
            return c_supported.ConvertAll(converter);
        } else {
            return new MapToMethodEnumerable<>(enumerable, converter);
        }
    }

    /**
     * Filters all the elements in the specified {@code enumerable} and returns only those that pass the provided {@code predicate}.
     * @param enumerable The source {@link IEnumerable} instance.
     * @param predicate The predicate to filter elements from the {@code enumerable}.
     * @return A new {@link IEnumerable} instance containing only those elements that pass the provided {@code predicate}.
     * @param <T> The type of the elements of {@code enumerable}.
     * @throws ArgumentNullException {@code enumerable} and/or {@code predicate} are {@code null}.
     */
    @NotNull
    public static <T> IEnumerable<T> FilterBy(IEnumerable<T> enumerable, Predicate<T> predicate)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(predicate, "predicate");
        ArgumentNullException.ThrowIfNull(enumerable, "enumerable");
        if (FunctionManipulations.IsAlwaysTrue(predicate)) {
            return enumerable;
        } else if (IsEmptyUnsafe(enumerable) || FunctionManipulations.IsAlwaysFalse(predicate)) {
            return new EmptyEnumerable<>();
        } else if (enumerable instanceof ISupportsFiltering<T> c_supported) {
            return c_supported.FilterBy(predicate);
        } else {
            return new FilterByEnumerable<>(enumerable, predicate);
        }
    }

    /**
     * Concatenates two sequences.
     * @param first The first sequence to concatenate.
     * @param second The sequence to concatenate to the first sequence.
     * @return An {@link IEnumerable} that contains the concatenated elements of the two input sequences.
     * @param <T> The type of the elements of the input sequences.
     * @throws ArgumentNullException {@code first} and/or {@code second} are {@code null}.
     */
    @NotNull
    public static <T> IEnumerable<T> Concat(IEnumerable<T> first, IEnumerable<T> second)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(first, "first");
        ArgumentNullException.ThrowIfNull(second, "second");
        boolean ee_1 = IsEmptyUnsafe(first);
        boolean ee_2 = IsEmptyUnsafe(second);
        return (ee_1 && ee_2) ? new EmptyEnumerable<>() : (
                (ee_1) ? second : (
                        (ee_2) ? first : new ConcatenatingEnumerable<>(first, second)
                )
        );
    }

    /**
     * Concatenates a multiple of sequences.
     * @param enumerables The sequences to concatenate.
     * @return An {@link IEnumerable} that contains the concatenated elements of the input sequences.
     * @param <T> The base type of the elements of the input sequences.
     * @throws ArgumentNullException {@code enumerables} is {@code null}.
     * @since 1.0.35
     */
    @NotNull
    @SafeVarargs
    public static <T> IEnumerable<T> Concat(IEnumerable<? extends T>... enumerables)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(enumerables, "enumerables");
        return enumerables.length == 0 ?
                new EmptyEnumerable<>() :
                new MultipleConcatenatingEnumerablesEnumerable<>(enumerables);
    }

    /**
     * Skips the specified number of elements from {@code enumerable}.
     * @param enumerable The source {@link IEnumerable} instance.
     * @param count Number of elements to skip from the beginning of the {@link IEnumerable} instance.
     * @return A new {@link IEnumerable} instance, skipping the first {@code count} elements. <br />
     * (That is, the first {@code count} elements are not returned by the enumerator)
     * @param <T> The type of the elements of {@code enumerable}.
     * @throws ArgumentNullException {@code enumerable} is {@code null}.
     * @throws ArgumentOutOfRangeException {@code count} is a negative value.
     */
    @NotNull
    public static <T> IEnumerable<T> Skip(IEnumerable<T> enumerable, int count)
            throws ArgumentNullException, ArgumentOutOfRangeException
    {
        ArgumentNullException.ThrowIfNull(enumerable, "enumerable");
        if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count cannot be a negative value.");
        } else if (IsEmptyUnsafe(enumerable)) {
            return new EmptyEnumerable<>();
        } else {
            return new SkipEnumerable<>(enumerable, count);
        }
    }

    /**
     * Returns a specified number of contiguous elements from the start of a sequence.
     * @param enumerable The sequence to return elements from.
     * @param count The number of elements to return.
     * @return An {@link IEnumerable} that contains the specified number of elements from the start of the input sequence.
     * @param <T> The type of the elements of {@code enumerable}.
     * @throws ArgumentNullException {@code enumerable} is {@code null}.
     * @throws ArgumentOutOfRangeException {@code count} is a negative value.
     */
    @NotNull
    public static <T> IEnumerable<T> Take(IEnumerable<T> enumerable, int count)
            throws ArgumentNullException, ArgumentOutOfRangeException
    {
        ArgumentNullException.ThrowIfNull(enumerable, "enumerable");
        if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count cannot be a negative value.");
        } else if (count == 0 || IsEmptyUnsafe(enumerable)) {
            return new EmptyEnumerable<>();
        } else if (enumerable instanceof ISupportsSlicing<T> c_supported) {
            return c_supported.Slice(0, count);
        } else {
            return new SliceEnumerable<>(enumerable, 0, count);
        }
    }

    /**
     * Converts a given {@link IEnumerable} instance to an equivalent
     * {@link IterableWithDisposableIterator} instance for use within the Java collections framework.
     * @param enumerable The {@link IEnumerable} instance to convert.
     * @return A new instance of the {@link IterableWithDisposableIterator} interface, that is essentially an {@link Iterable} instance.
     * @param <T> The type of the elements of {@code enumerable}.
     * @throws ArgumentNullException {@code enumerable} is {@code null}.
     */
    @NotNull
    public static <T> IterableWithDisposableIterator<T> ToIterable(IEnumerable<T> enumerable)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(enumerable, "enumerable");
        return new EnumerableToIterable<>(enumerable);
    }

    /**
     * Converts a given {@link Iterable} instance to an equivalent
     * {@link IEnumerable} instance for use within this collection framework.
     * @param iterable The {@link Iterable} instance to convert.
     * @return A new instance of the {@link BaseEnumerable} class.
     * @param <T> The type of the elements of {@code iterable}.
     * @throws ArgumentNullException {@code iterable} is {@code null}.
     * @since 1.0.34
     */
    @NotNull
    public static <T> BaseEnumerable<T> ToEnumerable(Iterable<T> iterable)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(iterable, "iterable");
        return new IterableToEnumerable<>(iterable);
    }

    /**
     * Purges all the elements contained in the specified {@link IEnumerable} instance into a new {@link IList} instance.
     * @param enumerable The enumerable that contains the elements to copy them to a new {@link IList} instance.
     * @param list_creator A function that returns an empty list collection instance of type {@link TL}.
     * @return A collection of type {@link TL}, containing all the elements in {@code enumerable}.
     * @param <T> The type of the elements that the {@code enumerable} contains.
     * @param <TL> The type of the {@link IList} to return.
     * @throws ArgumentNullException {@code enumerable} and/or {@code list_creator} are {@code null}.
     */
    @NotNull
    public static <T, TL extends IList<T>> TL ToList(IEnumerable<T> enumerable, Func1<TL> list_creator)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(enumerable, "enumerable");
        ArgumentNullException.ThrowIfNull(list_creator, "list_creator");

        TL list_inst = list_creator.function();

        if (!IsEmptyUnsafe(enumerable))
        {
            if (list_inst instanceof IArrayBasedCollection ac &&
                    enumerable instanceof ITraversableCollection<T> t)
            {
                ac.EnsureCapacity(t.GetCount());
            }

            try (IEnumerator<T> enumerator = enumerable.GetEnumerator())
            {
                while (enumerator.MoveNext())
                {
                    list_inst.Add(enumerator.getCurrent());
                }
            }
        }
        return list_inst;
    }

    /**
     * Generates a sequence of integral numbers within a specified range.
     * @param start The value of the first integer in the sequence.
     * @param count The number of sequential integers to generate.
     * @return An {@link IEnumerable} that contains a range of sequential integral numbers.
     * @throws ArgumentOutOfRangeException {@code count} is less than 0.
     * @apiNote Since 1.0.35, this method returns an instance of the {@link IIntEnumerable} interface,
     * better suited for primitive accessing and operations, and it's enumerator instances are able
     * to return the unboxed type directly. This might be ABI incompatible for mods compiled against
     * older versions of the library, but was done to avoid future breakage.
     */
    @NotNull
    public static IIntEnumerable Range(int start, int count)
        throws ArgumentOutOfRangeException
    {
        if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count cannot be a negative value.");
        } else if (((long)start + count - 1L) > Integer.MAX_VALUE) {
            throw new ArgumentOutOfRangeException("count", "count + start - 1 is larger than 2147483647.");
        } else {
            return new RangeEnumerable(start, count);
        }
    }

    /**
     * Generates a sequence of integral numbers within a specified range.
     * @param start The value of the first integer in the sequence.
     * @param count The number of sequential integers to generate.
     * @return An {@link IEnumerable} that contains a range of sequential integral numbers.
     * @throws ArgumentOutOfRangeException {@code count} is less than 0.
     * @since 1.0.35
     */
    @NotNull
    public static ILongEnumerable LongRange(long start, long count)
        throws ArgumentOutOfRangeException
    {
        if (count < 0L) {
            throw new ArgumentOutOfRangeException("count", "Count cannot be a negative value.");
        } else if (start + count < 0L) {
            throw new ArgumentOutOfRangeException("count", "count + start - 1 is larger than 2147483647.");
        } else {
            return new LongRangeEnumerable(start, count);
        }
    }

    /**
     * Filters the elements of a {@link com.github.mdcdi1315.DotNetLayer.System.Collections.IEnumerable} instance, given a specified type. <br />
     * Then, the elements that pass the filtering are cast to type {@link T}.
     * @param enumerable The {@link com.github.mdcdi1315.DotNetLayer.System.Collections.IEnumerable} to enumerate elements from.
     * @param type The {@link Class} that provides type information about {@link T}.
     * @return A new instance of {@link IEnumerable}, containing only the elements from {@code enumerable} that are compatible to type {@link T}.
     * @param <T> The type of the elements to be returned.
     * @throws ArgumentNullException {@code enumerable} and/or {@code type} are {@code null}.
     */
    @NotNull
    public static <T> IEnumerable<T> OfType(com.github.mdcdi1315.DotNetLayer.System.Collections.IEnumerable enumerable, Class<T> type)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(type, "type");
        ArgumentNullException.ThrowIfNull(enumerable, "enumerable");
        return new OfTypeEnumerable<>(enumerable, type);
    }

    /**
     * Determines whether the specified {@link IEnumerable} does not contain any elements.
     * @param enumerable The {@link IEnumerable} instance to test.
     * @return A value whether the {@link IEnumerable} instance does have zero (no) elements.
     * @param <T> The type of the elements that the {@code enumerable} contains.
     * @throws ArgumentNullException {@code enumerable} is {@code null}.
     */
    public static <T> boolean IsEmpty(IEnumerable<T> enumerable)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(enumerable, "enumerable");
        return IsEmptyUnsafe(enumerable);
    }

    /**
     * Determines whether the specified {@link IEnumerable} does contain at least one element.
     * @param enumerable The {@link IEnumerable} instance to test.
     * @return A value whether the {@link IEnumerable} instance does have at least one element.
     * @param <T> The type of the elements that the {@code enumerable} contains.
     * @throws ArgumentNullException {@code enumerable} is {@code null}.
     * @apiNote This API is the exact opposite of the {@link #IsEmpty(IEnumerable)} API.
     * @see #IsEmpty(IEnumerable)
     */
    public static <T> boolean Any(IEnumerable<T> enumerable)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(enumerable, "enumerable");
        return !IsEmptyUnsafe(enumerable);
    }

    @SuppressWarnings({"IfCanBeSwitch", "resource"})
    private static <T> boolean IsEmptyUnsafe(IEnumerable<T> enumerable)
    {
        if (enumerable instanceof IEmptyEnumerable) {
            return true;
        } else if (enumerable instanceof ITraversableCollection<T> t) {
            return t.GetCount() == 0;
        } else if (enumerable instanceof ICollection<T> c) {
            return c.getCount() == 0;
        } else if (enumerable instanceof SingletonEnumeratorEnumerable<T>) {
            // Avoid enumerating elements on singleton enumerator enumerables,
            // only check whether we have an empty enumerator on it.
            // There is also a small possibility that MoveNext returns false on allegedly
            // non-empty instance, but doing so may spoil the enumerator's position,
            // since it is only itself.
            return enumerable.GetEnumerator() instanceof EmptyEnumerator<T>;
        } else {
            try (IEnumerator<T> enumerator = enumerable.GetEnumerator()) { return !enumerator.MoveNext(); }
        }
    }

    /**
     * Determines whether any element of a sequence satisfies a condition.
     * @param enumerable An {@link IEnumerable} whose elements to apply the predicate to.
     * @param predicate A function to test each element for a condition.
     * @return {@code true} if the source sequence is not empty and at least one of its elements passes the test in the specified predicate; otherwise, {@code false}.
     * @param <T> The type of the elements of {@code enumerable}.
     * @throws ArgumentNullException {@code enumerable} and/or {@code predicate} are {@code null}.
     * @see #All(IEnumerable, Predicate)
     */
    public static <T> boolean Any(IEnumerable<T> enumerable, Predicate<T> predicate)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(predicate, "predicate");
        ArgumentNullException.ThrowIfNull(enumerable, "enumerable");

        if (FunctionManipulations.IsAlwaysTrue(predicate)) {
            return true;
        } else if (FunctionManipulations.IsAlwaysFalse(predicate)) {
            return false;
        } else if (IsEmptyUnsafe(enumerable)) {
            return false;
        } else {
            try (IEnumerator<T> enumerator = enumerable.GetEnumerator())
            {
                while (enumerator.MoveNext())
                {
                    if (predicate.predicate(enumerator.getCurrent())) { return true; }
                }
            }

            return false;
        }
    }

    /**
     * Gets a value whether a specified value is contained in {@code enumerable}.
     * @param enumerable The {@link IEnumerable} to test whether it has {@code value} or not.
     * @param value The value to search in {@code enumerable}.
     * @param comparer Optional. The {@linkplain IEqualityComparer equality comparer} to use for comparing all the elements of {@code enumerable} against {@code value}.
     * @return A boolean value whether {@code value} is contained in {@code enumerable} or not.
     * @param <T> The type of the elements of {@code enumerable}.
     * @throws ArgumentNullException {@code enumerable} is {@code null}.
     */
    public static <T> boolean Contains(IEnumerable<T> enumerable, @AllowNull T value, @AllowNull IEqualityComparer<T> comparer)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(enumerable, "enumerable");

        if (!IsEmptyUnsafe(enumerable))
        {
            if (comparer == null) {
                comparer = new EqualityComparer.ObjectEqualityComparer<>();
            }

            try (IEnumerator<T> enumerator = enumerable.GetEnumerator())
            {
                while (enumerator.MoveNext())
                {
                    if (comparer.Equals(enumerator.getCurrent(), value)) { return true; }
                }
            }
        }

        return false;
    }

    /**
     * Gets a value whether a specified value is contained in {@code enumerable}.
     * @param enumerable The {@link IEnumerable} to test whether it has {@code value} or not.
     * @param value The value to search in {@code enumerable}.
     * @return A boolean value whether {@code value} is contained in {@code enumerable} or not.
     * @param <T> The type of the elements of {@code enumerable}.
     * @throws ArgumentNullException {@code enumerable} is {@code null}.
     */
    public static <T> boolean Contains(IEnumerable<T> enumerable, @AllowNull T value) throws ArgumentNullException { return Contains(enumerable, value, null); }

    /**
     * Executes an action to all the {@link IEnumerable} elements.
     * @param enumerable The {@link IEnumerable} that contains the elements to execute the action to.
     * @param action The {@link Action1} to execute.
     * @param <T> The type of the elements of {@code enumerable}.
     * @throws ArgumentNullException {@code enumerable} and/or {@code action} are {@code null}.
     */
    public static <T> void ForEach(IEnumerable<T> enumerable, Action1<T> action)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(action, "action");
        ArgumentNullException.ThrowIfNull(enumerable, "enumerable");
        if (IsEmptyUnsafe(enumerable)) { return; }
        try (IEnumerator<T> enumerator = enumerable.GetEnumerator())
        {
            while (enumerator.MoveNext())
            {
                action.action(enumerator.getCurrent());
            }
        }
    }

    /**
     * Applies an accumulator function over a sequence.
     * The specified seed value is used as the initial accumulator value, and the specified function is used to select the result value.
     * @param enumerable An {@link IEnumerable} to aggregate over.
     * @param seed The initial accumulator value.
     * @param func An accumulator function to be invoked on each element.
     * @param result_func A function to transform the final accumulator value into the result value.
     * @return The transformed final accumulator value.
     * @param <T> The type of the elements of {@code enumerable}.
     * @param <TAccumulate> The type of the accumulator value.
     * @param <TResult> The type of the resulting value.
     * @throws ArgumentNullException {@code enumerable} and/or {@code func} and/or {@code result_func} are {@code null}.
     */
    @MaybeNull
    public static <T, TAccumulate, TResult> TResult Aggregate(IEnumerable<T> enumerable, @AllowNull TAccumulate seed, Func3<TAccumulate, T, TAccumulate> func, Func2<TAccumulate, TResult> result_func)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(func, "func");
        ArgumentNullException.ThrowIfNull(enumerable, "enumerable");
        ArgumentNullException.ThrowIfNull(result_func, "result_func");

        if (!IsEmptyUnsafe(enumerable))
        {
            try (IEnumerator<T> enumerator = enumerable.GetEnumerator())
            {
                while (enumerator.MoveNext())
                {
                    seed = func.function(seed, enumerator.getCurrent());
                }
            }
        }

        return result_func.function(seed);
    }

    /**
     * Determines whether all elements of a sequence satisfy a condition.
     * @param enumerable An {@link IEnumerable} that contains the elements to apply the predicate to.
     * @param predicate A function to test each element for a condition.
     * @return {@code true} if every element of the source sequence passes the test in the specified predicate, or if the sequence is empty; otherwise, {@code false}.
     * @param <T> The type of the elements of {@code enumerable}.
     * @throws ArgumentNullException {@code enumerable} and/or {@code predicate} are {@code null}.
     */
    public static <T> boolean All(IEnumerable<T> enumerable, Predicate<T> predicate)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(predicate, "predicate");
        ArgumentNullException.ThrowIfNull(enumerable, "enumerable");

        if (FunctionManipulations.IsAlwaysTrue(predicate)) {
            return true;
        } else if (IsEmptyUnsafe(enumerable) || FunctionManipulations.IsAlwaysFalse(predicate)) {
            return false;
        } else {
            try (IEnumerator<T> enumerator = enumerable.GetEnumerator())
            {
                while (enumerator.MoveNext())
                {
                    if (!predicate.predicate(enumerator.getCurrent())) { return false; }
                }
            }
            return true;
        }
    }

    /**
     * Produces the set intersection of two sequences by using the specified {@link IEqualityComparer} to compare values.
     * @param first An {@link IEnumerable} whose distinct elements that also appear in {@code second} will be returned.
     * @param second An {@link IEnumerable} whose distinct elements that also appear in the {@code first} sequence will be returned.
     * @param comparer An {@link IEqualityComparer} to compare values.
     * @return A sequence that contains the elements that form the set intersection of two sequences.
     * @param <T> The type of the elements of the input sequences.
     * @throws ArgumentNullException {@code first} and/or {@code second} are {@code null}.
     */
    @NotNull
    public static <T> IEnumerable<T> Intersect(IEnumerable<T> first, IEnumerable<T> second, @AllowNull IEqualityComparer<T> comparer)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(first, "first");
        ArgumentNullException.ThrowIfNull(second, "second");
        return new IntersectionEnumerable<>(first, second, comparer);
    }

    /**
     * Returns an enumerable that incorporates the element's index into a tuple.
     * @param enumerable The source enumerable providing the elements.
     * @return An enumerable that incorporates each element index into a tuple.
     * @param <T> The type of the elements of {@code enumerable}.
     * @throws ArgumentNullException {@code enumerable} is {@code null}.
     */
    @NotNull
    public static <T> IEnumerable<Tuple2<T, Integer>> Index(IEnumerable<T> enumerable)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(enumerable, "enumerable");
        return IsEmptyUnsafe(enumerable) ? new EmptyEnumerable<>() : new IndexEnumerable<>(enumerable);
    }

    /**
     * Bypasses elements in a sequence as long as a specified condition is true and then returns the remaining elements.
     * @param enumerable An {@link IEnumerable} to return elements from.
     * @param predicate A function to test each element for a condition.
     * @return An {@link IEnumerable} that contains the elements from the input sequence starting at the first element in the linear series that does not pass the test specified by {@code predicate}.
     * @param <T> The type of the elements of {@code enumerable}.
     * @throws ArgumentNullException {@code enumerable} and/or {@code predicate} are {@code null}.
     */
    @NotNull
    public static <T> IEnumerable<T> SkipWhile(IEnumerable<T> enumerable, Predicate<T> predicate)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(predicate, "predicate");
        ArgumentNullException.ThrowIfNull(enumerable, "enumerable");
        if (IsEmptyUnsafe(enumerable) || FunctionManipulations.IsAlwaysTrue(predicate)) {
            return new EmptyEnumerable<>();
        } else if (FunctionManipulations.IsAlwaysFalse(predicate)) {
            return enumerable;
        } else {
            return new SkipWhileSimpleEnumerable<>(enumerable, predicate);
        }
    }

    /**
     * Bypasses elements in a sequence as long as a specified condition is true and then returns the remaining elements.
     * The element's index is used in the logic of the predicate function.
     * @param enumerable An {@link IEnumerable} to return elements from.
     * @param predicate A function to test each source element for a condition; the second parameter of the function represents the index of the source element.
     * @return An {@link IEnumerable} that contains the elements from the input sequence starting at the first element in the linear series that does not pass the test specified by {@code predicate}.
     * @param <T> The type of the elements of {@code enumerable}.
     * @throws ArgumentNullException {@code enumerable} and/or {@code predicate} are {@code null}.
     */
    @NotNull
    public static <T> IEnumerable<T> SkipWhile(IEnumerable<T> enumerable, BiPredicate<T, Integer> predicate)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(predicate, "predicate");
        ArgumentNullException.ThrowIfNull(enumerable, "enumerable");
        if (IsEmptyUnsafe(enumerable) || FunctionManipulations.IsAlwaysTrue(predicate)) {
            return new EmptyEnumerable<>();
        } else if (FunctionManipulations.IsAlwaysFalse(predicate)) {
            return enumerable;
        } else {
            return new SkipWhileWithIndexEnumerable<>(enumerable, predicate);
        }
    }

    /**
     * Stores the specified portion of elements of the current traversable collection to a new instance.
     * @param collection The traversable collection to get the elements from.
     * @param index The starting index to start copying elements from the current collection.
     * @param count The number of elements to copy from the current collection to the new one.
     * @return A new collection containing only the specified portion of elements.
     * @param <T> The type of elements contained in {@code collection}.
     * @throws ArgumentNullException {@code collection} is {@code null}.
     * @throws ArgumentOutOfRangeException {@code index} and/or {@code count} are negative values.
     * @throws ArgumentException {@code index} + {@code count} was exceeding the collection's bounds.
     * @since 1.0.37
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static <T> ITraversableCollection<T> Slice(ITraversableCollection<T> collection, int index, int count)
            throws ArgumentNullException, ArgumentOutOfRangeException, ArgumentException
    {
        ArgumentNullException.ThrowIfNull(collection, "collection");
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be a negative value.");
        } else if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count cannot be a negative value.");
        } else if (count == 0) {
            return new TraversableCollectionSlice.Empty<>();
        } else if (collection instanceof ISupportsSlicing<?> s) {
            return (ITraversableCollection<T>) s.Slice(index, count);
        } else if (((long)index + count - 1L) >= collection.GetCount()) {
            throw new ArgumentException("The specified combination of the index and count parameters are outside of the collections's bounds.");
        } else {
            return new TraversableCollectionSlice<>(collection, index, count);
        }
    }

    /**
     * Provides a way to check whether a given enumerable has all it's elements sorted by a specified sorting order.
     * @param enumerable The enumerable to check.
     * @param comparer The {@link IComparer} instance that can compare two instances of type {@link T}.
     * @param order The sort ordering under which the elements in {@code enuemrable} are expected to be found as.
     * @return A value whether the array is sorted by the specified comparer and selected sorting order.
     * @param <T> The type of elements to compare.
     * @throws ArgumentNullException {@code enumerable} and/or {@code comparer} and/or {@code order} are {@code null}.
     * @throws ArgumentException {@code order} is not of type {@link SortingOrder#ASCENDING} or {@link SortingOrder#DESCENDING}.
     * @since 1.0.37
     * @apiNote This operation has complexity of O(n).
     */
    public static <T> boolean IsSortedBy(IEnumerable<T> enumerable, IComparer<? super T> comparer, SortingOrder order)
            throws ArgumentNullException, ArgumentException
    {
        ArgumentNullException.ThrowIfNull(enumerable, "enumerable");

        SortOrdering<T> ordering = SortOrdering.ConstructFromOrderAndComparer(order, comparer);

        try (IEnumerator<T> enumerator = enumerable.GetEnumerator())
        {
            T before = null, next = null;
            boolean before_assigned = false, after_assigned = false;
            while (enumerator.MoveNext())
            {
                if (before_assigned) {
                    next = enumerator.getCurrent();
                    after_assigned = true;
                } else {
                    before = enumerator.getCurrent();
                    before_assigned = true;
                }
                if (after_assigned)
                {
                    if (ordering.Test(before, next)) {
                        before = next;
                        next = null;
                        after_assigned = false;
                    } else {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Locates an item's index of the specified item in the specified traversable collection.
     * @param collection The traversable collection to run the binary search against.
     * @param item The item to search.
     * @param comparer The comparer to use for comparing values and determining the next step of the binary search.
     * @return The index of {@code item}, or -1 if not found in {@code collection}.
     * @param <T> The type of the item to specify in the {@code item} parameter.
     * @throws ArgumentNullException {@code collection} is {@code null}.
     * @since 1.0.37
     */
    public static <T> int BinarySearch(ITraversableCollection<T> collection, @AllowNull T item, @AllowNull IComparer<? super T> comparer)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(collection, "collection");
        return BinarySearchInternal(collection::GetItem, collection.GetCount(), item, comparer);
    }

    /**
     * Locates an item's index of the specified item in the specified read-only list.
     * @param read_only_list The read-only list to run the binary search against.
     * @param item The item to search.
     * @param comparer The comparer to use for comparing values and determining the next step of the binary search.
     * @return The index of {@code item}, or -1 if not found in {@code read_only_list}.
     * @param <T> The type of the item to specify in the {@code item} parameter.
     * @throws ArgumentNullException {@code read_only_list} is {@code null}.
     * @since 1.0.37
     */
    public static <T> int BinarySearch(IReadOnlyList<T> read_only_list, @AllowNull T item, @AllowNull IComparer<? super T> comparer)
    {
        ArgumentNullException.ThrowIfNull(read_only_list, "read_only_list");
        return BinarySearchInternal(read_only_list::getItem, read_only_list.getCount(), item, comparer);
    }

    /**
     * Locates an item's index of the specified item in the specified list.
     * @param list The list to run the binary search against.
     * @param item The item to search.
     * @param comparer The comparer to use for comparing values and determining the next step of the binary search.
     * @return The index of {@code item}, or -1 if not found in {@code list}.
     * @param <T> The type of the item to specify in the {@code item} parameter.
     * @throws ArgumentNullException {@code list} is {@code null}.
     * @since 1.0.37
     */
    public static <T> int BinarySearch(IList<T> list, @AllowNull T item, @AllowNull IComparer<? super T> comparer)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(list, "list");
        return BinarySearchInternal(list::getItem, list.getCount(), item, comparer);
    }

    private static <T> int BinarySearchInternal(
            PrimitiveIntegerFunction<T> get_item_function,
            int count,
            T item,
            IComparer<? super T> comparer
    ) {
        if (comparer == null) { comparer = Comparer.GetDefault(); }

        int mid, low = 0, high = count - 1;

        while (low <= high)
        {
            mid = low + ((high - low) >>> 1);

            int cr = comparer.Compare(get_item_function.function(mid), item);
            if (cr < 0) {
                low = mid + 1;
            } else if (cr > 0) {
                high = mid - 1;
            } else {
                return mid;
            }
        }

        return -1;
    }

    /**
     * Sorts the two given traversable collections and returns the sorted result into one list.
     * @param first The first traversable collection as input to the sort algorithm.
     * @param second The second traversable collection as input to the sort algorithm.
     * @param order The sorting order to apply for this sort operation.
     * @param comparer The {@link IComparer} instance that is able to compare the values and deduce a sort result.
     * @return The sorted result, stored into a list object.
     * @param <T> The type of objects to be sorted.
     * @throws ArgumentNullException {@code first} and/or {@code second} and/or {@code order} and/or {@code comparer} are {@code null}.
     * @apiNote This sorting algorithm has complexity of O(n log n), if assuming that the enumerators move and get the next element from the collection in O(1) time.
     * @since 1.0.37
     */
    @NotNull
    public static <T> IList<T> MergeSort(
            ITraversableCollection<T> first,
            ITraversableCollection<T> second,
            SortingOrder order,
            IComparer<? super T> comparer
    ) throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(first, "first");
        ArgumentNullException.ThrowIfNull(second, "second");
        return MergeSort.Algorithm(
                first,
                second,
                SortOrdering.ConstructFromOrderAndComparer(order, comparer)
        );
    }

    /**
     * Sorts the given traversable collection and returns the result as a list.
     * @param collection The traversable collection to sort.
     * @param order The sorting order to apply for this sort operation.
     * @param comparer The {@link IComparer} instance that is able to compare the values and deduce a sort result.
     * @return The sorted result, stored into a list object.
     * @param <T> The type of objects to be sorted.
     * @throws ArgumentNullException {@code collection} and/or {@code order} and/or {@code comparer} are {@code null}.
     * @apiNote This sorting algorithm has complexity of O(n log n), if assuming that the enumerators move and get the next element from the collection in O(1) time.
     * @since 1.0.37
     */
    @NotNull
    public static <T> IList<T> MergeSort(
            ITraversableCollection<T> collection,
            SortingOrder order,
            IComparer<? super T> comparer
    ) throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(collection, "collection");
        return MergeSort.Algorithm(
                collection,
                SortOrdering.ConstructFromOrderAndComparer(order, comparer)
        );
    }

    /**
     * Sorts the given list.
     * The list is sorted directly.
     * @param list The list to sort its elements.
     * @param order The sorting order to apply for this sort operation.
     * @param comparer The {@link IComparer} instance that is able to compare the values and deduce a sort result.
     * @param index Target index in {@code list} to begin sorting from.
     * @param count Number of elements in {@code list} to sort.
     * @param <T> The type of objects to be sorted.
     * @throws ArgumentNullException {@code list} and/or {@code order} and/or {@code comparer} are {@code null}.
     * @throws ArgumentOutOfRangeException {@code index} and/or {@code count} are negative values.
     * @throws ArgumentException {@code index + count} does exceed the list's bounds.
     * @apiNote Unlike MergeSort, this algorithm does in-place sort of the given list, which it might be faster in some cases,
     * since no new lists are allocated. <br />
     * Its complexity is O(n log n), if assuming that the {@link IList#getItem(int)} and {@link IList#setItem(int, Object)} calls have a complexity of O(1).
     * @since 1.0.37
     */
    public static <T> void QuickSort(
            IList<T> list,
            SortingOrder order,
            IComparer<? super T> comparer,
            int index,
            int count
    ) throws ArgumentNullException, ArgumentOutOfRangeException, ArgumentException
    {
        ArgumentNullException.ThrowIfNull(list, "list");
        if (index < 0) {
            throw new ArgumentOutOfRangeException("index", "Index cannot be less than 0.");
        } else if (count < 0) {
            throw new ArgumentOutOfRangeException("count", "Count cannot be less than 0.");
        } else if (Math.addExact(index, count) > list.getCount()) {
            throw new ArgumentException("Index and Count parameter values are exceeding the bounds of the list.");
        } else {
            QuickSort.Algorithm(
                    list,
                    index,
                    index + count - 1,
                    SortOrdering.ConstructFromOrderAndComparer(order, comparer)
            );
        }
    }

    /**
     * Sorts the given list.
     * The list is sorted directly.
     * @param list The list to sort its elements.
     * @param order The sorting order to apply for this sort operation.
     * @param comparer The {@link IComparer} instance that is able to compare the values and deduce a sort result.
     * @param <T> The type of objects to be sorted.
     * @throws ArgumentNullException {@code list} and/or {@code order} and/or {@code comparer} are {@code null}.
     * @apiNote Unlike MergeSort, this algorithm does in-place sort of the given list, which it might be faster in some cases,
     * since no new lists are allocated. <br />
     * Its complexity is O(n log n), if assuming that the {@link IList#getItem(int)} and {@link IList#setItem(int, Object)} calls have a complexity of O(1).
     * @since 1.0.37
     */
    public static <T> void QuickSort(
            IList<T> list,
            SortingOrder order,
            IComparer<? super T> comparer
    ) throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(list, "list");
        QuickSort.Algorithm(
                list,
                SortOrdering.ConstructFromOrderAndComparer(order, comparer)
        );
    }
}
