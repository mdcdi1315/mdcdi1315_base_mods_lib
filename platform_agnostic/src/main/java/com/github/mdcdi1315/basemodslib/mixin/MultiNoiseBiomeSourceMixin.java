package com.github.mdcdi1315.basemodslib.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MultiNoiseBiomeSource.class)
public interface MultiNoiseBiomeSourceMixin
{
    @Invoker("parameters")
    Climate.ParameterList<Holder<Biome>> GetParameters();
}
