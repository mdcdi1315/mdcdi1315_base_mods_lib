package com.github.mdcdi1315.basemodslib.integration.clothconfig.errorsuppliers;

import com.github.mdcdi1315.DotNetLayer.System.StringUtils;

import net.minecraft.network.chat.Component;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public final class ResourceLocationErrorSupplier
    extends AbstractErrorSupplier<String>
{
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
        return Optional.empty();
    }
}
