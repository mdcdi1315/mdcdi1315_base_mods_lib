package com.github.mdcdi1315.basemodslib.eventapi.mods;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.eventapi.IDestroyableEvent;

import java.util.concurrent.ConcurrentLinkedDeque;

import org.jetbrains.annotations.ApiStatus;

/**
 * Provides the base implementation class for setup events. <br />
 * Additional setup events can be specified by the user by deriving from this class.
 * @since 1.0.7
 */
public abstract class AbstractSetupEvent
    implements IDestroyableEvent
{
    private final ConcurrentLinkedDeque<Runnable> runnable_queue;

    /**
     * Initializes a new instance of the {@link AbstractSetupEvent} class.
     */
    public AbstractSetupEvent() {
        runnable_queue = new ConcurrentLinkedDeque<>();
    }

    /**
     * Executes all the {@link Runnable}s provided in the event. <br />
     * This is intended to be called by the mod loader and should not be called by your code.
     */
    @ApiStatus.Internal
    public void Run()
    {
        Runnable r;
        while ((r = runnable_queue.pollFirst()) != null) {
            try {
                r.run();
            } catch (Exception e) {
                BaseModsLib.LOGGER.warn("BASEMODSLIB: One of the runnables provided has thrown an exception.", e);
            }
        }
    }

    /**
     * Enqueues an action to execute once the event starts processing.
     * @param work The work to execute.
     * @throws ArgumentNullException {@code work} is {@code null}.
     */
    public void EnqueueWork(Runnable work)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(work, "work");
        runnable_queue.add(work);
    }
}
