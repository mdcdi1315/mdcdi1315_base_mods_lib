package com.github.mdcdi1315.basemodslib.utils.collections.helpers.sorting;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.StackTraceHidden;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IComparer;

import com.github.mdcdi1315.basemodslib.utils.collections.SortingOrder;

@FunctionalInterface
public interface SortOrdering<T>
{
    boolean Test(T item_1, T item_2);

    public record Ascending<T>(IComparer<? super T> comparer)
            implements SortOrdering<T>
    {
        @Override
        public boolean Test(T item_1, T item_2) { return comparer.Compare(item_1, item_2) <= 0; }
    }

    public record Descending<T>(IComparer<? super T> comparer)
        implements SortOrdering<T>
    {
        @Override
        public boolean Test(T item_1, T item_2) { return comparer.Compare(item_1, item_2) >= 0; }
    }

    @StackTraceHidden
    public static <T> SortOrdering<T> ConstructFromOrderAndComparer(
            SortingOrder order,
            IComparer<? super T> comparer
    ) throws ArgumentNullException, ArgumentException
    {
        ArgumentNullException.ThrowIfNull(order, "order");
        ArgumentNullException.ThrowIfNull(comparer, "comparer");
        if (order == SortingOrder.ASCENDING) {
            return new Ascending<>(comparer);
        } else if (order == SortingOrder.DESCENDING) {
            return new Descending<>(comparer);
        } else {
            throw new ArgumentException("Sorting order must be one of the ASCENDING or DESCENDING values.", "order");
        }
    }
}
