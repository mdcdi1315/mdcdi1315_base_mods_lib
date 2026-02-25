package com.github.mdcdi1315.basemodslib.eventapi.mods;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.eventapi.IDestroyableEvent;
import com.github.mdcdi1315.basemodslib.utils.collections.SingleLinkedListBasedQueue;

import org.jetbrains.annotations.ApiStatus;

/**
 * Provides the base implementation class for setup events. <br />
 * Additional setup events can be specified by the user by deriving from this class.
 * @since 1.0.7
 */
public abstract class AbstractSetupEvent
    implements IDestroyableEvent
{
    private final SingleLinkedListBasedQueue<Runnable> runnable_queue;

    /**
     * Initializes a new instance of the {@link AbstractSetupEvent} class.
     */
    public AbstractSetupEvent() { runnable_queue = new SingleLinkedListBasedQueue<>(); }

    /**
     * Executes all the {@link Runnable}s provided in the event. <br />
     * This is intended to be called by the mod loader and should not be called by your code.
     */
    @ApiStatus.Internal
    public void Run()
    {
        Runnable r;
        while ((r = DequeueItem()) != null)
        {
            try {
                r.run();
            } catch (Exception e) {
                BaseModsLib.LOGGER.warn("BASEMODSLIB: One of the runnables provided has thrown an exception.", e);
            }
        }
    }

    // Dequeues an item from the event queue, ensuring that the thread that is dispatching the Run
    // call has the lock on the queue. If needed, the method will wait until a new work item has been registered on the queue.
    private Runnable DequeueItem()
    {
        Runnable r;
        synchronized (runnable_queue) { r = runnable_queue.TryDequeue(); }
        return r;
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
        synchronized (runnable_queue) { runnable_queue.Enqueue(work); }
    }
}
