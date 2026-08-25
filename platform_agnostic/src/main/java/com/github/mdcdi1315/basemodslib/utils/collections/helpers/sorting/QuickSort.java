package com.github.mdcdi1315.basemodslib.utils.collections.helpers.sorting;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IList;

public final class QuickSort
{
    private QuickSort() {}

    public static <T> void Algorithm(IList<T> to_sort, SortOrdering<T> ordering)
    {
        Algorithm(
                to_sort,
                0,
                to_sort.getCount() - 1,
                ordering
        );
    }

    public static <T> void Algorithm(
            IList<T> to_sort,
            int lower_bound,
            int upper_bound,
            SortOrdering<T> ordering
    ) {
        if (lower_bound >= upper_bound)  { return; }
        int G = Partition(to_sort, lower_bound, upper_bound, ordering);
        Algorithm(to_sort, lower_bound, G-1, ordering);
        Algorithm(to_sort, G+1, upper_bound, ordering);
    }

    private static <T> int Partition(IList<T> to_sort, int low, int high, SortOrdering<T> ordering)
    {
        T old_item, item;
        T pivot = to_sort.getItem(high);
        int I = low - 1;
        for (int J = low; J < high; J++)
        {
            item = to_sort.getItem(J);
            if (ordering.Test(item, pivot))
            {
                I++;
                old_item = to_sort.getItem(I);
                to_sort.setItem(I, item);
                to_sort.setItem(J, old_item);
            }
        }
        I++;
        item = to_sort.getItem(high);
        old_item = to_sort.getItem(I);
        to_sort.setItem(I, item);
        to_sort.setItem(high, old_item);
        return I;
    }
}
