package com.github.mdcdi1315.basemodslib.utils.collections.helpers.linkednodes;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

public class NodeWithPreviousPointer<T>
{
    @AllowNull
    public final T Value;
    @AllowNull
    public NodeWithPreviousPointer<T> Previous;

    public NodeWithPreviousPointer(T value) { this.Value = value; this.Previous = null; }

    public NodeWithPreviousPointer(T value, NodeWithPreviousPointer<T> previous) { this.Value = value; this.Previous = previous; }
}
