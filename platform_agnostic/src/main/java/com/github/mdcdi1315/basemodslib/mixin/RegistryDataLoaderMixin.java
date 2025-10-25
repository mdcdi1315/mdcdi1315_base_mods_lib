package com.github.mdcdi1315.basemodslib.mixin;

import net.minecraft.resources.RegistryDataLoader;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.ArrayList;

@Mixin(RegistryDataLoader.class)
public class RegistryDataLoaderMixin
{
    @Final
    @Shadow
    @Mutable
    public static List<RegistryDataLoader.RegistryData<?>> WORLDGEN_REGISTRIES;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void Initializer(CallbackInfo ci)
    {
        WORLDGEN_REGISTRIES = new ArrayList<>(RegistryDataLoader.WORLDGEN_REGISTRIES);
    }
}
