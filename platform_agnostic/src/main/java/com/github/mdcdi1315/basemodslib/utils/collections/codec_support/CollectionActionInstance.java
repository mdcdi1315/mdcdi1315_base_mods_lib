package com.github.mdcdi1315.basemodslib.utils.collections.codec_support;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.Action2;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;

record CollectionActionInstance<T, TC extends IEnumerable<T>>(TC collection, Action2<TC, T> add_element)
        implements Action1<T>
{
    @Override
    public void action(T obj) { add_element.action(collection, obj); }
}