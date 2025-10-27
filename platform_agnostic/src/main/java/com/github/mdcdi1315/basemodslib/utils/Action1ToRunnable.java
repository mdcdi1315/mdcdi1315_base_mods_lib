package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

public record Action1ToRunnable<T>(Action1<T> action , @MaybeNull T input)
    implements Runnable
{
    public Action1ToRunnable {
        ArgumentNullException.ThrowIfNull(action, "action");
    }

    @Override
    public void run() {
        action.action(input);
    }
}
