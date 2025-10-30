package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.Action3;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

public record Action3ToRunnable<T1, T2, T3>(Action3<T1, T2, T3> action, @AllowNull T1 input_1, @AllowNull T2 input_2, @AllowNull T3 input_3)
    implements Runnable
{
    public Action3ToRunnable {
        ArgumentNullException.ThrowIfNull(action, "action");
    }

    @Override
    public void run() {
        action.action(input_1, input_2, input_3);
    }
}
