package com.github.mdcdi1315.basemodslib.integration.clothconfig.errorsuppliers;

import com.github.mdcdi1315.DotNetLayer.System.Func2;

import net.minecraft.network.chat.Component;

import java.util.Optional;

public abstract class AbstractErrorSupplier<T>
    implements Func2<T, Optional<Component>>
{
    @Override
    public abstract Optional<Component> function(T input);

    @Override
    public final Optional<Component> apply(T t) { return function(t); }
}
