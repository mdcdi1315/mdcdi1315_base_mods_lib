package com.github.mdcdi1315.basemodslib.commands;

import com.github.mdcdi1315.DotNetLayer.System.Action2;

import com.github.mdcdi1315.basemodslib.utils.annotations.APIStatus;

import net.minecraft.commands.CommandSourceStack;

/**
 * Provides the method that is executing to the {@link java.util.concurrent.ForkJoinPool},
 * on behalf of an {@link AbstractCommand}. <br />
 * This is used for offloading the server thread.
 * @param <TState> Additional state data that may be required for the execution of the command.
 * @since 1.0.40-beta7
 */
@APIStatus(Status = APIStatus.Status.STABLE, Since = "1.0.40-beta7")
public interface OffloadedCommandTask<TState> extends Action2<CommandSourceStack, TState> { }
