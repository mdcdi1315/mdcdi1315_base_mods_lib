package com.github.mdcdi1315.basemodslib.utils.collections.helpers.sorting;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IList;

/*
    This code was based on .NET's ArraySortHelper HeapSort code.
 */

public final class HeapSort
{
    private HeapSort() { }

    public static <T> void Algorithm(IList<T> list, SortOrdering<T> ordering)
    {
        int count = list.getCount();
        if (count < 2) { return; }

        for (int i = count >> 1; i >= 1; i--)
        {
            DownHeap(list, i, count, ordering);
        }

        T item;
        for (int i = count; i > 1; i--)
        {
            // Swap the first element with the last one.
            item = list.getItem(0);
            list.setItem(0, list.getItem(i - 1));
            list.setItem(i - 1, item);

            DownHeap(list, 1, i - 1, ordering);
        }
    }

    private static <T> void DownHeap(IList<T> list, int i, int n, SortOrdering<T> ordering)
    {
        T child_item;
        T d = list.getItem(i - 1);
        while (i <= n >> 1)
        {
            int child = 2 * i;
            if (child < n && ordering.Test(list.getItem(child - 1), list.getItem(child)))
            {
                child++;
            }

            child_item = list.getItem(child - 1);

            if (ordering.Test(d, child_item)) {
                list.setItem(i - 1, child_item);
                i = child;
            } else {
                break;
            }
        }

        list.setItem(i - 1, d);
    }
}
