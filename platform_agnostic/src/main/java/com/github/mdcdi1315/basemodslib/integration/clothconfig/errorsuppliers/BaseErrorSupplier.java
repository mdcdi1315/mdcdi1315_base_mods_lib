package com.github.mdcdi1315.basemodslib.integration.clothconfig.errorsuppliers;

import com.github.mdcdi1315.DotNetLayer.System.StringUtils;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;

import com.github.mdcdi1315.basemodslib.config.reflect.ReflectedConfigFieldData;
import com.github.mdcdi1315.basemodslib.config.reflect.constraints.IConfigFieldConstraint;

import net.minecraft.network.chat.Component;

import java.util.Optional;

public class BaseErrorSupplier<T>
    extends AbstractErrorSupplier<T>
{
    private final ReflectedConfigFieldData data;

    public BaseErrorSupplier(ReflectedConfigFieldData d) { data = d; }

    protected final Optional<Component> VirtualizedFunctionImpl(Object input)
    {
        IEnumerator<IConfigFieldConstraint> cs = data.GetConstraints().GetEnumerator();
        try {
            while (cs.MoveNext())
            {
                if (!cs.getCurrent().IsSatisfied(input)) {
                    return Optional.of(Component.literal(StringUtils.Format("Constraint failed for field named as {0}", data.GetFieldName())));
                }
            }
        } finally {
            cs.Dispose();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Component> function(T input) { return VirtualizedFunctionImpl(input); }
}
