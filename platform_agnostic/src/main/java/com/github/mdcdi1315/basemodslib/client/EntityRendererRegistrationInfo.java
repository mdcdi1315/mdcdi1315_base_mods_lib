package com.github.mdcdi1315.basemodslib.client;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Func1;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public record EntityRendererRegistrationInfo<T extends Entity>(
        Func1<EntityType<T>> entity_type_provider,
        EntityRendererProvider<? super T> renderer_provider
) {
    public EntityRendererRegistrationInfo {
        ArgumentNullException.ThrowIfNull(entity_type_provider, "entity_type_provider");
        ArgumentNullException.ThrowIfNull(renderer_provider, "renderer_provider");
    }
}
