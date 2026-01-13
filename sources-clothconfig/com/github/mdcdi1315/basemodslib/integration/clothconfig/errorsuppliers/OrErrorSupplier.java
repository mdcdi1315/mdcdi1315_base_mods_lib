package com.github.mdcdi1315.basemodslib.integration.clothconfig.errorsuppliers;

import net.minecraft.network.chat.Component;

import java.util.Optional;

class OrErrorSupplier<T>
    extends AbstractErrorSupplier<T>
{
    private final AbstractErrorSupplier<T> one, two;

    public OrErrorSupplier(AbstractErrorSupplier<T> one, AbstractErrorSupplier<T> two)
    {
        this.one = one;
        this.two = two;
    }

    @Override
    public Optional<Component> function(T input)
    {
        Optional<Component> oc1 = one.function(input);
        if (oc1.isPresent()) {
            return oc1;
        } else {
            return two.function(input);
        }
    }
}
