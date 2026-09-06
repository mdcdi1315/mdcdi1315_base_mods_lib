package com.github.mdcdi1315.basemodslib.utils.collections.helpers.sorting;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.basemodslib.utils.collections.*;

// Merge sort algorithm of complexity O(n log n).
public final class MergeSort
{
    private MergeSort() {}

    public static <T> FixedArrayBasedList<T> Algorithm(ITraversableCollection<T> to_sort, SortOrdering<T> ordering) { return Algorithm(to_sort, new SingleLinkedList<>(), ordering); }

    @SuppressWarnings("unchecked")
    public static <T> FixedArrayBasedList<T> Algorithm(
            ITraversableCollection<T> left,
            ITraversableCollection<T> right,
            SortOrdering<T> ordering
    ) {
        int left_count = left.GetCount();
        int right_count = right.GetCount();

        ITraversableCollection<T> first_part, second_part;

        if (left_count < 2) {
            first_part = left;
        } else {
            int mid_1 = left_count / 2;
            first_part = Algorithm(
                    (ITraversableCollection<T>)((ISupportsSlicing<T>)left).Slice(0, mid_1),
                    (ITraversableCollection<T>)((ISupportsSlicing<T>)left).Slice(mid_1, left_count - mid_1),
                    ordering
            );
        }

        if (right_count < 2) {
            second_part = right;
        } else {
            int mid_2 = right_count / 2;
            second_part = Algorithm(
                    (ITraversableCollection<T>)((ISupportsSlicing<T>)right).Slice(0, mid_2),
                    (ITraversableCollection<T>)((ISupportsSlicing<T>)right).Slice(mid_2, right_count - mid_2),
                    ordering
            );
        }

        return Merge(first_part, second_part, ordering);
    }

    private static <T> FixedArrayBasedList<T> Merge(
            ITraversableCollection<T> left,
            ITraversableCollection<T> right,
            SortOrdering<T> ordering
    ) {
        T item_left = null, item_right = null;
        FixedArrayBasedList<T> result = new FixedArrayBasedList<>(left.GetCount() + right.GetCount());

        try (IEnumerator<T> en_left = left.GetEnumerator(); IEnumerator<T> en_right = right.GetEnumerator())
        {
            boolean has_left = false, has_right = false;
            boolean check_left = true, check_right = true;
            while (check_left || check_right)
            {
                if (check_left && !has_left)
                {
                    if (en_left.MoveNext()) {
                        item_left = en_left.getCurrent();
                        has_left = true;
                    } else {
                        check_left = false;
                    }
                }

                if (check_right && !has_right)
                {
                    if (en_right.MoveNext()) {
                        item_right = en_right.getCurrent();
                        has_right = true;
                    } else {
                        check_right = false;
                    }
                }

                if (has_left && has_right) {
                    if (ordering.Test(item_left, item_right)) {
                        result.Add(item_left);
                        has_left = false;
                    } else {
                        result.Add(item_right);
                        has_right = false;
                    }
                } else if (has_left) {
                    result.Add(item_left);
                    has_left = false;
                } else if (has_right) {
                    result.Add(item_right);
                    has_right = false;
                }
            }
        }
        return result;
    }
}
