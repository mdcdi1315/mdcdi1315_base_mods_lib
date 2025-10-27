package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.Action2;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

public record Action2ToRunnable<T1, T2>(Action2<T1 , T2> action_2, @MaybeNull T1 input_1, @MaybeNull T2 input_2)
    implements Runnable
{
    public Action2ToRunnable {
        ArgumentNullException.ThrowIfNull(action_2, "action_2");
    }

    @Override
    public void run() {
        action_2.action(input_1, input_2);
    }
}
