package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.Action2;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import java.util.function.BiConsumer;

public record Action2ToBiConsumer<T1, T2>(Action2<T1, T2> action)
    implements BiConsumer<T1 , T2>
{
    public Action2ToBiConsumer {
        ArgumentNullException.ThrowIfNull(action, "action");
    }

    @Override
    public void accept(T1 t1, T2 t2) {
        action.action(t1, t2);
    }
}
