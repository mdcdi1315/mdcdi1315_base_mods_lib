package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Action1;

import java.util.function.Consumer;

record TranslateConsumerToAction1<T>(Consumer<T> consumer)
    implements Action1<T>
{
    @Override
    public void accept(T t) { consumer.accept(t); }

    @Override
    public void action(T obj) { consumer.accept(obj); }
}
