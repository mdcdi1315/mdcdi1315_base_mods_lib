package com.github.mdcdi1315.basemodslib;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.Action2;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import net.neoforged.bus.api.Event;

class NFGUtils_AddAllEnumerableElementsEventTransformer<T, TEvent extends Event>
        implements Action1<TEvent>
{
    protected IEnumerable<T> enumerable;
    protected Action2<TEvent, T> action;

    protected NFGUtils_AddAllEnumerableElementsEventTransformer(IEnumerable<T> enumerable, Action2<TEvent, T> action)
    {
        this.action = action;
        this.enumerable = enumerable;
    }

    protected void OnIterated(TEvent event) { }

    @Override
    public final void accept(TEvent event) { action(event); }

    @Override
    public final void action(TEvent obj)
    {
        IEnumerator<T> e = enumerable.GetEnumerator();
        try {
            while (e.MoveNext()) { action.action(obj, e.getCurrent()); }
        } finally {
            e.Dispose();
            OnIterated(obj);
        }
    }
}
