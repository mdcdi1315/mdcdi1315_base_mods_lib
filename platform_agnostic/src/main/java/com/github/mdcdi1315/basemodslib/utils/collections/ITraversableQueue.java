package com.github.mdcdi1315.basemodslib.utils.collections;

/**
 * Specialization of the {@link IQueue} interface for collections that can be traversed by an index value.
 * @param <T> The type of the elements that this queue will hold.
 * @since 1.0.18
 */
public interface ITraversableQueue<T>
    extends IQueue<T>, ITraversableCollection<T>
{

}
