package com.github.mdcdi1315.basemodslib;

import com.github.mdcdi1315.DotNetLayer.System.Action2;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;

import net.neoforged.bus.api.Event;

final class NFGUtils_AddAllEnumerableElementsEventTransformer_Dispose<T, TEvent extends Event>
        extends NFGUtils_AddAllEnumerableElementsEventTransformer<T, TEvent>
{
    public NFGUtils_AddAllEnumerableElementsEventTransformer_Dispose(IEnumerable<T> enumerable, Action2<TEvent, T> action) { super(enumerable, action); }

    protected void OnIterated(TEvent event)
    {
        action = null;
        enumerable = null;
    }
}
