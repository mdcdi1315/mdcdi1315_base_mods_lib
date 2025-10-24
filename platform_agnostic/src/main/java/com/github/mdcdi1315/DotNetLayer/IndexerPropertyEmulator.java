package com.github.mdcdi1315.DotNetLayer;

/**
 * Used to emulate the indexer property feature in .NET for Java.
 * @param <TIndex> The type of the index of the normally expected indexer property.
 * @param <TValue> The type of the value of the normally expected indexer property.
 */
public interface IndexerPropertyEmulator<TIndex , TValue>
{
    TValue getItem(TIndex index);

    void setItem(TIndex index, TValue value);
}
