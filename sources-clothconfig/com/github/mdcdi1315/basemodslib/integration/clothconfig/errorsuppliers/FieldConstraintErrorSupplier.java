package com.github.mdcdi1315.basemodslib.integration.clothconfig.errorsuppliers;

import com.github.mdcdi1315.DotNetLayer.System.StringUtils;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.config.lowlevelapi.IConfigField;
import com.github.mdcdi1315.basemodslib.config.lowlevelapi.IConfigFieldConstraint;

import net.minecraft.network.chat.Component;

import java.util.Optional;

public final class FieldConstraintErrorSupplier<TF, T extends IConfigField<TF>>
    extends AbstractErrorSupplier<TF>
{
    private final T field_inst;

    public FieldConstraintErrorSupplier(T field)
    {
        ArgumentNullException.ThrowIfNull(field, "field");
        this.field_inst = field;
    }

    @Override
    public Optional<Component> function(TF input)
    {
        for (IConfigFieldConstraint<TF> c : field_inst.GetConstraints())
        {
            if (!c.IsSatisfied(field_inst)) {
                return Optional.of(Component.literal(StringUtils.Format("Constraint failed for field named as {0}", field_inst.GetName())));
            }
        }
        return Optional.empty();
    }
}
