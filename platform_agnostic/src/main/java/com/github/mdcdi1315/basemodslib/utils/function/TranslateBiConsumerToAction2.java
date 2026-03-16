package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.Action2;

import java.util.function.BiConsumer;

record TranslateBiConsumerToAction2<T1, T2>(BiConsumer<T1, T2> bi_consumer)
    implements Action2<T1, T2>
{
    @Override
    public void accept(T1 t1, T2 t2) { bi_consumer.accept(t1, t2); }

    @Override
    public void action(T1 obj1, T2 obj2) { bi_consumer.accept(obj1, obj2); }
}
