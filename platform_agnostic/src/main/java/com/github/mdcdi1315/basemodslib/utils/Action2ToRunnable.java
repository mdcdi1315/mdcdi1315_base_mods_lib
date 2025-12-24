package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.Action2;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

/**
 * Translates an {@link Action2} functional interface to a {@link Runnable} interface.
 * @param action_2 The action to translate.
 * @param input_1 The first input parameter to pass to the action, once {@link #run()} is called.
 * @param input_2 The second input parameter to pass to the action, once {@link #run()} is called.
 * @param <T1> The type of the first parameter that the {@link Action2} requires.
 * @param <T2> The type of the second parameter that the {@link Action2} requires.
 */
public record Action2ToRunnable<T1, T2>(@NotNull Action2<T1 , T2> action_2, @AllowNull T1 input_1, @AllowNull T2 input_2)
    implements Runnable
{
    /**
     * Creates a new instance of the {@link Action2ToRunnable} class.
     * @param action_2 The action to translate.
     * @param input_1 The first input parameter to pass to the action, once {@link #run()} is called.
     * @param input_2 The second input parameter to pass to the action, once {@link #run()} is called.
     * @throws ArgumentNullException {@code action_2} is {@code null}.
     */
    public Action2ToRunnable {
        ArgumentNullException.ThrowIfNull(action_2, "action_2");
    }

    /**
     * Calls in the action, passing as input parameters the contents of the {@link #input_1} and {@link #input_2} fields.
     */
    @Override
    public void run() { action_2.action(input_1, input_2); }
}
