package com.github.mdcdi1315.DotNetLayer.System.Collections;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

/**
 * Provides the {@code abstract} base class for a strongly typed non-generic read-only collection.
 */
public abstract class ReadOnlyCollectionBase
    implements ICollection
{
    private ArrayList field;

    /**
     * Initializes a new instance of the {@link ReadOnlyCollectionBase} class.
     */
    public ReadOnlyCollectionBase() { field = null; }

    /**
     * Gets the list of elements contained in the {@link ReadOnlyCollectionBase} instance.
     * @return An {@link ArrayList} representing the {@link ReadOnlyCollectionBase} instance itself.
     */
    @NotNull
    protected ArrayList GetInnerList()
    {
        if (field == null)
        {
            field = new ArrayList();
        }
        return field;
    }

    @Override
    public int getCount() { return GetInnerList().getCount(); }

    @Override
    public Object getSyncRoot() { return GetInnerList().getSyncRoot(); }

    @Override
    public IEnumerator GetEnumerator() { return GetInnerList().GetEnumerator(); }

    @Override
    public boolean getIsSynchronized() { return GetInnerList().getIsSynchronized(); }

    @Override
    public void CopyTo(Object array, int index) throws ArgumentException { GetInnerList().CopyTo(array, index); }
}
