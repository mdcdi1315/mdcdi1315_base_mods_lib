package com.github.mdcdi1315.basemodslib.utils.random;

import com.github.mdcdi1315.basemodslib.utils.ISynchronized;

import java.util.concurrent.locks.ReentrantLock;

final class ThreadSafeRandomSourceWrapper
        implements IRandomSource, ISynchronized
{
    private final ReentrantLock lock;
    private final IRandomSource wrapped;

    public ThreadSafeRandomSourceWrapper(IRandomSource wrapped)
    {
        this.wrapped = wrapped;
        this.lock = new ReentrantLock();
    }

    @Override
    public long NextLong()
    {
        lock.lock();
        try {
            return wrapped.NextLong();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public long GetSeed()
    {
        lock.lock();
        try {
            return wrapped.GetSeed();
        } finally {
            lock.unlock();
        }
    }
}
