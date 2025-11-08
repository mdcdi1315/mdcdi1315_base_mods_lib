package com.github.mdcdi1315.basemodslib.entity.memory;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.mojang.serialization.Codec;

import java.util.Optional;

/**
 * Provides a way for registering new memory module types to Minecraft. <br />
 * These are typically get and set through the {@link net.minecraft.world.entity.ai.Brain} class for a given entity.
 * @param optional_codec The codec for de/serializing the memory module data when communicating over network.
 *                       May not be needed to be synced, and can be empty.
 * @param <TMemType> The type of the data to be held by this memory module type.
 */
public record MemoryModuleTypeRegistrationInfo<TMemType>(
       @NotNull Optional<Codec<TMemType>> optional_codec
) {
    /**
     * Constructs a new instance of the {@link MemoryModuleTypeRegistrationInfo} class, passing the codec that de/encodes it's data.
     * @param optional_codec The codec de/encoding the module type data.
     * @throws ArgumentNullException {@code optional_code} is {@code null}.
     */
    public MemoryModuleTypeRegistrationInfo {
        ArgumentNullException.ThrowIfNull(optional_codec , "optional_codec");
    }

    /**
     * Constructs an instance of the {@link MemoryModuleTypeRegistrationInfo} class by specifying an empty codec implementation.
     * @return The constructed {@link MemoryModuleTypeRegistrationInfo} class object.
     * @param <TM> The type of the data to be held by this memory module type.
     */
    public static <TM> MemoryModuleTypeRegistrationInfo<TM> GetEmpty() {
        return new MemoryModuleTypeRegistrationInfo<>(Optional.empty());
    }
}
