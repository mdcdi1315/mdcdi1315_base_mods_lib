package com.github.mdcdi1315.basemodslib.utils.collections.helpers.linkednodes;

import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

import com.github.mdcdi1315.basemodslib.utils.collections.BaseEnumerator;

public class NodeWithNextAndPrevPointerEnumerator<T>
    extends BaseEnumerator<T>
{
    private NodeWithNextAndPrevPointer<T> root, current;

    public NodeWithNextAndPrevPointerEnumerator(@AllowNull NodeWithNextAndPrevPointer<T> root)
    {
        this.root = root;
        this.current = null;
    }

    @Override
    public T getCurrent() { return current.Value; }

    @Override
    protected void ResetImpl() throws InvalidOperationException { current = null; }

    @Override
    protected boolean MoveNextImpl()
            throws InvalidOperationException
    {
        if (current == null) {
            return (current = root) != null;
        } else {
            NodeWithNextAndPrevPointer<T> next_node = current.Next;
            if (next_node == null) {
                return false;
            } else {
                current = next_node;
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
