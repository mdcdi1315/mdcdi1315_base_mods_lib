package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.*;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.utils.EmptyEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.helpers.*;
import com.github.mdcdi1315.basemodslib.utils.function.BiPredicate;
import com.github.mdcdi1315.basemodslib.utils.function.FunctionManipulations;
import com.github.mdcdi1315.basemodslib.utils.JavaObjectEqualsEqualityComparer;

import java.util.Collection;

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
     *         The only method that is not supported by the returned wrapper is the {@link java.util.Collection#retainAll(Collection)} method.
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
        if (enumerable instanceof ISupportsDirectConversionTo<TS> c_supported) {
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
        } else if (FunctionManipulations.IsAlwaysFalse(predicate)) {
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
        return new ConcatenatingEnumerable<>(first, second);
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

        if (list_inst instanceof IArrayBasedCollection ac && enumerable instanceof ITraversableCollection<T> t) { ac.EnsureCapacity(t.GetCount()); }

        IEnumerator<T> enumerator = enumerable.GetEnumerator();
        try {
            while (enumerator.MoveNext()) {
                list_inst.Add(enumerator.getCurrent());
            }
        } finally {
            enumerator.Dispose();
        }

        return list_inst;
    }

    /**
     * Generates a sequence of integral numbers within a specified range.
     * @param start The value of the first integer in the sequence.
     * @param count The number of sequential integers to generate.
     * @return An {@link IEnumerable} that contains a range of sequential integral numbers.
     * @throws ArgumentOutOfRangeException {@code count} is less than 0.
     */
    @NotNull
    public static IEnumerable<Integer> Range(int start, int count)
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
    @SuppressWarnings("IfCanBeSwitch")
    public static <T> boolean IsEmpty(IEnumerable<T> enumerable)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(enumerable, "enumerable");
        if (enumerable instanceof EmptyEnumerable<T>) {
            return true;
        } else if (enumerable instanceof ITraversableCollection<T> t) {
            return t.GetCount() == 0;
        } else if (enumerable instanceof ICollection<T> c) {
            return c.getCount() == 0;
        } else {
            IEnumerator<T> en = enumerable.GetEnumerator();
            try {
                return !en.MoveNext();
            } finally {
                en.Dispose();
            }
        }
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
    @SuppressWarnings("IfCanBeSwitch")
    public static <T> boolean Any(IEnumerable<T> enumerable)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(enumerable, "enumerable");
        if (enumerable instanceof EmptyEnumerable<T>) {
            return false;
        } else if (enumerable instanceof ITraversableCollection<T> t) {
            return t.GetCount() > 0;
        } else if (enumerable instanceof ICollection<T> c) {
            return c.getCount() > 0;
        } else {
            IEnumerator<T> en = enumerable.GetEnumerator();
            try {
                return en.MoveNext();
            } finally {
                en.Dispose();
            }
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
        } else {
            IEnumerator<T> enumerator = enumerable.GetEnumerator();

            try {
                while (enumerator.MoveNext()) {
                    if (predicate.predicate(enumerator.getCurrent())) { return true; }
                }
            } finally {
                enumerator.Dispose();
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
        if (comparer == null) {
            comparer = new JavaObjectEqualsEqualityComparer<>();
        }

        IEnumerator<T> enumerator = enumerable.GetEnumerator();

        try {
            while (enumerator.MoveNext()) {
                if (comparer.Equals(enumerator.getCurrent(), value)) { return true; }
            }
            return false;
        } finally {
            enumerator.Dispose();
        }
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
        IEnumerator<T> enumerator = enumerable.GetEnumerator();
        try {
            while (enumerator.MoveNext()) { action.action(enumerator.getCurrent()); }
        } finally {
            enumerator.Dispose();
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

        IEnumerator<T> enumerator = enumerable.GetEnumerator();

        try {
            while (enumerator.MoveNext()) { seed = func.function(seed, enumerator.getCurrent()); }
        } finally {
            enumerator.Dispose();
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
        } else if (FunctionManipulations.IsAlwaysFalse(predicate)) {
            return false;
        } else {
            IEnumerator<T> enumerator = enumerable.GetEnumerator();

            try {
                while (enumerator.MoveNext()) {
                    if (!predicate.predicate(enumerator.getCurrent())) { return false; }
                }
            } finally {
                enumerator.Dispose();
            }

            return true;
        }
    }

    /**
     * Produces the set intersection of two sequences by using the specified {@link IEqualityComparer} to compare values.
     * @param first An {@link IEnumerable} whose distinct elements that also appear in second will be returned.
     * @param second An {@link IEnumerable} whose distinct elements that also appear in the first sequence will be returned.
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
        return new IndexEnumerable<>(enumerable);
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
        if (FunctionManipulations.IsAlwaysTrue(predicate)) {
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
        if (FunctionManipulations.IsAlwaysTrue(predicate)) {
            return new EmptyEnumerable<>();
        } else if (FunctionManipulations.IsAlwaysFalse(predicate)) {
            return enumerable;
        } else {
            return new SkipWhileWithIndexEnumerable<>(enumerable, predicate);
        }
    }
}
