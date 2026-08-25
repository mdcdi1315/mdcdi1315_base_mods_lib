package com.github.mdcdi1315.basemodslib.utils.collections.helpers.linkednodes;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

public class NodeWithNextAndPrevPointer<T>
{
    @AllowNull
    public T Value;
    @AllowNull
    public NodeWithNextAndPrevPointer<T> Next, Previous;

    public NodeWithNextAndPrevPointer(T value)
    {
        this.Value = value;
        this.Next = null;
        this.Previous = null;
    }

    public NodeWithNextAndPrevPointer(T value, NodeWithNextAndPrevPointer<T> next, NodeWithNextAndPrevPointer<T> previous)
    {
        this.Next = next;
        this.Value = value;
        this.Previous = previous;
    }
}
