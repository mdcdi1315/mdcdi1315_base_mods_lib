package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.basemodslib.utils.collections.IQueue;

public final class WrappedIQueueFromJavaQueue<T>
    extends WrappedICollectionFromJavaCollection<T, java.util.Queue<T>>
    implements IQueue<T>
{
    public WrappedIQueueFromJavaQueue(java.util.Queue<T> collection) { super(collection); }

    @Override
    public T TryPeek() { return GetCollection().peek(); }

    @Override
    public T TryDequeue() { return GetCollection().poll(); }

    @Override
    public void Enqueue(T item) { GetCollection().add(item); }
}
