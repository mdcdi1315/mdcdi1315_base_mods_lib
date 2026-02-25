package com.github.mdcdi1315.basemodslib.integration.clothconfig.errorsuppliers;

import com.github.mdcdi1315.DotNetLayer.System.StringUtils;

import com.github.mdcdi1315.basemodslib.config.lowlevelapi.IConfigFieldConstraint;
import com.github.mdcdi1315.basemodslib.config.lowlevelapi.configfields.ResourceLocationConfigField;

import net.minecraft.network.chat.Component;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public final class ResourceLocationErrorSupplier
    extends AbstractErrorSupplier<String>
{
    private final ResourceLocationConfigField field;

    public ResourceLocationErrorSupplier(ResourceLocationConfigField f) { field = f; }

    @Override
    public Optional<Component> function(String input) {
        try {
            ResourceLocation.parse(input);
        } catch (ResourceLocationException rle) {
            return Optional.of(Component.literal(StringUtils.Format(
                    "Cannot parse value {0} as a resource location: \n{1}",
                    input,
                    rle
            )));
        }
        for (IConfigFieldConstraint<ResourceLocation> ct : field.GetConstraints())
        {
            if (!ct.IsSatisfied(field)) {
                return Optional.of(Component.literal(StringUtils.Format("Constraint failed for field named as {0}", field.GetName())));
            }
        }
        return Optional.empty();
    }
}
