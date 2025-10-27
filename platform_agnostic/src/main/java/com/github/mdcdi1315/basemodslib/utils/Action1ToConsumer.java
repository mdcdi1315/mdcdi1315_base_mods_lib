package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import java.util.function.Consumer;

public record Action1ToConsumer<T>(Action1<T> action)
    implements Consumer<T>
{
    public Action1ToConsumer {
        ArgumentNullException.ThrowIfNull(action, "action");
    }

    @Override
    public void accept(T t) {
        action.action(t);
    }
}
