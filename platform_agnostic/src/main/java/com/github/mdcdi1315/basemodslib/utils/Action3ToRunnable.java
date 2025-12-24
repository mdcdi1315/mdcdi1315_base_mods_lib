package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.Action3;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

/**
 * Translates an {@link Action3} functional interface to a {@link Runnable} interface.
 * @param action The action to translate.
 * @param input_1 The first input parameter to pass to the action, once {@link #run()} is called.
 * @param input_2 The second input parameter to pass to the action, once {@link #run()} is called.
 * @param input_3 The third input parameter to pass to the action, once {@link #run()} is called.
 * @param <T1> The type of the first parameter that the {@link Action3} requires.
 * @param <T2> The type of the second parameter that the {@link Action3} requires.
 * @param <T3> The type of the third parameter that the {@link Action3} requires.
 */
public record Action3ToRunnable<T1, T2, T3>(@NotNull Action3<T1, T2, T3> action, @AllowNull T1 input_1, @AllowNull T2 input_2, @AllowNull T3 input_3)
    implements Runnable
{
    /**
     * Creates a new instance of the {@link Action2ToRunnable} class.
     * @param action The action to translate.
     * @param input_1 The first input parameter to pass to the action, once {@link #run()} is called.
     * @param input_2 The second input parameter to pass to the action, once {@link #run()} is called.
     * @param input_3 The third input parameter to pass to the action, once {@link #run()} is called.
     * @throws ArgumentNullException {@code action} is {@code null}.
     */
    public Action3ToRunnable {
        ArgumentNullException.ThrowIfNull(action, "action");
    }

    /**
     * Calls in the action, passing as input parameters the contents of the {@link #input_1}, {@link #input_2} and {@link #input_3} fields.
     */
    @Override
    public void run() {
        action.action(input_1, input_2, input_3);
    }
}
