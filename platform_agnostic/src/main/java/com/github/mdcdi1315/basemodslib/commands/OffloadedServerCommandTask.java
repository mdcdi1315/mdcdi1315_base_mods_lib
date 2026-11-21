package com.github.mdcdi1315.basemodslib.commands;

import com.github.mdcdi1315.DotNetLayer.ExceptionTranslator;

import net.minecraft.commands.CommandSourceStack;

import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RunnableFuture;

final class OffloadedServerCommandTask<TS>
    extends ForkJoinTask<Byte>
    implements RunnableFuture<Byte>
{
    private Byte value;
    private final TS state;
    private final CommandSourceStack stack;
    private final OffloadedCommandTask<TS> executor;

    public OffloadedServerCommandTask(CommandSourceStack stack, OffloadedCommandTask<TS> executor, TS state)
    {
        value = null;
        this.state = state;
        this.stack = stack;
        this.executor = executor;
    }

    @Override
    public Byte getRawResult() { return value; }

    @Override
    protected void setRawResult(Byte value) { this.value = value; }

    @Override
    protected boolean exec()
    {
        try {
            executor.action(stack, state);
            value = 0;
            return true;
        } catch (Throwable t) {
            throw ExceptionTranslator.TranslateException(t);
        }
    }

    @Override
    public void run() { invoke(); }

    @Override
    public String toString() { return "[Offloaded server command task: " + executor + " ]"; }
}
