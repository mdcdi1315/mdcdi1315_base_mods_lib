package com.github.mdcdi1315.basemodslib.config.lowlevelapi;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import java.util.Iterator;

public final class ArrayValue
    implements Iterable<Object>
{
    private final Object[] array_final;

    private static class ArrayValueIterator
        implements Iterator<Object>
    {
        private final Object[] array;
        private int index;

        public ArrayValueIterator(Object[] arr)
        {
            array = arr;
            index = -1;
        }

        @Override
        public boolean hasNext() {
            return ++index < array.length;
        }

        @Override
        public Object next() {
            return array[index];
        }
    }

    private ArrayValue(Object[] arr) {
        array_final = arr;
    }

    public static ArrayValue FromListDirectly(java.util.List<?> list) {
        return new ArrayValue(list.toArray());
    }

    public static ArrayValue CreateEmpty() { return new ArrayValue(new Object[0]); }

    public static final class Builder
    {
        private final List<Object> object_list;

        public Builder() {
            object_list = new List<>(15);
        }

        public Builder(int initial_capacity) {
            object_list = new List<>(initial_capacity);
        }

        public Builder Add(Object o) {
            object_list.Add(o);
            return this;
        }

        public ArrayValue Build() {
            Object[] arr = new Object[object_list.getCount()];
            object_list.CopyTo(arr , 0);
            return new ArrayValue(arr);
        }
    }

    public int GetCount() {
        return array_final.length;
    }

    public boolean IsEmpty() { return array_final.length == 0; }

    @Override
    public @NotNull Iterator<Object> iterator() {
        return new ArrayValueIterator(array_final);
    }
}
