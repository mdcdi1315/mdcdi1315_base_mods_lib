package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Func1;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

public record ModelDefinitionRegistrationInfo(
        ModelLayerLocation location,
        Func1<LayerDefinition> definition
)
{
    public ModelDefinitionRegistrationInfo {
        ArgumentNullException.ThrowIfNull(location, "location");
        ArgumentNullException.ThrowIfNull(definition, "definition");
    }

    public static ModelDefinitionRegistrationInfo CreateTyped(ResourceLocation identifier, String layer, Func1<LayerDefinition> definition)
    {
        ArgumentNullException.ThrowIfNull(layer, "layer");
        ArgumentNullException.ThrowIfNull(identifier, "identifier");
        return new ModelDefinitionRegistrationInfo(new ModelLayerLocation(identifier, layer), definition);
    }
}
