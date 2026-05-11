package com.github.mdcdi1315.basemodslib.utils.collections.linkednodes;

import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerator;

public class NodeWithPreviousPointerEnumerator<T>
    extends BaseEnumerator<T>
{
    private NodeWithPreviousPointer<T> root, current;

    public NodeWithPreviousPointerEnumerator(@AllowNull NodeWithPreviousPointer<T> element) { super(); root = element; current = null; }

    @Override
    public T getCurrent() { return current.Value; }

    @Override
    protected void ResetImpl() throws InvalidOperationException { current = null; }

    @Override
    protected boolean MoveNextImpl()
            throws InvalidOperationException
    {
        if (current == null) {
            // Note - The constructor input accepts a null node - that means that we can receive a root of null, that's why this check here.
            return (current = root) != null;
        } else {
            NodeWithPreviousPointer<T> prev_node = current.Previous;
            if (prev_node == null) {
                return false;
            } else {
                current = prev_node;
                return true;
            }
        }
    }

    @Override
    public void Dispose()
    {
        super.Dispose();
        root = current = null;
    }
}
