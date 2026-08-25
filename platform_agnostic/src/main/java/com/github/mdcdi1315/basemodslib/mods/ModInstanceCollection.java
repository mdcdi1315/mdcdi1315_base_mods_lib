package com.github.mdcdi1315.basemodslib.mods;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentOutOfRangeException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.StackTraceHidden;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Collections.ObjectModel.KeyedCollection;

import com.github.mdcdi1315.basemodslib.utils.ISynchronized;
import com.github.mdcdi1315.basemodslib.utils.collections.comparers.CharSequenceEqualityComparer;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Provides a base class for the collection retaining
 * any number of BML mod instances. <br />
 * It is thread-safe.
 * @param <T> The type of the mod instance to manage.
 * @since 1.0.37
 */
public abstract class ModInstanceCollection<T extends IModInstance>
    extends KeyedCollection<String, T>
    implements ISynchronized
{
    private boolean frozen;
    private final ReentrantLock lock;

    /**
     * Initializes a new instance of the {@link ModInstanceCollection} class.
     */
    public ModInstanceCollection()
    {
        super(CharSequenceEqualityComparer.SENSITIVE, 10);
        frozen = false;
        lock = new ReentrantLock();
    }

    @Override
    protected final String GetKeyForItem(T instance) { return instance.GetModId(); }

    @StackTraceHidden
    private void VerifyNotFrozen()
    {
        if (frozen) { throw new InvalidOperationException("This mod instance collection is already frozen."); }
    }

    @Override
    protected final void ClearItems()
    {
        VerifyNotFrozen();
        lock.lock();
        try {
            super.ClearItems();
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected final void RemoveItem(int index)
    {
        VerifyNotFrozen();
        lock.lock();
        try {
            super.RemoveItem(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected final void InsertItem(int index, T item)
    {
        VerifyNotFrozen();
        lock.lock();
        try {
            super.InsertItem(index, item);
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected final void SetItem(int index, T item)
    {
        VerifyNotFrozen();
        lock.lock();
        try {
            super.SetItem(index, item);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Freezes the collection, ensuring that no modifications can be applied to it.
     */
    public void Freeze() { frozen = true; }

    @Override
    public final boolean getIsReadOnly() { return frozen; }

    @Override
    public final boolean Contains(T instance)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(instance, "instance");
        lock.lock();
        try {
            return super.Contains(instance);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final int IndexOf(T instance)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(instance, "instance");
        lock.lock();
        try {
            return super.IndexOf(instance);
        } finally {
            lock.unlock();
        }
    }

    @NotNull
    @Override
    public final T getItem(int index)
    {
        lock.lock();
        try {
            return super.getItem(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final boolean Remove(T instance)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(instance, "instance");
        return super.Remove(instance);
    }

    @Override
    public final void Insert(int index, T instance)
    {
        ArgumentNullException.ThrowIfNull(instance, "instance");
        super.Insert(index, instance);
    }

    @Override
    public final void Add(T instance)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(instance, "instance");
        super.Add(instance);
    }

    @Override
    public final void setItem(int index, T instance)
            throws ArgumentNullException, ArgumentOutOfRangeException
    {
        ArgumentNullException.ThrowIfNull(instance, "instance");
        super.setItem(index, instance);
    }
}
