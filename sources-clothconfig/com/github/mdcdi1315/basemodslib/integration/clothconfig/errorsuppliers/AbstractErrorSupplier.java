package com.github.mdcdi1315.basemodslib.integration.clothconfig.errorsuppliers;

import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import net.minecraft.network.chat.Component;

import java.util.Optional;

public abstract class AbstractErrorSupplier<T>
    implements Func2<T, Optional<Component>>
{
    public static <T> AbstractErrorSupplier<T> Or(AbstractErrorSupplier<T> one, AbstractErrorSupplier<T> two)
    {
        ArgumentNullException.ThrowIfNull(one, "one");
        ArgumentNullException.ThrowIfNull(two, "two");
        return new OrErrorSupplier<>(one, two);
    }

    @Override
    public abstract Optional<Component> function(T input);
}
