package com.github.mdcdi1315.basemodslib.config.lowlevelapi;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.List;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import java.util.Arrays;
import java.util.Iterator;

public final class ArrayValue
    implements Iterable<Object>
{
    private final Object[] array_final;

    private ArrayValue(Object[] arr) {
        array_final = arr;
    }

    public static ArrayValue FromListDirectly(java.util.List<?> list) {
        return new ArrayValue(list.toArray());
    }

    public static final class Builder
    {
        private List<Object> object_list;

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
            return new ArrayValue(object_list.ToArray());
        }
    }

    public int GetCount() {
        return array_final.length;
    }

    @Override
    public @NotNull Iterator<Object> iterator() {
        return Arrays.stream(array_final).iterator();
    }
}
