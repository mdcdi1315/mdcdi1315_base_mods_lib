package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerator;
import com.github.mdcdi1315.basemodslib.utils.collections.IPriorityQueue;

public final class PriorityQueue_AllPriorityBanksDefaultEnumerator<T>
    extends BaseEnumerator<T>
{
    private int current_priority;
    private IEnumerator<T> current_enumerator;
    private final IPriorityQueue<T> the_queue;

    public PriorityQueue_AllPriorityBanksDefaultEnumerator(IPriorityQueue<T> the_queue)
    {
        current_priority = -1;
        current_enumerator = null;
        this.the_queue = the_queue;
    }

    private boolean GetNextPriorityBank()
    {
        // Loop through the priority banks.
        // See below for the reason why we are looping through them.
        while (++current_priority < the_queue.GetPriorityCount())
        {
            DisposeCurrentEnumerator();
            current_enumerator = the_queue.GetEnumerator(current_priority);
            // There might be the case that a priority bank might be empty.
            // If that is the case, just dispose the current enumerator and move on to the next one,
            // if we have a priority bank available.
            if (current_enumerator.MoveNext()) { return true; }
        }

        // We do not have any avaliable priority banks, so give up.
        // Note also that we do not dispose the currently allocated enumerator - if there is one,
        // that would be done in Dispose method call time.
        return false;
    }

    @Override
    public T getCurrent()
    {
        if (current_enumerator == null) {
            throw new InvalidOperationException("Enumeration has not yet begun.");
        } else {
            return current_enumerator.getCurrent();
        }
    }

    @Override
    protected void ResetImpl()
            throws InvalidOperationException
    {
        current_priority = -1;
        DisposeCurrentEnumerator();
    }

    @Override
    protected boolean MoveNextImpl()
            throws InvalidOperationException
    {
        if (current_enumerator == null) {
            return GetNextPriorityBank();
        } else if (current_enumerator.MoveNext()) {
            return true;
        } else {
            return GetNextPriorityBank();
        }
    }

    private void DisposeCurrentEnumerator()
    {
        if (current_enumerator != null)
        {
            current_enumerator.Dispose();
            current_enumerator = null;
        }
    }

    @Override
    public void Dispose()
    {
        synchronized (this)
        {
            super.Dispose();
            DisposeCurrentEnumerator();
        }
    }
}
