package com.github.mdcdi1315.basemodslib.utils.collections;

/**
 * Specialization of the {@link IStack} interface for collections that can be traversed by an index value.
 * @param <T> The type of the elements that this stack will hold.
 * @since 1.0.18
 */
public interface ITraversableStack<T>
        extends IStack<T>, ITraversableCollection<T>
{
    /**
     * {@inheritDoc}
     */
    @Override
    default T DuplicateLastItem()
    {
        if (GetCount() == 0) {
            return null;
        } else {
            T value = TryPop();
            Push(value);
            Push(value);
            return value;
        }
    }
}
