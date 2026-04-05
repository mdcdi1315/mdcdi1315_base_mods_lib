package com.github.mdcdi1315.DotNetLayer.System.Collections.ObjectModel;

import com.github.mdcdi1315.DotNetLayer.System.NotSupportedException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IList;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IReadOnlyList;

/**
 * Provides the base class for a generic read-only collection.
 * @param <T> The type of elements in the collection.
 */
public class ReadOnlyCollection<T>
    implements IList<T>, IReadOnlyList<T>
{
    private final IList<T> list; // Do not rename (binary serialization)

    /**
     * Initializes a new instance of the {@link ReadOnlyCollection} class that is a read-only wrapper around the specified list.
     * @param list The list to wrap.
     * @throws ArgumentNullException {@code list} is {@code null}.
     */
    public ReadOnlyCollection(IList<T> list)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(this.list = list, "list");
    }

    private static final class EmptyList<T>
        implements IList<T>
    {
        @Override
        public T getItem(int index) { return null; }

        @Override
        public void setItem(int index, T item) { }

        @Override
        public int IndexOf(T item) { return -1; }

        @Override
        public void RemoveAt(int index) {}

        @Override
        public void Insert(int index, T item) {}

        @Override
        public int getCount() { return 0; }

        @Override
        public boolean getIsReadOnly() { return true; }

        @Override
        public void Add(T item) { }

        @Override
        public void Clear() {}

        @Override
        public boolean Contains(T item) { return false; }

        @Override
        public void CopyTo(T[] array, int arrayIndex) {}

        @Override
        public boolean Remove(T item) { return false; }

        @Override
        public IEnumerator<T> GetEnumerator() { return new EmptyEnumerator<>(); }

        private record EmptyEnumerator<T>()
            implements IEnumerator<T>
        {
            @Override
            public T getCurrent() { return null; }

            @Override
            public boolean MoveNext() throws InvalidOperationException { return false; }

            @Override
            public void Reset() throws InvalidOperationException {}

            @Override
            public void Dispose() {}
        }
    }

    /**
     * Gets an empty {@link ReadOnlyCollection}.
     * @return An empty {@link ReadOnlyCollection}.
     * @param <T> The type of elements in the collection.
     * @apiNote The returned instance is immutable and will always be empty.
     */
    @NotNull
    public static <T> ReadOnlyCollection<T> GetEmpty() { return new ReadOnlyCollection<>(new EmptyList<>()); }

    protected IList<T> GetItems() { return list; }

    @Override
    public boolean getIsReadOnly() { return true; }

    @Override
    public int getCount() { return list.getCount(); }

    @Override
    public int IndexOf(T item) { return list.IndexOf(item); }

    @Override
    public T getItem(int index) { return list.getItem(index); }

    @Override
    public IEnumerator<T> GetEnumerator() { return list.GetEnumerator(); }


    @Override
    public void setItem(int index, T item)
            throws NotSupportedException
    {
        throw new NotSupportedException("This collection is read-only and cannot be modified.");
    }

    @Override
    public void Insert(int index, T item) {
        throw new NotSupportedException("This collection is read-only and cannot be modified.");
    }

    @Override
    public void RemoveAt(int index) {
        throw new NotSupportedException("This collection is read-only and cannot be modified.");
    }

    @Override
    public void Add(T item) {
        throw new NotSupportedException("This collection is read-only and cannot be modified.");
    }

    @Override
    public void Clear() {
        throw new NotSupportedException("This collection is read-only and cannot be modified.");
    }

    @Override
    public boolean Contains(T item) { return list.Contains(item); }

    @Override
    public void CopyTo(T[] array, int arrayIndex) { list.CopyTo(array, arrayIndex); }

    @Override
    public boolean Remove(T item) {
        throw new NotSupportedException("This collection is read-only and cannot be modified.");
    }
}
