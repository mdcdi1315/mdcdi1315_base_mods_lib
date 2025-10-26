package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.Action2;

import java.util.function.BiConsumer;

public record Action2ToBiConsumer<T1, T2>(Action2<T1, T2> underlying)
    implements BiConsumer<T1 , T2>
{
    @Override
    public void accept(T1 t1, T2 t2) {
        underlying.action(t1, t2);
    }
}
