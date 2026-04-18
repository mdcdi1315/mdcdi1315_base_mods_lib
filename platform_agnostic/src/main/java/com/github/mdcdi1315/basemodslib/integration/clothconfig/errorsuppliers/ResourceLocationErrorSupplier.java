package com.github.mdcdi1315.basemodslib.integration.clothconfig.errorsuppliers;

import com.github.mdcdi1315.DotNetLayer.System.StringUtils;

import com.github.mdcdi1315.basemodslib.registries.RegistryUtils;
import com.github.mdcdi1315.basemodslib.config.reflect.ReflectedConfigFieldData;
import com.github.mdcdi1315.basemodslib.registries.ResourceLocationConstructionException;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class ResourceLocationErrorSupplier
    extends BaseErrorSupplier<String>
{
    public ResourceLocationErrorSupplier(ReflectedConfigFieldData d) { super(d); }

    @Override
    public Optional<Component> function(String input)
    {
        try {
            ResourceLocation parsed = RegistryUtils.ParseResourceLocation(input);
            return VirtualizedFunctionImpl(parsed);
        } catch (ResourceLocationConstructionException construction) {
            return Optional.of(
                    Component.literal(
                            StringUtils.Format(
                                    "Not a valid resource location; Parsing of \"{0}\" failed:\n{1}",
                                    input,
                                    construction.getMessage()
                            )
                    )
            );
        }
    }
}
