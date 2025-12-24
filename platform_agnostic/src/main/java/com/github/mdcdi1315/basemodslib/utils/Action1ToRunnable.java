package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

/**
 * Translates an {@link Action1} functional interface to a {@link Runnable} interface.
 * @param action The action to translate.
 * @param input The input parameter to pass to the action, once {@link #run()} is called.
 * @param <T> The parameter type that the {@link Action1} requires.
 */
public record Action1ToRunnable<T>(@NotNull Action1<T> action , @AllowNull T input)
    implements Runnable
{
    /**
     * Creates a new instance of the {@link Action1ToRunnable} class.
     * @param action The action to translate.
     * @param input The input parameter to pass to the action, once {@link #run()} is called.
     * @throws ArgumentNullException {@code action} is {@code null}.
     */
    public Action1ToRunnable {
        ArgumentNullException.ThrowIfNull(action, "action");
    }

    /**
     * Calls in the action, passing as an input parameter the contents of the {@link #input} field.
     */
    @Override
    public void run() { action.action(input); }
}
