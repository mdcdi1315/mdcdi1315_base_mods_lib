package com.github.mdcdi1315.basemodslib.utils.collections.helpers.linkednodes;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

public class NodeWithNextPointer<T>
{
    @AllowNull
    public final T Value;
    @AllowNull
    public NodeWithNextPointer<T> Next;

    public NodeWithNextPointer(T value) { this.Value = value; this.Next = null; }

    public NodeWithNextPointer(T value, NodeWithNextPointer<T> next) { this.Value = value; this.Next = next; }
}
