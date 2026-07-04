package com.github.mdcdi1315.basemodslib.commands;

import com.github.mdcdi1315.DotNetLayer.System.Func2;

import com.mojang.brigadier.arguments.ArgumentType;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;

record ArgumentTypeRegistrationFunction<A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>>(
        Class<A> argument_type_class,
        ArgumentTypeInfo<A, T> info
) implements Func2<ResourceLocation, ArgumentTypeInfo<A, T>>
{
    @Override
    public ArgumentTypeInfo<A, T> function(ResourceLocation input)
    {
        return ArgumentTypeInfos.registerByClass(
                argument_type_class,
                info
        );
    }
}
